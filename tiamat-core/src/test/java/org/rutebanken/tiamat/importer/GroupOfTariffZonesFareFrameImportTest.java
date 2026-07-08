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

package org.rutebanken.tiamat.importer;

import org.junit.Test;
import org.rutebanken.netex.model.FareFrame;
import org.rutebanken.netex.model.FareZone;
import org.rutebanken.netex.model.FareZonesInFrame_RelStructure;
import org.rutebanken.netex.model.GroupsOfTariffZonesInFrame_RelStructure;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.TariffZone;
import org.rutebanken.netex.model.TariffZoneRef;
import org.rutebanken.netex.model.TariffZoneRefs_RelStructure;
import org.rutebanken.netex.model.TariffZonesInFrame_RelStructure;
import org.rutebanken.netex.model.ValidBetween;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.config.FareZoneConfig;
import org.rutebanken.tiamat.config.GroupOfTariffZonesConfig;
import org.rutebanken.tiamat.config.TariffZoneConfig;
import org.rutebanken.tiamat.model.GroupOfTariffZones;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.PublicationDeliveryTestHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for the Nordic NeTEx profile layout where FareZones are delivered in a FareFrame
 * while the GroupOfTariffZones referencing them lives in the SiteFrame of the same publication delivery.
 *
 * @see <a href="https://github.com/entur/profile-examples/blob/master/netex/fares-sales/FareZones.xml">Entur profile example</a>
 */
@Transactional
public class GroupOfTariffZonesFareFrameImportTest extends TiamatIntegrationTest {

    @Autowired
    private PublicationDeliveryTestHelper publicationDeliveryTestHelper;

    @Autowired
    private PublicationDeliveryImporter publicationDeliveryImporter;

    @Autowired
    private FareZoneConfig fareZoneConfig;

    @Autowired
    private GroupOfTariffZonesConfig groupOfTariffZonesConfig;

    @Autowired
    private TariffZoneConfig tariffZoneConfig;

    private final ObjectFactory objectFactory = new ObjectFactory();

    @Test
    public void importGroupOfTariffZonesReferencingFareZonesInFareFrame() {
        // GIVEN: a delivery where the FareFrame holds the FareZones and the SiteFrame holds a group referencing them
        FareFrame fareFrame = fareFrameWithFareZones("OST:FareZone:1", "OST:FareZone:2");

        SiteFrame siteFrame = publicationDeliveryTestHelper.siteFrame();
        siteFrame.withGroupsOfTariffZones(new GroupsOfTariffZonesInFrame_RelStructure()
                .withGroupOfTariffZones(groupOfTariffZones("OST:GroupOfTariffZones:1", "OST:FareZone:1", "OST:FareZone:2")));

        PublicationDeliveryStructure delivery = publicationDeliveryTestHelper.publicationDelivery(siteFrame, fareFrame);

        ImportParams importParams = new ImportParams();
        importParams.importType = ImportType.INITIAL;

        // WHEN: imported through the default (SiteFrame) importer
        PublicationDeliveryStructure response = publicationDeliveryImporter.importPublicationDelivery(delivery, importParams);

        // THEN: both fare zones and the group with its resolved members are persisted
        assertThat(response).isNotNull();
        assertThat(fareZoneRepository.findByNetexId("OST:FareZone:1")).isNotEmpty();
        assertThat(fareZoneRepository.findByNetexId("OST:FareZone:2")).isNotEmpty();

        List<GroupOfTariffZones> groups = groupOfTariffZonesRepository.findAll();
        assertThat(groups).as("Imported group of tariff zones").hasSize(1);
        assertThat(groups.getFirst().getMembers())
                .extracting(member -> member.getRef())
                .containsExactlyInAnyOrder("OST:FareZone:1", "OST:FareZone:2");
    }

    @Test
    public void importGroupResolvesMemberAgainstPreviouslyPersistedFareZone() {
        // GIVEN: a first delivery that persists a FareZone (FareFrame alongside an empty SiteFrame)
        PublicationDeliveryStructure firstDelivery = publicationDeliveryTestHelper.publicationDelivery(
                publicationDeliveryTestHelper.siteFrame(),
                fareFrameWithFareZones("OST:FareZone:99"));

        ImportParams importParams = new ImportParams();
        importParams.importType = ImportType.INITIAL;
        publicationDeliveryImporter.importPublicationDelivery(firstDelivery, importParams);

        // WHEN: a later delivery contains only the group (no FareFrame), referencing the persisted zone
        SiteFrame siteFrame = publicationDeliveryTestHelper.siteFrame();
        siteFrame.withGroupsOfTariffZones(new GroupsOfTariffZonesInFrame_RelStructure()
                .withGroupOfTariffZones(groupOfTariffZones("OST:GroupOfTariffZones:2", "OST:FareZone:99")));

        publicationDeliveryImporter.importPublicationDelivery(publicationDeliveryTestHelper.publicationDelivery(siteFrame), importParams);

        // THEN: the member is resolved from the database and the group is persisted
        List<GroupOfTariffZones> groups = groupOfTariffZonesRepository.findAll();
        assertThat(groups).hasSize(1);
        assertThat(groups.getFirst().getMembers())
                .extracting(member -> member.getRef())
                .containsExactly("OST:FareZone:99");
    }

    @Test
    public void importGroupFailsWhenMemberZoneIsUnknown() {
        // GIVEN: a group referencing a zone that is neither in the delivery nor persisted
        SiteFrame siteFrame = publicationDeliveryTestHelper.siteFrame();
        siteFrame.withGroupsOfTariffZones(new GroupsOfTariffZonesInFrame_RelStructure()
                .withGroupOfTariffZones(groupOfTariffZones("OST:GroupOfTariffZones:3", "OST:FareZone:doesNotExist")));

        PublicationDeliveryStructure delivery = publicationDeliveryTestHelper.publicationDelivery(siteFrame, fareFrameWithFareZones("OST:FareZone:1"));

        ImportParams importParams = new ImportParams();
        importParams.importType = ImportType.INITIAL;

        // WHEN / THEN: import is rejected with a clear message
        assertThatThrownBy(() -> publicationDeliveryImporter.importPublicationDelivery(delivery, importParams))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("OST:FareZone:doesNotExist");
    }

    @Test
    public void externalVersioningPrunesOrphanedFareZonesInCombinedPath() {
        ImportParams importParams = new ImportParams();
        importParams.importType = ImportType.INITIAL;

        // GIVEN: a first delivery persists two fare zones
        publicationDeliveryImporter.importPublicationDelivery(
                publicationDeliveryTestHelper.publicationDelivery(publicationDeliveryTestHelper.siteFrame(),
                        fareFrameWithFareZones("OST:FareZone:keep", "OST:FareZone:orphan")),
                importParams);

        ReflectionTestUtils.setField(fareZoneConfig, "externalVersioning", true);
        try {
            // WHEN: a later combined delivery only contains one of them, plus a group referencing it
            SiteFrame siteFrame = publicationDeliveryTestHelper.siteFrame();
            siteFrame.withGroupsOfTariffZones(new GroupsOfTariffZonesInFrame_RelStructure()
                    .withGroupOfTariffZones(groupOfTariffZones("OST:GroupOfTariffZones:10", "OST:FareZone:keep")));

            publicationDeliveryImporter.importPublicationDelivery(
                    publicationDeliveryTestHelper.publicationDelivery(siteFrame, fareFrameWithFareZones("OST:FareZone:keep")),
                    importParams);

            // THEN: the orphaned zone is removed, the referenced zone survives, and the group is imported
            assertThat(fareZoneRepository.findByNetexId("OST:FareZone:keep")).isNotEmpty();
            assertThat(fareZoneRepository.findByNetexId("OST:FareZone:orphan")).isEmpty();
            assertThat(groupOfTariffZonesRepository.findAll()).hasSize(1);
        } finally {
            ReflectionTestUtils.setField(fareZoneConfig, "externalVersioning", false);
        }
    }

    @Test
    public void externalVersioningKeepsSingleVersionUsingIncomingVersionNumber() {
        ImportParams importParams = new ImportParams();
        importParams.importType = ImportType.INITIAL;

        ReflectionTestUtils.setField(groupOfTariffZonesConfig, "externalVersioning", true);
        try {
            // GIVEN: group version 1 imported
            importCombined(importParams, "OST:FareZone:1",
                    groupVersioned("OST:GroupOfTariffZones:1", "3", "OST:FareZone:1"));

            // WHEN: the same group is re-imported with the externally supplied version 7
            importCombined(importParams, "OST:FareZone:1",
                    groupVersioned("OST:GroupOfTariffZones:1", "7", "OST:FareZone:1"));

            // THEN: only one version is kept and its version number is the incoming one
            List<GroupOfTariffZones> groups = groupOfTariffZonesRepository.findByNetexId("OST:GroupOfTariffZones:1");
            assertThat(groups).hasSize(1);
            assertThat(groups.getFirst().getVersion()).isEqualTo(7L);
            assertThat(groupOfTariffZonesRepository.findAll()).hasSize(1);
        } finally {
            ReflectionTestUtils.setField(groupOfTariffZonesConfig, "externalVersioning", false);
        }
    }

    @Test
    public void externalVersioningPrunesOrphanedGroups() {
        ImportParams importParams = new ImportParams();
        importParams.importType = ImportType.INITIAL;

        ReflectionTestUtils.setField(groupOfTariffZonesConfig, "externalVersioning", true);
        try {
            // GIVEN: two groups imported
            SiteFrame first = publicationDeliveryTestHelper.siteFrame();
            first.withGroupsOfTariffZones(new GroupsOfTariffZonesInFrame_RelStructure().withGroupOfTariffZones(
                    groupOfTariffZones("OST:GroupOfTariffZones:1", "OST:FareZone:1"),
                    groupOfTariffZones("OST:GroupOfTariffZones:2", "OST:FareZone:2")));
            publicationDeliveryImporter.importPublicationDelivery(
                    publicationDeliveryTestHelper.publicationDelivery(first, fareFrameWithFareZones("OST:FareZone:1", "OST:FareZone:2")), importParams);
            assertThat(groupOfTariffZonesRepository.findAll()).hasSize(2);

            // WHEN: a later delivery only contains the first group
            SiteFrame second = publicationDeliveryTestHelper.siteFrame();
            second.withGroupsOfTariffZones(new GroupsOfTariffZonesInFrame_RelStructure().withGroupOfTariffZones(
                    groupOfTariffZones("OST:GroupOfTariffZones:1", "OST:FareZone:1")));
            publicationDeliveryImporter.importPublicationDelivery(
                    publicationDeliveryTestHelper.publicationDelivery(second, fareFrameWithFareZones("OST:FareZone:1")), importParams);

            // THEN: the orphaned group is removed, the present one survives
            List<GroupOfTariffZones> groups = groupOfTariffZonesRepository.findAll();
            assertThat(groups).hasSize(1);
            assertThat(groups.getFirst().getMembers())
                    .extracting(member -> member.getRef())
                    .containsExactly("OST:FareZone:1");
        } finally {
            ReflectionTestUtils.setField(groupOfTariffZonesConfig, "externalVersioning", false);
        }
    }

    @Test
    public void ignoreTariffZoneImportSkipsTariffZonesButKeepsFareZonesAndGroups() {
        ImportParams importParams = new ImportParams();
        importParams.importType = ImportType.INITIAL;

        ReflectionTestUtils.setField(tariffZoneConfig, "ignoreImport", true);
        try {
            // GIVEN: a SiteFrame with a deprecated TariffZone + a group referencing a FareZone, and a FareFrame with that FareZone
            SiteFrame siteFrame = publicationDeliveryTestHelper.siteFrame();
            siteFrame.withTariffZones(new TariffZonesInFrame_RelStructure()
                    .withTariffZone(objectFactory.createTariffZone(new TariffZone()
                            .withId("OST:TariffZone:1")
                            .withVersion("1")
                            .withName(new MultilingualString().withValue("Deprecated zone")))));
            siteFrame.withGroupsOfTariffZones(new GroupsOfTariffZonesInFrame_RelStructure()
                    .withGroupOfTariffZones(groupOfTariffZones("OST:GroupOfTariffZones:1", "OST:FareZone:1")));

            publicationDeliveryImporter.importPublicationDelivery(
                    publicationDeliveryTestHelper.publicationDelivery(siteFrame, fareFrameWithFareZones("OST:FareZone:1")), importParams);

            // THEN: the TariffZone is ignored, but the FareZone and the group are imported
            assertThat(tariffZoneRepository.findByNetexId("OST:TariffZone:1")).isEmpty();
            assertThat(fareZoneRepository.findByNetexId("OST:FareZone:1")).isNotEmpty();
            assertThat(groupOfTariffZonesRepository.findAll()).hasSize(1);
        } finally {
            ReflectionTestUtils.setField(tariffZoneConfig, "ignoreImport", false);
        }
    }

    private void importCombined(ImportParams importParams, String fareZoneId, org.rutebanken.netex.model.GroupOfTariffZones group) {
        SiteFrame siteFrame = publicationDeliveryTestHelper.siteFrame();
        siteFrame.withGroupsOfTariffZones(new GroupsOfTariffZonesInFrame_RelStructure().withGroupOfTariffZones(group));
        publicationDeliveryImporter.importPublicationDelivery(
                publicationDeliveryTestHelper.publicationDelivery(siteFrame, fareFrameWithFareZones(fareZoneId)), importParams);
    }

    private org.rutebanken.netex.model.GroupOfTariffZones groupVersioned(String groupId, String version, String... memberRefs) {
        return groupOfTariffZones(groupId, memberRefs).withVersion(version);
    }

    private FareFrame fareFrameWithFareZones(String... fareZoneIds) {
        LocalDateTime validFrom = LocalDateTime.now().minusDays(3);
        FareFrame fareFrame = publicationDeliveryTestHelper.fareFrame();
        fareFrame.setFareZones(new FareZonesInFrame_RelStructure());
        for (String id : fareZoneIds) {
            fareFrame.getFareZones().getFareZone().add(new FareZone()
                    .withId(id)
                    .withVersion("1")
                    .withName(new MultilingualString().withValue(id))
                    .withValidBetween(new ValidBetween().withFromDate(validFrom)));
        }
        return fareFrame;
    }

    private org.rutebanken.netex.model.GroupOfTariffZones groupOfTariffZones(String groupId, String... memberRefs) {
        TariffZoneRefs_RelStructure members = new TariffZoneRefs_RelStructure();
        for (String ref : memberRefs) {
            members.getTariffZoneRef_().add(objectFactory.createTariffZoneRef(new TariffZoneRef().withRef(ref)));
        }
        return new org.rutebanken.netex.model.GroupOfTariffZones()
                .withId(groupId)
                .withVersion("1")
                .withName(new MultilingualString().withValue(groupId))
                .withMembers(members);
    }
}
