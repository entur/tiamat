package org.rutebanken.tiamat;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.rutebanken.tiamat.repository.PathLinkRepository;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TiamatTestApplication.class)
@ActiveProfiles("geodb")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public abstract class CommonSpringBootTest {

    @Autowired
    protected StopPlaceRepository stopPlaceRepository;

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;

    @Autowired
    private PathLinkRepository pathLinkRepository;

    @Autowired
    protected QuayRepository quayRepository;

    @Before
    @After
    public void clearRepositories() {

        pathLinkRepository.deleteAll();
        pathLinkRepository.flush();

        stopPlaceRepository.deleteAll();
        stopPlaceRepository.flush();

        quayRepository.deleteAll();
        quayRepository.flush();

        topographicPlaceRepository.deleteAll();
        topographicPlaceRepository.flush();
    }
}
