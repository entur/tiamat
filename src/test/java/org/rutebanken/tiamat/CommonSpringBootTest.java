package org.rutebanken.tiamat;

import com.hazelcast.core.HazelcastInstance;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
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
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextBeforeModesTestExecutionListener;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;

import static org.rutebanken.tiamat.netex.id.GaplessIdGeneratorTask.USED_H2_IDS_BY_ENTITY;

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

    @AfterTransaction
    public void cleanUpGaplessIdGenerator() {
//        gaplessIdGenerator.getEntityTypeNames().forEach(entityTypeName -> {
//            generatedIdState.getClaimedIdListForEntity(entityTypeName).clear();
//            generatedIdState.getLastIdForEntityMap().put(entityTypeName, 1L);
//            generatedIdState.getQueueForEntity(entityTypeName).clear();
//            hazelcastInstance.getList(USED_H2_IDS_BY_ENTITY + entityTypeName).clear();
//
//        });
    }
}
