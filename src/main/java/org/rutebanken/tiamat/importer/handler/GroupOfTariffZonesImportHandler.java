/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.importer.handler;

import com.google.api.client.util.Preconditions;
import org.rutebanken.netex.model.GroupOfTariffZones;
import org.rutebanken.netex.model.GroupsOfTariffZonesInFrame_RelStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.Zone_VersionStructure;
import org.rutebanken.tiamat.config.GroupOfTariffZonesConfig;
import org.rutebanken.tiamat.importer.GroupOfTariffZonesImporter;
import org.rutebanken.tiamat.importer.GroupOfTariffZonesImportResult;
import org.rutebanken.tiamat.importer.ImportParams;
import org.rutebanken.tiamat.importer.ImportType;
import org.rutebanken.tiamat.model.TariffZoneRef;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;
import org.rutebanken.tiamat.repository.FareZoneRepository;
import org.rutebanken.tiamat.versioning.save.GroupOffTariffZonesSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class GroupOfTariffZonesImportHandler {

    private static final Logger logger = LoggerFactory.getLogger(GroupOfTariffZonesImportHandler.class);

    private final PublicationDeliveryHelper publicationDeliveryHelper;

    private final NetexMapper netexMapper;

    private final GroupOfTariffZonesImporter groupOfTariffZonesImporter;

    private final FareZoneRepository fareZoneRepository;

    private final GroupOfTariffZonesConfig groupOfTariffZonesConfig;

    private final GroupOffTariffZonesSaverService groupOffTariffZonesSaverService;


    public GroupOfTariffZonesImportHandler(PublicationDeliveryHelper publicationDeliveryHelper,
                                           NetexMapper netexMapper,
                                           GroupOfTariffZonesImporter groupOfTariffZonesImporter,
                                           FareZoneRepository fareZoneRepository,
                                           GroupOfTariffZonesConfig groupOfTariffZonesConfig,
                                           GroupOffTariffZonesSaverService groupOffTariffZonesSaverService) {
        this.publicationDeliveryHelper = publicationDeliveryHelper;
        this.netexMapper = netexMapper;
        this.groupOfTariffZonesImporter = groupOfTariffZonesImporter;
        this.fareZoneRepository = fareZoneRepository;
        this.groupOfTariffZonesConfig = groupOfTariffZonesConfig;
        this.groupOffTariffZonesSaverService = groupOffTariffZonesSaverService;
    }


    public void handleGroupOfTariffZones(SiteFrame netexSiteFrame, ImportParams importParams, SiteFrame responseSiteframe, Set<String> additionalZoneIds) {

        if (!publicationDeliveryHelper.hasGroupOfTariffZones(netexSiteFrame) || importParams.importType == ImportType.ID_MATCH) {
            return;
        }

        // Zones declared in this SiteFrame (either TariffZone or FareZone instances share the Zone_VersionStructure supertype).
        Set<String> knownZoneIds = new HashSet<>();
        if (publicationDeliveryHelper.hasTariffZones(netexSiteFrame)) {
            netexSiteFrame.getTariffZones().getTariffZone().stream()
                    .map(jaxbElement -> (Zone_VersionStructure) jaxbElement.getValue())
                    .map(Zone_VersionStructure::getId)
                    .forEach(knownZoneIds::add);
        }

        // Zones imported from an accompanying FareFrame in the same delivery.
        if (additionalZoneIds != null) {
            knownZoneIds.addAll(additionalZoneIds);
        }

        List<org.rutebanken.tiamat.model.GroupOfTariffZones> tiamatGroupOfTariffZones = netexSiteFrame.getGroupsOfTariffZones().getGroupOfTariffZones().stream()
                .map(netexMapper::mapToTiamatModel)
                .collect(Collectors.toList());

        // Members not supplied in this delivery may already be persisted (e.g. FareZones imported earlier). Resolve them from the database.
        Set<String> unresolvedRefs = tiamatGroupOfTariffZones.stream()
                .flatMap(group -> group.getMembers().stream())
                .map(TariffZoneRef::getRef)
                .filter(ref -> !knownZoneIds.contains(ref))
                .collect(Collectors.toSet());
        if (!unresolvedRefs.isEmpty()) {
            fareZoneRepository.findValidFareZones(new ArrayList<>(unresolvedRefs))
                    .forEach(fareZone -> knownZoneIds.add(fareZone.getNetexId()));
        }

        // Checks that every referenced zone is resolvable (in this delivery or already persisted).
        validateMembers(tiamatGroupOfTariffZones, knownZoneIds);

        logger.debug("Mapped {} group tariff zones from netex to internal model", tiamatGroupOfTariffZones.size());
        final GroupOfTariffZonesImportResult importResult = groupOfTariffZonesImporter.importGroupOfTariffZones(tiamatGroupOfTariffZones);
        final List<GroupOfTariffZones> importedGroupOfTariffZones = importResult.importedGroupsOfTariffZones();

        logger.debug("Got {} imported group of tariffZones ", importedGroupOfTariffZones.size());

        // With external versioning the import is a full replace: prune groups not present in this delivery.
        if (groupOfTariffZonesConfig.isExternalVersioning() && !importResult.importedNetexIds().isEmpty()) {
            int deletedCount = groupOffTariffZonesSaverService.deleteAllExcept(importResult.importedNetexIds());
            logger.info("External versioning cleanup: deleted {} orphaned GroupOfTariffZones", deletedCount);
        }

        if (!importedGroupOfTariffZones.isEmpty()) {
            responseSiteframe.withGroupsOfTariffZones(new GroupsOfTariffZonesInFrame_RelStructure().withGroupOfTariffZones(importedGroupOfTariffZones));
        }
    }


    private void validateMembers(List<org.rutebanken.tiamat.model.GroupOfTariffZones>  groupOfTariffZones, Set<String> tariffZoneRefs) {
        groupOfTariffZones.forEach(groupOfTariffZone -> groupOfTariffZone.getMembers().forEach(member ->
                Preconditions.checkArgument(tariffZoneRefs.contains(member.getRef()),
                "Member with reference " + member.getRef() + " does not exist when importing group of tariff zones " + groupOfTariffZones)));

    }

}
