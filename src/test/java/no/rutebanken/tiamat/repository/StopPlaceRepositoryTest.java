package no.rutebanken.tiamat.repository;

import no.rutebanken.tiamat.TiamatApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import no.rutebanken.tiamat.model.MultilingualString;
import no.rutebanken.tiamat.model.StopPlace;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TiamatApplication.class)
@ActiveProfiles("geodb")
public class StopPlaceRepositoryTest {

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Test
    public void findStopPlacesSortedCorrectly() {
        StopPlace stopPlaceOlder = new StopPlace();
        StopPlace stopPlaceNewer = new StopPlace();

        stopPlaceOlder.setChanged(new Date(50));
        stopPlaceNewer.setChanged(new Date(100));

        stopPlaceRepository.save(stopPlaceNewer);
        stopPlaceRepository.save(stopPlaceOlder);

        Pageable pageable = new PageRequest(0, 2);
        Page<StopPlace> page = stopPlaceRepository.findAllByOrderByChangedDesc(pageable);

        assertThat(page.getContent().get(0).getChanged().getTime()).isEqualTo(stopPlaceNewer.getChanged().getTime());
        assertThat(page.getContent().get(1).getChanged().getTime()).isEqualTo(stopPlaceOlder.getChanged().getTime());
    }

    @Test
    public void findStopPlacesByNameSortedCorrectly() {
        StopPlace stopPlaceOlder = new StopPlace();
        StopPlace stopPlaceNewer = new StopPlace();

        stopPlaceOlder.setChanged(new Date(50));
        stopPlaceOlder.setName(new MultilingualString("it's older", "en", ""));

        stopPlaceNewer.setChanged(new Date(100));
        stopPlaceNewer.setName(new MultilingualString("it's newer", "en", ""));

        stopPlaceRepository.save(stopPlaceNewer);
        stopPlaceRepository.save(stopPlaceOlder);

        Pageable pageable = new PageRequest(0, 2);
        Page<StopPlace> page = stopPlaceRepository.findByNameValueContainingIgnoreCaseOrderByChangedDesc("it", pageable);

        assertThat(page.getContent().get(0).getChanged().getTime()).isEqualTo(stopPlaceNewer.getChanged().getTime());
        assertThat(page.getContent().get(1).getChanged().getTime()).isEqualTo(stopPlaceOlder.getChanged().getTime());
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