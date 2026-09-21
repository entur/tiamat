package org.rutebanken.tiamat.ext.fintraffic.netex;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rutebanken.tiamat.netex.id.GaplessIdGeneratorService;
import org.rutebanken.tiamat.service.batch.StopPlaceRefUpdaterService;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class SynchronousBackgroundJobsTest {

    @Mock
    private GaplessIdGeneratorService gaplessIdGeneratorService;
    @Mock
    private StopPlaceRefUpdaterService stopPlaceRefUpdaterService;

    @Test
    void triggerStopPlaceUpdate_runsOnTheCallingThread() {
        AtomicReference<Thread> updaterThread = new AtomicReference<>();
        doAnswer(invocation -> {
            updaterThread.set(Thread.currentThread());
            return null;
        }).when(stopPlaceRefUpdaterService).updateAllStopPlaces();

        new SynchronousBackgroundJobs(gaplessIdGeneratorService, stopPlaceRefUpdaterService)
                .triggerStopPlaceUpdate();

        // The inherited implementation submits to an executor and returns immediately.
        assertThat(updaterThread.get()).isSameAs(Thread.currentThread());
    }

    @Test
    void scheduleBackgroundJobs_doesNotScheduleThePeriodicStopPlaceUpdate() throws Exception {
        new SynchronousBackgroundJobs(gaplessIdGeneratorService, stopPlaceRefUpdaterService)
                .scheduleBackgroundJobs();

        // The inherited schedule fires the stop place updater one minute in; the gapless id
        // persister, which is kept, is 15 seconds out. Neither may have run yet.
        Thread.sleep(200);

        verifyNoInteractions(stopPlaceRefUpdaterService, gaplessIdGeneratorService);
    }
}
