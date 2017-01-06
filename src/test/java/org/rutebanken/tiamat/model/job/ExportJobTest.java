package org.rutebanken.tiamat.model.job;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.rutebanken.tiamat.TiamatApplication;
import org.rutebanken.tiamat.repository.ExportJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TiamatApplication.class)
@ActiveProfiles("geodb")
public class ExportJobTest {

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