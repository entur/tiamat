package org.rutebanken.tiamat.service;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.junit.Test;
import org.rutebanken.tiamat.CommonSpringBootTest;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.repository.*;
import org.rutebanken.tiamat.versioning.VersionCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class VersionCreatorTest extends CommonSpringBootTest {

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private PathLinkRepository pathLinkRepository;

    @Autowired
    private GeometryFactory geometryFactory;

    @Autowired
    private QuayRepository quayRepository;


    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;

    @Autowired
    private VersionCreator versionCreator;

    @Test
    public void createNewVersionFromExistingStopPlaceAndVerifyTwoPersistedCoexistingStops() {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(1L);
        stopPlace.setName(new EmbeddableMultilingualString("version "));

        Quay quay = new Quay();
        quay.setVersion(1L);

        stopPlace.getQuays().add(quay);

        stopPlace = stopPlaceRepository.save(stopPlace);

        StopPlace newVersion = versionCreator.createNewVersionFrom(stopPlace, StopPlace.class);
        assertThat(newVersion.getVersion()).isEqualTo(2L);

        stopPlaceRepository.save(newVersion);

        StopPlace firstVersion = stopPlaceRepository.findFirstByNetexIdAndVersion(stopPlace.getNetexId(), 1L);
        assertThat(firstVersion).isNotNull();
        StopPlace secondVersion = stopPlaceRepository.findFirstByNetexIdAndVersion(stopPlace.getNetexId(), 2L);
        assertThat(secondVersion).isNotNull();
        assertThat(secondVersion.getQuays()).isNotNull();
        assertThat(secondVersion.getQuays()).hasSize(1);
    }

    @Test
    public void createNewVersionOfStopWithGeometry() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(1L);
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(59.0, 11.1)));
        stopPlace = stopPlaceRepository.save(stopPlace);

        StopPlace newVersion = versionCreator.createNewVersionFrom(stopPlace, StopPlace.class);
        assertThat(newVersion.getCentroid()).isNotNull();
    }

    @Test
    public void createNewVersionOfStopWithZonedDateTime() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(1L);
        stopPlace.setChanged(ZonedDateTime.now());
        stopPlace = stopPlaceRepository.save(stopPlace);
        StopPlace newVersion = versionCreator.createNewVersionFrom(stopPlace, StopPlace.class);
        assertThat(newVersion.getChanged()).isNotNull();
    }

    @Test
    public void createNewVersionOfStopWithTopographicPlace() {

        TopographicPlace topographicPlace = new TopographicPlace();
        topographicPlace.setVersion(1L);
        topographicPlaceRepository.save(topographicPlace);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setTopographicPlace(topographicPlace);
        stopPlace.setVersion(1L);

        stopPlace = stopPlaceRepository.save(stopPlace);

        StopPlace newVersion = versionCreator.createNewVersionFrom(stopPlace, StopPlace.class);

        // Save it. Reference to topographic place should be kept.
        newVersion = stopPlaceRepository.save(newVersion);
    }

    @Test
    public void createNewVersionOfPathLink() {
        Quay fromQuay = new Quay();
        fromQuay.setVersion(1L);
        fromQuay = quayRepository.save(fromQuay);

        Quay toQuay = new Quay();
        toQuay.setVersion(1L);
        toQuay = quayRepository.save(toQuay);

        PathLinkEnd pathLinkEndFromQuay = new PathLinkEnd(fromQuay);
        PathLinkEnd pathLinkEndToQuay = new PathLinkEnd(toQuay);

        PathLink pathLink = new PathLink(pathLinkEndFromQuay, pathLinkEndToQuay);
        pathLink.setVersion(1L);

        pathLink = pathLinkRepository.save(pathLink);

        PathLink newVersion = versionCreator.createNewVersionFrom(pathLink, PathLink.class);

        assertThat(newVersion.getVersion())
                .describedAs("The version of path link should have been incremented")
                .isEqualTo(pathLink.getVersion()+1);

        newVersion = pathLinkRepository.save(newVersion);

        PathLink actualNewVersionPathLink = pathLinkRepository.findFirstByNetexIdOrderByVersionDesc(newVersion.getNetexId());

        assertThat(actualNewVersionPathLink.getVersion()).isEqualTo(2L);
        assertThat(actualNewVersionPathLink.getFrom().getQuay().getNetexId()).isEqualTo(fromQuay.getNetexId());
        assertThat(actualNewVersionPathLink.getTo().getQuay().getNetexId()).isEqualTo(toQuay.getNetexId());

        PathLink actualOldVersionPathLink = pathLinkRepository.findFirstByNetexIdAndVersion(newVersion.getNetexId(), 1L);
        assertThat(actualOldVersionPathLink).isNotNull();
    }

}