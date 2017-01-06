package org.rutebanken.tiamat.exporters;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rutebanken.tiamat.TiamatApplication;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.job.ExportJob;
import org.rutebanken.tiamat.model.job.JobStatus;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.ExportJobRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.StopPlaceSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TiamatApplication.class)
@ActiveProfiles("geodb")
public class AsyncPublicationDeliveryExporterTest {

    @Autowired
    private AsyncPublicationDeliveryExporter asyncPublicationDeliveryExporter;

    @Autowired
    private ExportJobRepository exportJobRepository;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Ignore
    @Test
    public void test() throws JAXBException, ParserConfigurationException, IOException, SAXException, TransformerException, XMLStreamException, InterruptedException {

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("stop place to be exported"));

        stopPlaceRepository.save(stopPlace);
        stopPlaceRepository.flush();

        ExportJob exportJob = asyncPublicationDeliveryExporter.startExportJob(new StopPlaceSearch());

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