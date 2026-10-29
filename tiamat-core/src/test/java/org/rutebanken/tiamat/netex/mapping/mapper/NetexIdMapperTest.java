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

package org.rutebanken.tiamat.netex.mapping.mapper;

import org.junit.Test;
import org.entur.netex2.model.KeyListStructure;
import org.entur.netex2.model.KeyValueStructure;
import org.rutebanken.tiamat.model.AccessibilityAssessment;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.SiteFrame;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.netex.id.ValidPrefixList;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.ORIGINAL_ID_KEY;

public class NetexIdMapperTest {

    private ValidPrefixList validPrefixList = new ValidPrefixList("NSR", new HashMap<>());
    private NetexIdHelper netexIdHelper = new NetexIdHelper(validPrefixList);
    private NetexIdMapper netexIdMapper = new NetexIdMapper(validPrefixList, netexIdHelper);

    @Test
    public void mapSiteFrameIdToNetex() throws Exception {
        SiteFrame siteFrame = new SiteFrame();
        siteFrame.setNetexId("NSR:SiteFrame:123123");

        org.entur.netex2.model.SiteFrame netexSiteFrame = new org.entur.netex2.model.SiteFrame();
        netexIdMapper.toNetexModel(siteFrame, netexSiteFrame);

        assertThat(netexSiteFrame.getId()).isNotEmpty();
        assertThat(netexSiteFrame.getId()).isEqualToIgnoringCase("NSR:SiteFrame:123123");
    }

    @Test
    public void accessibilityAssesmentIdToNetex() throws Exception {
        AccessibilityAssessment accessibilityAssessment = new AccessibilityAssessment();
        accessibilityAssessment.setNetexId("NSR:AccessibilityAssesment:123124");

        org.entur.netex2.model.AccessibilityAssessment netexAccessibilityAssesment = new org.entur.netex2.model.AccessibilityAssessment();
        netexIdMapper.toNetexModel(accessibilityAssessment, netexAccessibilityAssesment);

        assertThat(netexAccessibilityAssesment.getId()).isNotEmpty();
        assertThat(netexAccessibilityAssesment.getId()).isEqualToIgnoringCase("NSR:AccessibilityAssesment:123124");
    }

    @Test
    public void copyKeyValuesAvoidEmptyOriginalId() throws Exception {

        String originalId = "RUT:StopPlace:1,,RUT:StopPlace:2";

        org.entur.netex2.model.DataManagedObjectStructure netexEntity = new org.entur.netex2.model.StopPlace()
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

        org.entur.netex2.model.DataManagedObjectStructure netexEntity = new org.entur.netex2.model.Quay()
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

        org.entur.netex2.model.DataManagedObjectStructure netexEntity = new org.entur.netex2.model.StopPlace()
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