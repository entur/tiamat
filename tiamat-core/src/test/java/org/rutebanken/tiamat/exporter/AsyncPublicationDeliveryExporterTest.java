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

package org.rutebanken.tiamat.exporter;

import org.junit.Ignore;
import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.exporter.params.StopPlaceSearch;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.job.ExportJob;
import org.rutebanken.tiamat.model.job.JobStatus;
import org.rutebanken.tiamat.repository.ExportJobRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.rutebanken.tiamat.exporter.params.ExportParams.newExportParamsBuilder;

/**
 * Manual test for verifying async export to Google Cloud Storage.
 * Requires actual GCS bucket configuration to run.
 */
@Ignore("Manual test - requires GCS bucket configuration")
public class AsyncPublicationDeliveryExporterTest extends TiamatIntegrationTest {

    @Autowired
    private AsyncPublicationDeliveryExporter asyncPublicationDeliveryExporter;

    @Autowired
    private ExportJobRepository exportJobRepository;

    @Test
    public void testAsyncExportToGcs() throws InterruptedException {

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("stop place to be exported"));

        stopPlaceRepository.save(stopPlace);
        stopPlaceRepository.flush();

        ExportParams exportParams = newExportParamsBuilder().setStopPlaceSearch(new StopPlaceSearch()).build();

        ExportJob exportJob = asyncPublicationDeliveryExporter.startExportJob(exportParams);

        assertThat(exportJob.getId()).isGreaterThan(0L);

        long start = System.currentTimeMillis();
        long timeout = 10000;
        while (true) {
            Optional<ExportJob> actualExportJob = exportJobRepository.findById(exportJob.getId());
            if (actualExportJob.isPresent() && actualExportJob.get().getStatus().equals(exportJob.getStatus())) {
                if (System.currentTimeMillis() - start > timeout) {
                    fail("Waited more than " + timeout + " millis for job status to change");
                }
                Thread.sleep(1000);
                continue;
            }

            if (actualExportJob.isPresent()) {
                if (actualExportJob.get().getStatus().equals(JobStatus.FAILED)) {
                    fail("Job status is failed: " + actualExportJob.get().getMessage());
                } else if (actualExportJob.get().getStatus().equals(JobStatus.FINISHED)) {
                    assertThat(actualExportJob.get().getFileName()).isNotEmpty();
                    return; // Test passed
                }
            }
        }
    }
}