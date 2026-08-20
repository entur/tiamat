package org.rutebanken.tiamat.rest.write.dto;

import org.junit.Test;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJob;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJobStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class StopPlaceJobDtoTest {

    /**
     * IN_PROGRESS exists so a job can be claimed exactly once; it is not a distinction clients
     * need, and exposing it would change the wire contract for an internal implementation detail.
     */
    @Test
    public void claimedJobIsReportedAsProcessing() {
        AsyncStopPlaceJob job = job(AsyncStopPlaceJobStatus.IN_PROGRESS);

        assertThat(StopPlaceJobDto.from(job).status()).isEqualTo(AsyncStopPlaceJobStatus.PROCESSING);
    }

    @Test
    public void acceptedJobIsReportedAsProcessing() {
        AsyncStopPlaceJob job = job(AsyncStopPlaceJobStatus.PROCESSING);

        assertThat(StopPlaceJobDto.from(job).status()).isEqualTo(AsyncStopPlaceJobStatus.PROCESSING);
    }

    @Test
    public void terminalStatusesAreReportedAsThemselves() {
        assertThat(StopPlaceJobDto.from(job(AsyncStopPlaceJobStatus.FINISHED)).status())
                .isEqualTo(AsyncStopPlaceJobStatus.FINISHED);
        assertThat(StopPlaceJobDto.from(job(AsyncStopPlaceJobStatus.FAILED)).status())
                .isEqualTo(AsyncStopPlaceJobStatus.FAILED);
        assertThat(StopPlaceJobDto.from(job(AsyncStopPlaceJobStatus.TIMED_OUT)).status())
                .isEqualTo(AsyncStopPlaceJobStatus.TIMED_OUT);
    }

    private static AsyncStopPlaceJob job(AsyncStopPlaceJobStatus status) {
        AsyncStopPlaceJob job = new AsyncStopPlaceJob();
        job.setId(1L);
        job.setStatus(status);
        return job;
    }
}
