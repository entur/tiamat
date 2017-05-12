package org.rutebanken.tiamat.repository;

import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class StopPlaceRepositoryTest extends TiamatIntegrationTest {

	@Test
	public void findStopPlacesSortedCorrectly() {
		StopPlace stopPlaceOlder = new StopPlace();
		StopPlace stopPlaceNewer = new StopPlace();

		stopPlaceOlder.setChanged(Instant.ofEpochMilli(50));
		stopPlaceNewer.setChanged(Instant.ofEpochMilli(100));

		stopPlaceRepository.save(stopPlaceNewer);
		stopPlaceRepository.save(stopPlaceOlder);

		Pageable pageable = new PageRequest(0, 2);
		Page<StopPlace> page = stopPlaceRepository.findAllByOrderByChangedDesc(pageable);

		assertThat(page.getContent().get(0).getNetexId()).isEqualTo(stopPlaceNewer.getNetexId());
		assertThat(page.getContent().get(0).getChanged()).isEqualTo(stopPlaceNewer.getChanged());
		assertThat(page.getContent().get(1).getNetexId()).isEqualTo(stopPlaceOlder.getNetexId());
		assertThat(page.getContent().get(1).getChanged()).isEqualTo(stopPlaceOlder.getChanged());
	}

	@Test
	public void findStopPlacesByNameSortedCorrectly() {
		StopPlace stopPlaceOlder = new StopPlace();
		StopPlace stopPlaceNewer = new StopPlace();

		stopPlaceOlder.setChanged(Instant.ofEpochMilli(50));
		stopPlaceOlder.setName(new EmbeddableMultilingualString("it's older", "en"));

		stopPlaceNewer.setChanged(Instant.ofEpochMilli(100));
		stopPlaceNewer.setName(new EmbeddableMultilingualString("it's newer", "en"));

		stopPlaceRepository.save(stopPlaceNewer);
		stopPlaceRepository.save(stopPlaceOlder);

		Pageable pageable = new PageRequest(0, 2);
		Page<StopPlace> page = stopPlaceRepository.findByNameValueContainingIgnoreCaseOrderByChangedDesc("it", pageable);

		assertThat(page.getContent().get(0).getChanged()).isEqualTo(stopPlaceNewer.getChanged());
		assertThat(page.getContent().get(1).getChanged()).isEqualTo(stopPlaceOlder.getChanged());
	}


	@Test
	public void findByQuay() {
		StopPlace stopPlace = new StopPlace();
		Quay quay = new Quay();
		stopPlace.setQuays(Sets.newHashSet(quay));

		StopPlace savedStop = stopPlaceRepository.save(stopPlace);

		StopPlace foundStop = stopPlaceRepository.findByQuay(quay);
		Assert.assertEquals(savedStop, foundStop);
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
                //System.out.println("Saved: " + stopPlace.getNetexId());
                return stopPlace.getNetexId();
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