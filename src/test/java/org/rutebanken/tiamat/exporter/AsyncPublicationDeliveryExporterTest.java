package org.rutebanken.tiamat.exporter;

import org.junit.Ignore;
import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.job.ExportJob;
import org.rutebanken.tiamat.model.job.JobStatus;
import org.rutebanken.tiamat.repository.ExportJobRepository;
import org.rutebanken.tiamat.exporter.params.StopPlaceSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Assertions.assertThat;

public class AsyncPublicationDeliveryExporterTest extends TiamatIntegrationTest {

    @Autowired
    private AsyncPublicationDeliveryExporter asyncPublicationDeliveryExporter;

    @Autowired
    private ExportJobRepository exportJobRepository;

    @Ignore
    @Test
    public void test() throws JAXBException, ParserConfigurationException, IOException, SAXException, TransformerException, XMLStreamException, InterruptedException {

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("stop place to be exported"));

        stopPlaceRepository.save(stopPlace);
        stopPlaceRepository.flush();

        ExportParams exportParams = new ExportParams();
        exportParams.stopPlaceSearch = new StopPlaceSearch();

        ExportJob exportJob = asyncPublicationDeliveryExporter.startExportJob(exportParams);

        assertThat(exportJob.getId()).isGreaterThan(0L);

        long start = System.currentTimeMillis();
        long timeout = 10000;
        while (true) {
            ExportJob actualExportJob = exportJobRepository.findOne(exportJob.getId());
            if (actualExportJob.getStatus().equals(exportJob.getStatus())) {
                if (System.currentTimeMillis() - start > timeout) {
                    fail("Waited more than " + timeout + " millis for job status to change");
                }
                Thread.sleep(1000);
                continue;
            }

            if (actualExportJob.getStatus().equals(JobStatus.FAILED)) {
                fail("Job status is failed");
            } else if (actualExportJob.getStatus().equals(JobStatus.FINISHED)) {
                System.out.println("Job finished");

            }
        }
    }
}