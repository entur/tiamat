package org.rutebanken.tiamat.importer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rutebanken.tiamat.TiamatApplication;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test stop place importer with geodb and repository.
 * See also {@link DefaultStopPlaceImporterTest}
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TiamatApplication.class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@TestPropertySource(properties = {
        "stopPlaceFromOriginalIdFinderCache.maxSize = 0",
        "stopPlaceFromOriginalIdFinderCache.maxSize = 0"}
)
@ActiveProfiles("geodb")
@Transactional
public class DefaultStopPlaceImporterWithGeoDBNoCacheTest {

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private QuayRepository quayRepository;

    @Autowired
    private GeometryFactory geometryFactory;

    @Autowired
    private DefaultStopPlaceImporter defaultStopPlaceImporter;

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;


    @Before
    public void cleanRepositories() {
        stopPlaceRepository.deleteAll();
        topographicPlaceRepository.deleteAll();
        quayRepository.deleteAll();
    }

    /**
     * Test is ignored because the striped semaphore has been moved to SiteFrameImporter
     */
    @Ignore
    @Test
    public void multipleThreadsStopPlaceWithQuaysImporter() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        int sameStopImportedCount = 10;
        int eachQuayImportedCount = 10;
        int uniqueQuays = 5;
        AtomicInteger imports = new AtomicInteger();

        // Send stop place without quays

        for (int j = 0; j < uniqueQuays; j++) {
            final String quayName = "QuayName" +j;
            // Add quays to same stop place, but create it again to have fresh references
            Point randomPoint = randomCoordinates();
            executeNTimes(eachQuayImportedCount, executorService, () -> {
                StopPlace stopPlace = createStop();
                Quay quay = new Quay(new EmbeddableMultilingualString(quayName));
                quay.setCentroid(randomPoint);
                stopPlace.getQuays().add(quay);
                try {
                    StopPlace response = defaultStopPlaceImporter.importStopPlaceWithoutNetexMapping(stopPlace, new SiteFrame(), new AtomicInteger());
                    if (response != null) {
                        imports.incrementAndGet();
                    }
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            });
        };

        executeNTimes(sameStopImportedCount, executorService, () -> {
            try {
                StopPlace response = defaultStopPlaceImporter.importStopPlaceWithoutNetexMapping(createStop(), new SiteFrame(), new AtomicInteger());
                if(response != null) {
                    imports.incrementAndGet();
                }

            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.MINUTES);
        assertThat(imports.get()).isEqualTo(sameStopImportedCount + (eachQuayImportedCount*uniqueQuays));
        StopPlace importedStopPlace = defaultStopPlaceImporter.importStopPlaceWithoutNetexMapping(createStop(), new SiteFrame(), new AtomicInteger());
        assertThat(importedStopPlace.getQuays()).hasSize(uniqueQuays)
                .as("Regardless of how many times similar stop with two different quays as imported. We must end up with a stop place with two quays.");
    }


    private final Random random = new Random();
    private final double latitudeMin = 40.0;
    private final double latitudeMax = 90.0;

    private final double longitudeMin = 50.0;
    private final double longitudeMax = 60.0;

    public Point randomCoordinates() {
        double latitude = latitudeMin + (latitudeMax - latitudeMin) * random.nextDouble();
        double longitude = longitudeMin + (longitudeMax - longitudeMin) * random.nextDouble();

        return geometryFactory.createPoint(new Coordinate(latitude, longitude));
    }

    private void executeNTimes(int times, ExecutorService executorService, Runnable runnable) {
        for(int i = 0; i < times; i ++) {
            executorService.submit(runnable);
        }
    }

    private StopPlace createStop() {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Stopp"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10.0393763, 59.750071)));
        return stopPlace;
    }
}
