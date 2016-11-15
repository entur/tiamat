package org.rutebanken.tiamat.repository;

import org.rutebanken.tiamat.TiamatApplication;
import org.rutebanken.tiamat.model.MultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TiamatApplication.class)
@ActiveProfiles("geodb")
public class StopPlaceRepositoryTest {

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Test
    public void findStopPlacesSortedCorrectly() {
        StopPlace stopPlaceOlder = new StopPlace();
        StopPlace stopPlaceNewer = new StopPlace();

        stopPlaceOlder.setChanged(ZonedDateTime.ofInstant(Instant.ofEpochMilli(50), ZoneId.systemDefault()));
        stopPlaceNewer.setChanged(ZonedDateTime.ofInstant(Instant.ofEpochMilli(100), ZoneId.systemDefault()));

        stopPlaceRepository.save(stopPlaceNewer);
        stopPlaceRepository.save(stopPlaceOlder);

        Pageable pageable = new PageRequest(0, 2);
        Page<StopPlace> page = stopPlaceRepository.findAllByOrderByChangedDesc(pageable);

        assertThat(page.getContent().get(0).getChanged()).isEqualTo(stopPlaceNewer.getChanged());
        assertThat(page.getContent().get(1).getChanged()).isEqualTo(stopPlaceOlder.getChanged());
    }

    @Test
    public void findStopPlacesByNameSortedCorrectly() {
        StopPlace stopPlaceOlder = new StopPlace();
        StopPlace stopPlaceNewer = new StopPlace();

        stopPlaceOlder.setChanged(ZonedDateTime.ofInstant(Instant.ofEpochMilli(50), ZoneId.systemDefault()));
        stopPlaceOlder.setName(new MultilingualString("it's older", "en"));

        stopPlaceNewer.setChanged(ZonedDateTime.ofInstant(Instant.ofEpochMilli(100), ZoneId.systemDefault()));
        stopPlaceNewer.setName(new MultilingualString("it's newer", "en"));

        stopPlaceRepository.save(stopPlaceNewer);
        stopPlaceRepository.save(stopPlaceOlder);

        Pageable pageable = new PageRequest(0, 2);
        Page<StopPlace> page = stopPlaceRepository.findByNameValueContainingIgnoreCaseOrderByChangedDesc("it", pageable);

        assertThat(page.getContent().get(0).getChanged()).isEqualTo(stopPlaceNewer.getChanged());
        assertThat(page.getContent().get(1).getChanged()).isEqualTo(stopPlaceOlder.getChanged() );
    }

    /*
    @Test
    public void reproduceConcurrencyIssue() throws InterruptedException, ExecutionException {


        ExecutorService service = Executors.newFixedThreadPool(200);
        List<Future<String>> futures = new ArrayList<>();

        for(int i = 0; i < 1000; i++) {
            Future<String> future = service.submit(() -> {
                StopPlace stopPlace = new StopPlace();
                stopPlaceRepository.save(stopPlace);
                //System.out.println("Saved: " + stopPlace.getId());
                return stopPlace.getId();
            });
            futures.add(future);
        }

        for(Future<String> future : futures) {

            String id = future.get();
            //System.out.println("Got id "+id);
            assertThat(id).isNotEmpty();
        }


    }
    */
}