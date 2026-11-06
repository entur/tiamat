package org.rutebanken.tiamat.ext.fintraffic.netex;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rutebanken.tiamat.service.batch.StopPlaceRefUpdaterService;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class SynchronousBackgroundJobsTest {

    @Mock
    private StopPlaceRefUpdaterService stopPlaceRefUpdaterService;

    @Test
    void triggerStopPlaceUpdate_runsOnTheCallingThread() {
        AtomicReference<Thread> updaterThread = new AtomicReference<>();
        doAnswer(invocation -> {
            updaterThread.set(Thread.currentThread());
            return null;
        }).when(stopPlaceRefUpdaterService).updateAllStopPlaces();

        new SynchronousBackgroundJobs(stopPlaceRefUpdaterService).triggerStopPlaceUpdate();

        assertThat(updaterThread.get()).isSameAs(Thread.currentThread());
    }
}
