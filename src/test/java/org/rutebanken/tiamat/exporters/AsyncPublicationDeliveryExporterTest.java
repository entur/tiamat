package org.rutebanken.tiamat.exporters;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rutebanken.tiamat.TiamatApplication;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.ExportJobRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.StopPlaceSearch;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.PublicationDeliveryStreamingOutput;
import org.rutebanken.tiamat.service.BlobStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TiamatApplication.class)
@ActiveProfiles("geodb")
public class AsyncPublicationDeliveryExporterTest {

    @Autowired
    private AsyncPublicationDeliveryExporter asyncPublicationDeliveryExporter;

    /**
     * Test uploading export file to google blob store.
     * Ignored. Intended for manual use.
     */
    @Ignore
    @Test
    public void startExportJob() throws Exception {

        asyncPublicationDeliveryExporter.startExportJob(new StopPlaceSearch());

        Thread.sleep(10000);
    }

}