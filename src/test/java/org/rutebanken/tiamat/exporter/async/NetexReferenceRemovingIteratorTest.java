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

package org.rutebanken.tiamat.exporter.async;

import jakarta.xml.bind.JAXBElement;
import org.junit.Test;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.TariffZoneRef;
import org.rutebanken.netex.model.TariffZoneRefs_RelStructure;
import org.rutebanken.netex.model.TopographicPlaceRefStructure;
import org.rutebanken.tiamat.exporter.params.ExportParams;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class NetexReferenceRemovingIteratorTest {


    @Test
    public void testReferenceRemoval() {


        StopPlace stopPlace = new StopPlace()
                .withTariffZones(
                        new TariffZoneRefs_RelStructure()
                                .withTariffZoneRef_(
                                        new ObjectFactory().createTariffZoneRef(
                                            new TariffZoneRef()
                                                .withRef("RUT:TariffZone:1")
                                                .withVersion("1")),
                                        new ObjectFactory().createTariffZoneRef(
                                        new TariffZoneRef()
                                                .withRef("RUT:FareZone:2")
                                                .withVersion("2"))
                                )
                )
                .withTopographicPlaceRef(
                        new TopographicPlaceRefStructure()
                            .withValue("KVE:TopographicPlace:XXX")
                            .withVersion("version"));


        List<StopPlace> stopPlaces = Collections.singletonList(stopPlace);


        ExportParams exportParams = ExportParams.newExportParamsBuilder()
                .setTopographicPlaceExportMode(ExportParams.ExportMode.NONE)
                .setTariffZoneExportMode(ExportParams.ExportMode.NONE)
                .setFareZoneExportMode(ExportParams.ExportMode.NONE)
                .build();

        NetexReferenceRemovingIterator netexReferenceRemovingIterator = new NetexReferenceRemovingIterator(stopPlaces.iterator(), exportParams);


        StopPlace actual = netexReferenceRemovingIterator.next();

        assertThat(actual.getTariffZones().getTariffZoneRef_().stream()
                .map(JAXBElement::getValue)
                .toList()
                .getFirst().getVersion()).as("TariffZoneref version").isNull();
        assertThat(actual.getTariffZones().getTariffZoneRef_().stream()
                .map(JAXBElement::getValue)
                .toList()
                .getLast().getVersion()).as("TariffZoneref version").isNull();
        assertThat(actual.getTopographicPlaceRef().getVersion()).as("topographic place ref version").isNull();
    }

    @Test
    public void testNoReferenceRemoval() {


        StopPlace stopPlace = new StopPlace()
                .withTariffZones(
                        new TariffZoneRefs_RelStructure()
                                .withTariffZoneRef_(new ObjectFactory().createTariffZoneRef(
                                        new TariffZoneRef()
                                                .withRef("ref")
                                                .withVersion("version"))))
                .withTopographicPlaceRef(
                        new TopographicPlaceRefStructure()
                                .withValue("KVE:TopographicPlace:XXX")
                                .withVersion("version"));


        List<StopPlace> stopPlaces = Collections.singletonList(stopPlace);


        ExportParams exportParams = ExportParams.newExportParamsBuilder()
                .setTopographicPlaceExportMode(ExportParams.ExportMode.RELEVANT)
                .setTariffZoneExportMode(ExportParams.ExportMode.RELEVANT)
                .build();

        NetexReferenceRemovingIterator netexReferenceRemovingIterator = new NetexReferenceRemovingIterator(stopPlaces.iterator(), exportParams);

        StopPlace actual = netexReferenceRemovingIterator.next();

        assertThat(actual.getTariffZones().getTariffZoneRef_().stream()
                .map(JAXBElement::getValue)
                .toList()
                .getFirst().getVersion()).as("TariffZoneref version").isEqualTo("version");
        assertThat(actual.getTopographicPlaceRef().getVersion()).as("topographic place ref version").isEqualTo("version");
    }

}