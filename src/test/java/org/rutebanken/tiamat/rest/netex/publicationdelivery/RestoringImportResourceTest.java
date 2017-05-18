package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import org.junit.Test;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.TopographicPlace;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.repository.ParkingRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
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
        assertThat(topographicPlaceRepository.findAllMaxVersion()).isNotEmpty();

        assertThat(stopPlaceRepository.findAll()).isNotEmpty();
        assertThat(parkingRepository.findAll()).isNotEmpty();
    }

}