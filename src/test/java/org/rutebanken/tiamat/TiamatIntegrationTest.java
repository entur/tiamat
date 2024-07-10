/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat;

import com.hazelcast.core.HazelcastInstance;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.locationtech.jts.geom.GeometryFactory;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.id.GeneratedIdState;
import org.rutebanken.tiamat.repository.FareZoneRepository;
import org.rutebanken.tiamat.repository.GroupOfStopPlacesRepository;
import org.rutebanken.tiamat.repository.GroupOfTariffZonesRepository;
import org.rutebanken.tiamat.repository.OrganisationRepository;
import org.rutebanken.tiamat.repository.ParkingRepository;
import org.rutebanken.tiamat.repository.PathJunctionRepository;
import org.rutebanken.tiamat.repository.PathLinkRepository;
import org.rutebanken.tiamat.repository.PurposeOfGroupingRepository;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.TagRepository;
import org.rutebanken.tiamat.repository.TariffZoneRepository;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.rutebanken.tiamat.service.TariffZonesLookupService;
import org.rutebanken.tiamat.service.TopographicPlaceLookupService;
import org.rutebanken.tiamat.versioning.VersionCreator;
import org.rutebanken.tiamat.versioning.save.GroupOfStopPlacesSaverService;
import org.rutebanken.tiamat.versioning.save.ParkingVersionedSaverService;
import org.rutebanken.tiamat.versioning.save.PurposeOfGroupingSaverService;
import org.rutebanken.tiamat.versioning.save.StopPlaceVersionedSaverService;
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
@ActiveProfiles({"test","gcs-blobstore","activemq"})
@EnableAspectJAutoProxy(proxyTargetClass = true)
public abstract class TiamatIntegrationTest {

    private static Logger logger = LoggerFactory.getLogger(TiamatIntegrationTest.class);

    @Autowired
    protected GroupOfStopPlacesRepository groupOfStopPlacesRepository;

    @Autowired
    protected PurposeOfGroupingRepository purposeOfGroupingRepository;

    @Autowired

    protected GroupOfTariffZonesRepository groupOfTariffZonesRepository;

    @Autowired
    protected GroupOfStopPlacesSaverService groupOfStopPlacesSaverService;

    @Autowired
    protected PurposeOfGroupingSaverService purposeOfGroupingSaverService;

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
    protected FareZoneRepository fareZoneRepository;

    @Autowired
    protected OrganisationRepository organisationRepository;

    @Autowired
    protected HazelcastInstance hazelcastInstance;

    @Autowired
    protected GeneratedIdState generatedIdState;

    @Autowired
    protected EntityManagerFactory entityManagerFactory;

    @Autowired
    private TopographicPlaceLookupService topographicPlaceLookupService;

    @Autowired
    private TariffZonesLookupService tariffZonesLookupService;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    protected VersionCreator versionCreator;

    @Value("${local.server.port}")
    protected int port;

    @Before
    @After
    public void clearRepositories() {

        groupOfStopPlacesRepository.flush();
        groupOfStopPlacesRepository.deleteAll();

        purposeOfGroupingRepository.flush();
        purposeOfGroupingRepository.deleteAll();

        stopPlaceRepository.flush();

        pathLinkRepository.deleteAll();
        pathLinkRepository.flush();

        stopPlaceRepository.findAll().stream()
                .filter(StopPlace::isParentStopPlace)
                .forEach(sp -> {
                    stopPlaceRepository.delete(sp);
                });
        stopPlaceRepository.flush();
        stopPlaceRepository.deleteAll();

        stopPlaceRepository.flush();

        quayRepository.deleteAll();
        quayRepository.flush();

        topographicPlaceRepository.deleteAll();
        topographicPlaceRepository.flush();
        topographicPlaceLookupService.reset();

        parkingRepository.deleteAll();
        parkingRepository.flush();

        tariffZoneRepository.deleteAll();
        tariffZoneRepository.flush();
        tariffZonesLookupService.resetTariffZone();


        fareZoneRepository.deleteAll();
        fareZoneRepository.flush();
        tariffZonesLookupService.resetFareZone();

        organisationRepository.deleteAll();
        organisationRepository.flush();

        clearIdGeneration();

        tagRepository.deleteAll();
        tagRepository.flush();
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
