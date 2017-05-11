package org.rutebanken.tiamat.netex.mapping.mapper;

import org.junit.Test;
import org.rutebanken.netex.model.KeyListStructure;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.tiamat.model.AccessibilityAssessment;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.SiteFrame;
import org.rutebanken.tiamat.model.StopPlace;

import static org.assertj.core.api.Assertions.assertThat;
import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.ORIGINAL_ID_KEY;

public class NetexIdMapperTest {
    private NetexIdMapper netexIdMapper = new NetexIdMapper();

    @Test
    public void mapSiteFrameIdToNetex() throws Exception {
        SiteFrame siteFrame = new SiteFrame();
        siteFrame.setNetexId("NSR:SiteFrame:123123");

        org.rutebanken.netex.model.SiteFrame netexSiteFrame = new org.rutebanken.netex.model.SiteFrame();
        netexIdMapper.toNetexModel(siteFrame, netexSiteFrame);

        assertThat(netexSiteFrame.getId()).isNotEmpty();
        assertThat(netexSiteFrame.getId()).isEqualToIgnoringCase("NSR:SiteFrame:123123");
    }

    @Test
    public void accessibilityAssesmentIdToNetex() throws Exception {
        AccessibilityAssessment accessibilityAssessment = new AccessibilityAssessment();
        accessibilityAssessment.setNetexId("NSR:AccessibilityAssesment:123124");

        org.rutebanken.netex.model.AccessibilityAssessment netexAccessibilityAssesment = new org.rutebanken.netex.model.AccessibilityAssessment();
        netexIdMapper.toNetexModel(accessibilityAssessment, netexAccessibilityAssesment);

        assertThat(netexAccessibilityAssesment.getId()).isNotEmpty();
        assertThat(netexAccessibilityAssesment.getId()).isEqualToIgnoringCase("NSR:AccessibilityAssesment:123124");
    }

    @Test
    public void copyKeyValuesStripZeroPaddedNumericOriginalId() throws Exception {

        String originalId = "RUT:StopPlace:012345670";

        org.rutebanken.netex.model.DataManagedObjectStructure netexEntity = new org.rutebanken.netex.model.StopPlace()
                .withKeyList(new KeyListStructure()
                        .withKeyValue(new KeyValueStructure()
                                .withKey(ORIGINAL_ID_KEY)
                                .withValue(originalId)));

        StopPlace stopPlace = new StopPlace();

        netexIdMapper.copyKeyValuesToTiamatModel(netexEntity, stopPlace);

        assertThat(stopPlace.getOriginalIds().iterator().next()).isEqualTo("RUT:StopPlace:12345670");
    }

    @Test
    public void copyKeyValuesAvoidEmptyOriginalId() throws Exception {

        String originalId = "RUT:StopPlace:1,,RUT:StopPlace:2";

        org.rutebanken.netex.model.DataManagedObjectStructure netexEntity = new org.rutebanken.netex.model.StopPlace()
                .withKeyList(new KeyListStructure()
                        .withKeyValue(new KeyValueStructure()
                                .withKey(ORIGINAL_ID_KEY)
                                .withValue(originalId)));

        StopPlace stopPlace = new StopPlace();

        netexIdMapper.copyKeyValuesToTiamatModel(netexEntity, stopPlace);

        assertThat(stopPlace.getOriginalIds()).hasSize(2);
    }

    @Test
    public void copyKeyValuesForQuayEmptyPostfixRemove() throws Exception {

        String originalId = "RUT:Quay:";

        org.rutebanken.netex.model.DataManagedObjectStructure netexEntity = new org.rutebanken.netex.model.Quay()
                .withKeyList(new KeyListStructure()
                        .withKeyValue(new KeyValueStructure()
                                .withKey(ORIGINAL_ID_KEY)
                                .withValue(originalId)));

        Quay quay = new Quay();

        netexIdMapper.copyKeyValuesToTiamatModel(netexEntity, quay);

        assertThat(quay.getOriginalIds()).hasSize(0);
    }

    @Test
    public void moveKeyValuesForQuayEmptyPostfixRemove() throws Exception {

        String originalId = "RUT:Quay:";

        Quay quay = new Quay();
        netexIdMapper.moveOriginalIdToKeyValueList(quay, originalId);

        assertThat(quay.getOriginalIds()).hasSize(0);
    }

    @Test
    public void copyKeyValuesForStopEmptyPostfixRemove() throws Exception {

        String originalId = "RUT:StopPlace:";

        org.rutebanken.netex.model.DataManagedObjectStructure netexEntity = new org.rutebanken.netex.model.StopPlace()
                .withKeyList(new KeyListStructure()
                        .withKeyValue(new KeyValueStructure()
                                .withKey(ORIGINAL_ID_KEY)
                                .withValue(originalId)));

        StopPlace stopPlace = new StopPlace();

        netexIdMapper.copyKeyValuesToTiamatModel(netexEntity, stopPlace);

        assertThat(stopPlace.getOriginalIds()).hasSize(0);
    }

    @Test
    public void moveKeyValuesForStopEmptyPostfixRemove() throws Exception {

        String originalId = "RUT:StopPlace:";

        StopPlace stopPlace = new StopPlace();
        netexIdMapper.moveOriginalIdToKeyValueList(stopPlace, originalId);

        assertThat(stopPlace.getOriginalIds()).hasSize(0);
    }
}