package org.rutebanken.tiamat.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.rutebanken.tiamat.TiamatApplication;
import org.rutebanken.tiamat.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TiamatApplication.class)
@ActiveProfiles("geodb")
public class PathLinkRepositoryTest {

    @Autowired
    private PathLinkRepository pathLinkRepository;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private QuayRepository quayRepository;

    @Test
    public void simplePersistTest() {
        PathLink pathLink = new PathLink();
        pathLinkRepository.save(pathLink);
        assertThat(pathLink.getId()).describedAs("Path link should get ID when saved").isNotNull();
    }

    @Test
    public void persistPathLinkWithPathLinkEnd() {
        PathLink pathLink = new PathLink();
        PathLinkEnd from = new PathLinkEnd(createAndSaveStop("A stop place that is referenced to by a path link"));

        pathLink.setFrom(from);

        pathLinkRepository.save(pathLink);

        PathLink actualPathLink = pathLinkRepository.findOne(pathLink.getId());

        assertThat(actualPathLink.getFrom()).isNotNull();
    }

    @Test
    public void pathLinkBetweenQuays() {
        Quay quay1 = new Quay();
        Quay quay2 = new Quay();
        quayRepository.save(quay1);
        quayRepository.save(quay2);


        PathLinkEnd from = new PathLinkEnd(quay1);
        PathLinkEnd to = new PathLinkEnd(quay2);

        PathLink pathLink = new PathLink(from, to);
        pathLinkRepository.save(pathLink);

        PathLink actualPathLink = pathLinkRepository.findOne(pathLink.getId());

        assertThat(actualPathLink.getFrom().getQuay().getId()).isEqualTo(quay1.getId());
        assertThat(actualPathLink.getTo().getQuay().getId()).isEqualTo(quay2.getId());
    }

    @Test
    public void pathLinkWithQuaysAndPathJunction() {
        Quay quay1 = new Quay();
        Quay quay2 = new Quay();
        quayRepository.save(quay1);
        quayRepository.save(quay2);

        PathJunction pathJunction = new PathJunction();

        PathLinkEnd fromQuay = new PathLinkEnd(quay1);
        PathLinkEnd toPathJunction = new PathLinkEnd(pathJunction);
        PathLinkEnd toQuay = new PathLinkEnd(quay2);

        PathLink pathLinkToPathJunction = new PathLink(fromQuay, toPathJunction);
        PathLink pathLinkToQuay = new PathLink(fromQuay, toPathJunction);
        pathLinkRepository.save(pathLinkToPathJunction);
        pathLinkRepository.save(pathLinkToQuay);

        PathLink actualPathLinkToPathJunction = pathLinkRepository.findOne(pathLinkToPathJunction.getId());
        PathLink actualPathLinkToQuay = pathLinkRepository.findOne(pathLinkToQuay.getId());

        assertThat(actualPathLinkToPathJunction.getFrom().getQuay().getId()).isEqualTo(quay1.getId());
        assertThat(actualPathLinkToPathJunction.getTo().getPathJunction().getId()).isEqualTo(pathJunction.getId());

        assertThat(actualPathLinkToQuay.getFrom().getPathJunction().getId()).isEqualTo(pathJunction.getId());
        assertThat(actualPathLinkToQuay.getTo().getQuay().getId()).isEqualTo(toQuay.getId());
    }

    private StopPlace createAndSaveStop(String name) {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(name));
        stopPlaceRepository.save(stopPlace);
        return stopPlace;
    }
}