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

import static org.junit.Assert.*;

public class NetexXmlReferenceValidatorTest {
    @Test
    public void validateNetexReferences() throws Exception {

        NetexXmlReferenceValidator netexXmlReferenceValidator = new NetexXmlReferenceValidator(true);

        String xmlShouldBeValidNoVersionsInReferences = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<PublicationDelivery xmlns=\"http://www.netex.org.uk/netex\" xmlns:ns2=\"http://www.opengis.net/gml/3.2\" xmlns:ns3=\"http://www.siri.org.uk/siri\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"1\" xsi:schemaLocation=\"\">\n" +
                "    <PublicationTimestamp>2018-03-08T17:22:02.938</PublicationTimestamp>\n" +
                "    <ParticipantRef>NSR</ParticipantRef>\n" +
                "    <dataObjects>\n" +
                "        <SiteFrame modification=\"new\" version=\"1\" id=\"NSR:SiteFrame:964226245\">\n" +
                "            <Description>Site frame ExportParams{topographicPlaceExportMode=NONE, municipalityReferences=null, countyReferences=null, stopPlaceSearch=StopPlaceSearch{q=null, stopPlaceType=null, submode=null, netexIdList=null, allVersions=false, versionValidity=CURRENT_FUTURE, withouLocationOnly=false, withoutQuaysOnly=false, withDuplicatedQuayImportedIds=false, withTags=null, tags=null, page=0, size=20}, tariffZoneExportMode=NONE}</Description>\n" +
                "            <FrameDefaults>\n" +
                "                <DefaultLocale>\n" +
                "                    <TimeZone>Europe/Oslo</TimeZone>\n" +
                "                </DefaultLocale>\n" +
                "            </FrameDefaults>\n" +
                "            <stopPlaces>\n" +
                "                <StopPlace modification=\"new\" version=\"0\" id=\"NSR:StopPlace:1\">\n" +
                "                    <keyList>\n" +
                "                        <KeyValue>\n" +
                "                            <Key>IS_PARENT_STOP_PLACE</Key>\n" +
                "                            <Value>false</Value>\n" +
                "                        </KeyValue>\n" +
                "                    </keyList>\n" +
                "                    <Name>stop place</Name>\n" +
                "                    <TopographicPlaceRef ref=\"NSR:TopographicPlace:2\" created=\"2018-03-08T17:22:03.028\"/>\n" +
                "                    <AirSubmode>unknown</AirSubmode>\n" +
                "                    <OtherTransportModes></OtherTransportModes>\n" +
                "                    <tariffZones>\n" +
                "                        <TariffZoneRef ref=\"NSR:TariffZone:1\"/>\n" +
                "                    </tariffZones>\n" +
                "                </StopPlace>\n" +
                "            </stopPlaces>\n" +
                "        </SiteFrame>\n" +
                "    </dataObjects>\n" +
                "</PublicationDelivery>";

        netexXmlReferenceValidator.validateNetexReferences(new ByteArrayInputStream(xmlShouldBeValidNoVersionsInReferences.getBytes()), "test");
    }
}