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
import org.rutebanken.netex.model.CompositeFrame;
import org.rutebanken.netex.model.Frames_RelStructure;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PublicationDeliveryImporterTest extends TiamatIntegrationTest {

    @Autowired
    private PublicationDeliveryHelper publicationDeliveryHelper;

    @SuppressWarnings("unchecked")
    @Test
    public void findSiteFrameFromCompositeFrame() {
        ObjectFactory objectFactory = new ObjectFactory();

        PublicationDeliveryStructure publicationDeliveryStructure = new PublicationDeliveryStructure()
                .withDataObjects(
                        new PublicationDeliveryStructure.DataObjects()
                                .withCompositeFrameOrCommonFrame(
                                        objectFactory.createCompositeFrame(
                                                new CompositeFrame()
                                                        .withFrames(new Frames_RelStructure()
                                                            .withCommonFrame(objectFactory.createCommonFrame(new SiteFrame()))))));

        SiteFrame siteFrame = publicationDeliveryHelper.findSiteFrame(publicationDeliveryStructure);
        assertThat(siteFrame).isNotNull();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findSiteFrameFromCommonFrame() {
        ObjectFactory objectFactory = new ObjectFactory();

        PublicationDeliveryStructure publicationDeliveryStructure = new PublicationDeliveryStructure()
                .withDataObjects(
                        new PublicationDeliveryStructure.DataObjects()
                                .withCompositeFrameOrCommonFrame(
                                        objectFactory.createCommonFrame(new SiteFrame())));

        SiteFrame siteFrame = publicationDeliveryHelper.findSiteFrame(publicationDeliveryStructure);
        assertThat(siteFrame).isNotNull();
    }

}