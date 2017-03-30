package org.rutebanken.tiamat.model.job;

import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.repository.ExportJobRepository;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class ExportJobTest extends TiamatIntegrationTest {

    @Autowired
    private ExportJobRepository exportJobRepository;

    @Test
    public void saveAndLoadExportJob() {
        ExportJob exportJob = new ExportJob();
        exportJob.setStatus(JobStatus.PROCESSING);
        exportJobRepository.save(exportJob);

        ExportJob actual = exportJobRepository.findOne(exportJob.getId());
        assertThat(actual).isNotNull();
    }

}