package org.rutebanken.tiamat.netex.mapping.mapper;

import org.junit.Test;
import org.rutebanken.tiamat.model.AccessibilityAssessment;
import org.rutebanken.tiamat.model.SiteFrame;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;

import static org.assertj.core.api.Assertions.assertThat;

public class NetexIdMapperTest {
    @Test
    public void siteFrameIdMapping() throws Exception {
        SiteFrame siteFrame = new SiteFrame();
        siteFrame.setNetexId("NSR:SiteFrame:123123");

        org.rutebanken.netex.model.SiteFrame netexSiteFrame = new org.rutebanken.netex.model.SiteFrame();
        new NetexIdMapper().toNetexModel(siteFrame, netexSiteFrame);

        assertThat(netexSiteFrame.getId()).isNotEmpty();
        assertThat(netexSiteFrame.getId()).isEqualToIgnoringCase("NSR:SiteFrame:123123");
    }

    @Test
    public void accessibilityAssesmentIdMapping() throws Exception {
        AccessibilityAssessment accessibilityAssessment = new AccessibilityAssessment();
        accessibilityAssessment.setNetexId("NSR:AccessibilityAssesment:123124");

        org.rutebanken.netex.model.AccessibilityAssessment netexAccessibilityAssesment = new org.rutebanken.netex.model.AccessibilityAssessment();
        new NetexIdMapper().toNetexModel(accessibilityAssessment, netexAccessibilityAssesment);

        assertThat(netexAccessibilityAssesment.getId()).isNotEmpty();
        assertThat(netexAccessibilityAssesment.getId()).isEqualToIgnoringCase("NSR:AccessibilityAssesment:123124");
    }


}