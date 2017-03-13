package org.rutebanken.tiamat;

import com.hazelcast.core.HazelcastInstance;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.rutebanken.tiamat.netex.id.GaplessIdGenerator;
import org.rutebanken.tiamat.netex.id.GeneratedIdState;
import org.rutebanken.tiamat.repository.PathLinkRepository;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.RestDocsTestExecutionListener;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.boot.test.mock.mockito.ResetMocksTestExecutionListener;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextBeforeModesTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TiamatTestApplication.class)
@ActiveProfiles("geodb")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public abstract class CommonSpringBootTest {

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;

    @Autowired
    private PathLinkRepository pathLinkRepository;

    @Autowired
    private QuayRepository quayRepository;

    @Autowired
    private GeneratedIdState generatedIdState;

    @Autowired
    private GaplessIdGenerator gaplessIdGenerator;

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Before
    public void clearRepositories() {
        pathLinkRepository.deleteAll();
        stopPlaceRepository.deleteAll();
        quayRepository.deleteAll();
        topographicPlaceRepository.deleteAll();

        gaplessIdGenerator.getEntityTypeNames().forEach(entityTypeName -> {
            generatedIdState.getClaimedIdQueueForEntity(entityTypeName).clear();
            generatedIdState.getLastIdForEntityMap().put(entityTypeName, 1L);
            generatedIdState.getQueueForEntity(entityTypeName).clear();
            hazelcastInstance.getList("used-h2-ids-by-entity-" + entityTypeName).clear();

        });
    }
}
