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

package org.rutebanken.tiamat.netex.validation;

import org.junit.Test;

import java.io.ByteArrayInputStream;

public class NetexXmlReferenceValidatorTest {
    @Test
    public void validateNetexReferences() throws Exception {

        NetexXmlReferenceValidator netexXmlReferenceValidator = new NetexXmlReferenceValidator(true);

        String xmlShouldBeValidNoVersionsInReferences = """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <PublicationDelivery xmlns="http://www.netex.org.uk/netex" xmlns:ns2="http://www.opengis.net/gml/3.2" xmlns:ns3="http://www.siri.org.uk/siri" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1" xsi:schemaLocation="">
                    <PublicationTimestamp>2018-03-08T17:22:02.938</PublicationTimestamp>
                    <ParticipantRef>NSR</ParticipantRef>
                    <dataObjects>
                        <SiteFrame modification="new" version="1" id="NSR:SiteFrame:964226245">
                            <Description>Site frame ExportParams{topographicPlaceExportMode=NONE, municipalityReferences=null, countyReferences=null, stopPlaceSearch=StopPlaceSearch{q=null, stopPlaceType=null, submode=null, netexIdList=null, allVersions=false, versionValidity=CURRENT_FUTURE, withouLocationOnly=false, withoutQuaysOnly=false, withDuplicatedQuayImportedIds=false, withTags=null, tags=null, page=0, size=20}, tariffZoneExportMode=NONE}</Description>
                            <FrameDefaults>
                                <DefaultLocale>
                                    <TimeZone>Europe/Oslo</TimeZone>
                                </DefaultLocale>
                            </FrameDefaults>
                            <stopPlaces>
                                <StopPlace modification="new" version="0" id="NSR:StopPlace:1">
                                    <keyList>
                                        <KeyValue>
                                            <Key>IS_PARENT_STOP_PLACE</Key>
                                            <Value>false</Value>
                                        </KeyValue>
                                    </keyList>
                                    <Name>stop place</Name>
                                    <TopographicPlaceRef ref="NSR:TopographicPlace:2" created="2018-03-08T17:22:03.028"/>
                                    <AirSubmode>unknown</AirSubmode>
                                    <OtherTransportModes></OtherTransportModes>
                                    <tariffZones>
                                        <TariffZoneRef ref="NSR:TariffZone:1"/>
                                    </tariffZones>
                                </StopPlace>
                            </stopPlaces>
                        </SiteFrame>
                    </dataObjects>
                </PublicationDelivery>""";

        netexXmlReferenceValidator.validateNetexReferences(new ByteArrayInputStream(xmlShouldBeValidNoVersionsInReferences.getBytes()), "test");
    }
}