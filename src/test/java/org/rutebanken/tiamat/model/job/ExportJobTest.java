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

package org.rutebanken.tiamat.model.job;

import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.repository.ExportJobRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.text.html.Option;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ExportJobTest extends TiamatIntegrationTest {

    @Autowired
    private ExportJobRepository exportJobRepository;

    @Test
    public void saveAndLoadExportJob() {
        ExportJob exportJob = new ExportJob();
        exportJob.setStatus(JobStatus.PROCESSING);
        exportJobRepository.save(exportJob);

        Optional<ExportJob> actual = exportJobRepository.findById(exportJob.getId());
        assertThat(actual).isPresent();
    }

}