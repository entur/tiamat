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

package org.rutebanken.tiamat.exporter;

import jakarta.xml.bind.JAXBElement;
import org.rutebanken.netex.model.FareZone;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.TariffZone;
import org.rutebanken.netex.model.TariffZonesInFrame_RelStructure;
import org.rutebanken.netex.model.Zone_VersionStructure;
import org.rutebanken.tiamat.model.TariffZoneRef;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.reference.ReferenceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@Transactional
public class TariffZonesFromStopsExporter {

    private static final Logger logger = LoggerFactory.getLogger(TariffZonesFromStopsExporter.class);

    @Autowired
    private NetexMapper netexMapper;

    @Autowired
    private ReferenceResolver referenceResolver;

    /**
     * Resolve and add relevant tariffzones to the response site frame.
     * If a tariffzone already exists in the response, it's not added twice.
     *
     * @param importedNetexStopPlaces stop places that were imported, that could reference to tariff zones
     * @param responseSiteFrame the site fra to append or add tariff zones to
     */
    public void resolveTariffZones(Collection<StopPlace> importedNetexStopPlaces, SiteFrame responseSiteFrame) {

        Map<String, JAXBElement<? extends Zone_VersionStructure>> tariffZoneMap = new HashMap<>();

//        TODO
//        if(responseSiteFrame.getTariffZones() != null && responseSiteFrame.getTariffZones().getTariffZone() != null) {
//            responseSiteFrame.getTariffZones().getTariffZone()
//                    .forEach(tariffZone -> tariffZoneMap.put(key(tariffZone.getValue().getId(), tariffZone.getValue().getVersion()), tariffZone));
//        }
//
//        importedNetexStopPlaces.stream()
//                .filter(stopPlace -> stopPlace.getTariffZones() != null)
//                .flatMap(stopPlace -> stopPlace.getTariffZones().getTariffZoneRef().stream())
//                .filter(tariffZoneRef -> !tariffZoneMap.containsKey(key(tariffZoneRef.getRef(), tariffZoneRef.getVersion())))
//                .map(tariffZoneRef -> netexMapper.getFacade().map(tariffZoneRef, TariffZoneRef.class))
//                .peek(mappedTariffZoneRef -> logger.debug("Resolving ref: {}", mappedTariffZoneRef))
//                .map(mappedTariffZoneRef -> {
//                    Object tariffFareZone = referenceResolver.resolve(mappedTariffZoneRef);
//                    if(tariffFareZone instanceof org.rutebanken.tiamat.model.TariffZone) {
//                        return  (org.rutebanken.tiamat.model.TariffZone) tariffFareZone;
//                    }else if(tariffFareZone instanceof  org.rutebanken.tiamat.model.FareZone) {
//                        return (org.rutebanken.tiamat.model.FareZone) tariffFareZone;
//                    } else {
//                        logger.warn("Resolved tariff zone to null from reference: {}", mappedTariffZoneRef);
//                        return null;
//                    }
//                })
//                .filter(Objects::nonNull)
//                .peek(tiamatTariffZone -> logger.debug("Resolved tariffZone: {}", tiamatTariffZone))
//                .map(tiamatTariffZone -> {
//                    if (tiamatTariffZone instanceof org.rutebanken.tiamat.model.TariffZone) {
//                        return netexMapper.getFacade().map(tiamatTariffZone, TariffZone.class);
//                    } else {
//                        return netexMapper.getFacade().map(tiamatTariffZone, FareZone.class);
//                    }
//                })
//                .forEach(tariffZone -> {
//                    if (tariffZone instanceof TariffZone) {
//                        tariffZoneMap.put(key(tariffZone.getId(), tariffZone.getVersion()), new ObjectFactory().createTariffZone((TariffZone) tariffZone));
//                    } else {
//                        tariffZoneMap.put(key(tariffZone.getId(), tariffZone.getVersion()), new ObjectFactory().createFareZone((FareZone) tariffZone));
//                    }
//                });
//
//        if(tariffZoneMap.values().isEmpty()) {
//            logger.info("No relevant tariff zones to return");
//            responseSiteFrame.withTariffZones(null);
//        } else {
//            logger.info("Adding {} tariff zones", tariffZoneMap.values().size());
//            responseSiteFrame.withTariffZones(new TariffZonesInFrame_RelStructure().withTariffZone(tariffZoneMap.values()));
//        }
    }

    private String key(String id, String version) {
        return id + "-" + version;
    }

}
