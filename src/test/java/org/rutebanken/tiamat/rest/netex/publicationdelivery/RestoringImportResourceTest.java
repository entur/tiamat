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
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TariffZone;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.repository.*;
import org.rutebanken.tiamat.repository.reference.ReferenceResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class RestoringImportResourceTest extends TiamatIntegrationTest {

    @Autowired
    private RestoringImportResource restoringImportResource;

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private ParkingRepository parkingRepository;

    @Autowired
    private PathLinkRepository pathLinkRepository;

    @Autowired
    private ReferenceResolver referenceResolver;

    @Test
    public void restoringImport() throws IOException, InterruptedException, ParserConfigurationException, JAXBException, SAXException, XMLStreamException {


        File file = new File(getClass().getClassLoader().getResource("publication_delivery/restoring_import.xml").getFile());

        assertThat(topographicPlaceRepository.findAll()).isEmpty();

        restoringImportResource.importPublicationDeliveryOnEmptyDatabase(new FileInputStream(file));
        List<org.rutebanken.tiamat.model.TopographicPlace> allMaxVersion = topographicPlaceRepository.findAllMaxVersion();
        assertThat(allMaxVersion).isNotEmpty();
        assertThat(allMaxVersion).hasSize(2);
        assertThat(allMaxVersion)
                .extracting(org.rutebanken.tiamat.model.TopographicPlace::getNetexId)
                .contains("KVE:TopographicPlace:20", "KVE:TopographicPlace:2002");

        List<StopPlace> importedStops = stopPlaceRepository.findAll();
        assertThat(importedStops).as("imported stops in repository").isNotEmpty();


        importedStops.forEach(stopPlace -> {
            org.rutebanken.tiamat.model.TopographicPlace topographicPlace = stopPlace.getTopographicPlace();
            assertThat(topographicPlace).as("stop place's topographic place "+stopPlace.getNetexId()).isNotNull();
            assertThat(NetexIdHelper.isNetexId(topographicPlace.getNetexId())).as("Topographic place has valid netexID").isTrue();

            Stream.of(stopPlace.getTariffZones())
                    .filter(Objects::nonNull)
                    .flatMap(tariffZoneRefs -> tariffZoneRefs.stream())
                    .forEach(tariffZoneRefs -> {
                        TariffZone tariffZone = referenceResolver.resolve(tariffZoneRefs);
                        assertThat(tariffZone).as("resolved tariff zone from imported stop place").isNotNull();
                    });

        });

        assertThat(parkingRepository.findAll()).as("imported parkings in repository").isNotEmpty();

        assertThat(tariffZoneRepository.findAll()).as("tariff zones in repository").isNotEmpty();

        assertThat(pathLinkRepository.findAll()).as("path links in repository)").isNotEmpty();
    }

}