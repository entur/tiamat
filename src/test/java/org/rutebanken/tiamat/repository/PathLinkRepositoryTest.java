package org.rutebanken.tiamat.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.rutebanken.tiamat.TiamatApplication;
import org.rutebanken.tiamat.model.PathLink;
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

    @Test
    public void simplePersistTest() {

        PathLink pathLink = new PathLink();

        pathLinkRepository.save(pathLink);

        assertThat(pathLink.getId()).describedAs("Path link should get ID when saved").isNotNull();



    }


}