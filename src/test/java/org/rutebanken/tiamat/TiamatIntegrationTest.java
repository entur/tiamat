package org.rutebanken.tiamat;

import com.vividsolutions.jts.geom.GeometryFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.rutebanken.tiamat.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TiamatTestApplication.class)
@ActiveProfiles("geodb")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public abstract class TiamatIntegrationTest {

    @Autowired
    protected StopPlaceRepository stopPlaceRepository;

    @Autowired
    protected TopographicPlaceRepository topographicPlaceRepository;

    @Autowired
    protected PathLinkRepository pathLinkRepository;

    @Autowired
    protected PathJunctionRepository pathJunctionRepository;

    @Autowired
    protected QuayRepository quayRepository;

    @Autowired
    protected GeometryFactory geometryFactory;

    @Value("${local.server.port}")
    protected int port;

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
