package org.rutebanken.tiamat.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.rutebanken.tiamat.TiamatApplication;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.PathLink;
import org.rutebanken.tiamat.model.PathLinkEnd;
import org.rutebanken.tiamat.model.StopPlace;
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

    @Test
    public void simplePersistTest() {
        PathLink pathLink = new PathLink();
        pathLinkRepository.save(pathLink);
        assertThat(pathLink.getId()).describedAs("Path link should get ID when saved").isNotNull();
    }

    @Test
    public void persistPathLinkWithPathLinkEnd() {
        PathLink pathLink = new PathLink();
        PathLinkEnd from = new PathLinkEnd();
        from.setPlace(createAndSaveStop("A stop place that is references to by a path link"));
        pathLink.setFrom(from);

        pathLinkRepository.save(pathLink);

        PathLink actualPathLink = pathLinkRepository.findOne(pathLink.getId());

        assertThat(actualPathLink.getFrom()).isNotNull();
        assertThat(actualPathLink.getFrom().getPlace()).isInstanceOf(StopPlace.class);
    }

    private StopPlace createAndSaveStop(String name) {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(name));
        stopPlaceRepository.save(stopPlace);
        return stopPlace;
    }
}