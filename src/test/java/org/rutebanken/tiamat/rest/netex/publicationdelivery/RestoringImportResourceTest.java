package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import org.junit.Test;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.TopographicPlace;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.repository.ParkingRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

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

    @Test
    public void restoringImport() throws IOException, InterruptedException, ParserConfigurationException, JAXBException, SAXException, XMLStreamException {


        File file = new File(getClass().getClassLoader().getResource("publication_delivery/initial_import.xml").getFile());

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
        });

        assertThat(parkingRepository.findAll()).as("imported parkings in repository").isNotEmpty();
    }

}