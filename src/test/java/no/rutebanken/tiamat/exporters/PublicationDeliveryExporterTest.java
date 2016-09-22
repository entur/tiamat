package no.rutebanken.tiamat.exporters;

import no.rutebanken.netex.model.PublicationDeliveryStructure;
import no.rutebanken.netex.model.SiteFrame;
import no.rutebanken.netex.model.StopPlace;
import no.rutebanken.tiamat.TiamatApplication;
import no.rutebanken.tiamat.repository.StopPlaceRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TiamatApplication.class)
@ActiveProfiles("geodb")
public class PublicationDeliveryExporterTest {

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private PublicationDeliveryExporter publicationDeliveryExporter;

    @Test
    public void exportPublicationDeliveryWithStopPlace() throws JAXBException {
        no.rutebanken.tiamat.model.StopPlace stopPlace = new no.rutebanken.tiamat.model.StopPlace();
        stopPlaceRepository.save(stopPlace);

        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryExporter.exportAllStopPlaces();

        StopPlace actual = findStopPlace(publicationDeliveryStructure, stopPlace.getId());
        assertThat(actual.getId()).isEqualTo(stopPlace.getId());
    }

    private StopPlace findStopPlace(PublicationDeliveryStructure publicationDeliveryStructure, String stopPlaceId) {
        return publicationDeliveryStructure.getDataObjects()
                .getCompositeFrameOrCommonFrame()
                .stream()
                .map(JAXBElement::getValue)
                .filter(commonVersionFrameStructure -> commonVersionFrameStructure instanceof SiteFrame)
                .flatMap(commonVersionFrameStructure -> ((SiteFrame) commonVersionFrameStructure).getStopPlaces().getStopPlace().stream())
                .filter(stopPlace -> stopPlace.getId().equals(stopPlaceId))
                .findFirst().get();
    }


}