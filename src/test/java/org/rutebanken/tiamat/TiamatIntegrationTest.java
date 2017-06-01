package org.rutebanken.tiamat;

import com.hazelcast.core.HazelcastInstance;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.rutebanken.tiamat.netex.id.GeneratedIdState;
import org.rutebanken.tiamat.repository.*;
import org.rutebanken.tiamat.versioning.ParkingVersionedSaverService;
import org.rutebanken.tiamat.versioning.StopPlaceVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import static org.rutebanken.tiamat.netex.id.GaplessIdGeneratorService.INITIAL_LAST_ID;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TiamatTestApplication.class)
@ActiveProfiles("geodb")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public abstract class TiamatIntegrationTest {

    private static Logger logger = LoggerFactory.getLogger(TiamatIntegrationTest.class);

    @Autowired
    protected StopPlaceRepository stopPlaceRepository;

    @Autowired
    protected StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    @Autowired
    protected ParkingRepository parkingRepository;

    @Autowired
    protected ParkingVersionedSaverService parkingVersionedSaverService;

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

    @Autowired
    protected TariffZoneRepository tariffZoneRepository;

    @Autowired
    protected HazelcastInstance hazelcastInstance;

    @Autowired
    protected GeneratedIdState generatedIdState;

    @Autowired
    private EntityManagerFactory entityManagerFactory;
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

        parkingRepository.deleteAll();
        parkingRepository.flush();

        tariffZoneRepository.deleteAll();
        tariffZoneRepository.flush();

        clearIdGeneration();
    }

    /**
     * Clear id_generator table and reset available ID queues and last IDs for entities.
     */
    private void clearIdGeneration() {

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        generatedIdState.getRegisteredEntityNames().forEach(entityName -> {
            hazelcastInstance.getQueue(entityName).clear();
            generatedIdState.setLastIdForEntity(entityName, INITIAL_LAST_ID);
            generatedIdState.getClaimedIdListForEntity(entityName).clear();
        });

        int updated = entityManager.createNativeQuery("DELETE FROM id_generator").executeUpdate();
        logger.debug("Cleared id generator table. deleted: {}", updated);
        transaction.commit();
        entityManager.close();
    }
}
