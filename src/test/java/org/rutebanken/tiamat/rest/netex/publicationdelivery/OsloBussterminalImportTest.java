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

package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import org.junit.Test;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests related to importing Oslo Bussterminal.
 */
public class OsloBussterminalImportTest extends TiamatIntegrationTest {

    @Autowired
    private PublicationDeliveryTestHelper publicationDeliveryTestHelper;

    /**
     * Oslo bussterminal.
     * Usually, object structures are preferred for type checks and refactoring.
     * This xml is taken from the log when running in carbon. Could be saved in a separate file.
     */
    private static final String OSLO_BUSSTERMINAL_XML = """
            <?OSLO_BUSSTERMINAL_XML version="1.0" encoding="UTF-8"?>
            <PublicationDelivery xmlns="http://www.netex.org.uk/netex" xmlns:ns2="http://www.opengis.net/gml/3.2" xmlns:ns3="http://www.siri.org.uk/siri">
               <PublicationTimestamp>2017-01-26T10:55:00.262+01:00</PublicationTimestamp>
               <ParticipantRef>participantRef</ParticipantRef>
               <Description lang="no" textIdType="">Publication delivery from chouette</Description>
               <dataObjects>
                  <SiteFrame created="2017-01-26T10:55:00.262+01:00" version="1" id="331f175c-3cef-476b-997d-c7282270de2a">
                     <FrameDefaults>
                        <DefaultLocale>
                          <TimeZone>Europe/Oslo</TimeZone>
                          <DefaultLanguage>no</DefaultLanguage>
                       </DefaultLocale>\
                     </FrameDefaults> \
                     <stopPlaces>
                        <StopPlace version="1" id="RUT:StopArea:03010619">
                           <Name lang="no" textIdType="">Oslo Bussterminal</Name>
                           <Centroid>
                              <Location>
                                 <Longitude>10.75761549038076481110692839138209819793701171875</Longitude>
                                 <Latitude>59.91176224246809312035111361183226108551025390625</Latitude>
                              </Location>
                           </Centroid>
                           <StopPlaceType>onstreetBus</StopPlaceType>
                           <quays>
                              <Quay version="1" id="RUT:StopArea:0301061917">
                                 <Name lang="no" textIdType="">Oslo Bussterminal</Name>
                                 <Description>Plattform 17</Description>
                                 <Centroid>
                                    <Location>
                                       <Longitude>10.7600201712276106746912773814983665943145751953125</Longitude>
                                       <Latitude>59.911577472464529137141653336584568023681640625</Latitude>
                                    </Location>
                                 </Centroid>
                                 <CompassBearing>209.0</CompassBearing>
                              </Quay>
                              <Quay version="1" id="RUT:StopArea:0301061930">
                                 <Name lang="no" textIdType="">Oslo Bussterminal</Name>
                                 <Description>avstigning</Description>
                                 <Centroid>
                                    <Location>
                                       <Longitude>10.75761549038076481110692839138209819793701171875</Longitude>
                                       <Latitude>59.91176224246809312035111361183226108551025390625</Latitude>
                                    </Location>
                                 </Centroid>
                                 <CompassBearing>9.0</CompassBearing>
                              </Quay>
                           </quays>
                        </StopPlace>
                     </stopPlaces>
                  </SiteFrame>
               </dataObjects>
            </PublicationDelivery>
            """;

    @Test
    public void extractPlatformCodeFromDescription() throws Exception {

        PublicationDeliveryStructure publicationDeliveryResponse = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(OSLO_BUSSTERMINAL_XML);

        StopPlace actualStopPlace = publicationDeliveryTestHelper.findFirstStopPlace(publicationDeliveryResponse);

        assertThat(actualStopPlace.getName().getContent()).isEqualTo("Oslo Bussterminal");

        List<Quay> actualQuays = publicationDeliveryTestHelper.extractQuays(actualStopPlace);
        assertThat(actualQuays).as("quays should not be null").isNotNull();

        assertThat(actualQuays.stream()
                .filter(quay -> quay.getPublicCode() != null)
                .filter(quay -> quay.getPublicCode().equals("17"))
                .findAny()).describedAs("There should be a quay with matching public code").isPresent();

        assertThat(actualQuays.stream()
                .filter(quay -> quay.getDescription() != null)
                .filter(quay -> quay.getDescription().getContent() != null)
                .filter(quay -> quay.getDescription().getContent().equals("avstigning"))
                .findAny()).describedAs("Quay should contain description").isPresent();
    }
}
