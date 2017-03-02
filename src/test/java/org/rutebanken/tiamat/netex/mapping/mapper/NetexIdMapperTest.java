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
        siteFrame.setNetexId(NetexIdMapper.generateNetexId(siteFrame));

        org.rutebanken.netex.model.SiteFrame netexSiteFrame = new org.rutebanken.netex.model.SiteFrame();
        new NetexIdMapper().toNetexModel(siteFrame, netexSiteFrame);

        assertThat(netexSiteFrame.getId()).isNotEmpty();
        assertThat(netexSiteFrame.getId()).isEqualToIgnoringCase("NSR:SiteFrame:123123");
    }

    @Test
    public void accessibilityAssesmentIdMapping() throws Exception {
        AccessibilityAssessment accessibilityAssessment = new AccessibilityAssessment();
        accessibilityAssessment.setNetexId(NetexIdMapper.generateNetexId(accessibilityAssessment));

        org.rutebanken.netex.model.SiteFrame netexSiteFrame = new org.rutebanken.netex.model.SiteFrame();
        new NetexIdMapper().toNetexModel(accessibilityAssessment, netexSiteFrame);

        assertThat(netexSiteFrame.getId()).isNotEmpty();    
        assertThat(netexSiteFrame.getId()).isEqualToIgnoringCase("NSR:AccessibilityAssessment:123124");
    }


}