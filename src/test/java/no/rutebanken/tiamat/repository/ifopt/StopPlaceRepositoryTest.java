package no.rutebanken.tiamat.repository.ifopt;

import no.rutebanken.tiamat.TiamatApplication;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.org.netex.netex.MultilingualString;
import uk.org.netex.netex.StopPlace;

import java.util.Date;

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

        Assertions.assertThat(page.getContent().get(0).getChanged().getTime()).isEqualTo(stopPlaceNewer.getChanged().getTime());
        Assertions.assertThat(page.getContent().get(1).getChanged().getTime()).isEqualTo(stopPlaceOlder.getChanged().getTime());
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

        Assertions.assertThat(page.getContent().get(0).getChanged().getTime()).isEqualTo(stopPlaceNewer.getChanged().getTime());
        Assertions.assertThat(page.getContent().get(1).getChanged().getTime()).isEqualTo(stopPlaceOlder.getChanged().getTime());
    }
}