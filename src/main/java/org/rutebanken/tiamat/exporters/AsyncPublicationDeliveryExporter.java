package org.rutebanken.tiamat.exporters;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.tiamat.model.job.ExportJob;
import org.rutebanken.tiamat.model.job.JobStatus;
import org.rutebanken.tiamat.repository.ExportJobRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.StopPlaceSearch;
import org.rutebanken.tiamat.service.BlobStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import javax.xml.bind.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static javax.xml.bind.JAXBContext.newInstance;

@Service
public class AsyncPublicationDeliveryExporter {

    public static final String ASYNC_JOB_URL = "async/job";

    private static final Logger logger = LoggerFactory.getLogger(AsyncPublicationDeliveryExporter.class);

    private static final ExecutorService exportService = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder()
            .setNameFormat("exporter-%d").build());

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("YYYYMMdd-HHmmss");

    private final PublicationDeliveryExporter publicationDeliveryExporter;

    private final ExportJobRepository exportJobRepository;

    private final BlobStoreService blobStoreService;

    private final StreamingPublicationDelivery streamingPublicationDelivery;

    private final StopPlaceRepository stopPlaceRepository;

    @Autowired
    public AsyncPublicationDeliveryExporter(PublicationDeliveryExporter publicationDeliveryExporter, ExportJobRepository exportJobRepository, BlobStoreService blobStoreService, StreamingPublicationDelivery streamingPublicationDelivery, StopPlaceRepository stopPlaceRepository) {
        this.publicationDeliveryExporter = publicationDeliveryExporter;
        this.exportJobRepository = exportJobRepository;
        this.blobStoreService = blobStoreService;
        this.streamingPublicationDelivery = streamingPublicationDelivery;
        this.stopPlaceRepository = stopPlaceRepository;
    }

    public ExportJob startExportJob(StopPlaceSearch stopPlaceSearch) {

        ExportJob exportJob = new ExportJob(JobStatus.PROCESSING);
        exportJob.setStarted(ZonedDateTime.now());
        exportJobRepository.save(exportJob);
        exportJob.setFileName(createFileName(exportJob.getId(), exportJob.getStarted()));
        exportJob.setJobUrl(ASYNC_JOB_URL + '/' + exportJob.getId());
        exportJobRepository.save(exportJob);
        
        exportService.submit(new Runnable() {
            @Override
            public void run() {
                logger.info("Started export job {}", exportJob);
                PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryExporter.exportPublicationDeliveryWithoutStops();
                logger.info("Got publication delivery from exporter: {}", publicationDeliveryStructure);

                try {
                    logger.info("About to add stop places?");

                    final PipedInputStream in = new PipedInputStream();
                    final PipedOutputStream out = new PipedOutputStream(in);

                    Thread outputStreamThread = new Thread(
                            new Runnable() {
                                public void run() {
                                    try {
                                        logger.info("Streaming output thread running");

                                        logger.info("Write to streaming output which is piped to input stream");
                                        streamingPublicationDelivery.stream(publicationDeliveryStructure, stopPlaceRepository.scrollStopPlaces(), out);

                                        out.close();

                                    } catch (JAXBException | IOException | InterruptedException | XMLStreamException e) {
                                        exportJob.setStatus(JobStatus.FAILED);
                                        String message = "Error executing export job " + exportJob;
                                        logger.error(message, e);
                                    }
                                }
                            }
                    );
                    outputStreamThread.setName("outstream-" + exportJob.getId());
                    outputStreamThread.start();

                    blobStoreService.upload(exportJob.getFileName(), in);
                    outputStreamThread.join();

                    if (!exportJob.getStatus().equals(JobStatus.FAILED)) {
                        exportJob.setStatus(JobStatus.FINISHED);
                        exportJob.setFinished(ZonedDateTime.now());
                        logger.info("Export job {} done", exportJob);
                    }
                } catch (Exception e) {
                    logger.error("Error while exporting asynchronously", e);
                    exportJob.setStatus(JobStatus.FAILED);
                    exportJob.setMessage(e.getMessage());
                } finally {
                    exportJobRepository.save(exportJob);
                }
            }
        });
        logger.info("Returning export job {}", exportJob);
        return exportJob;
    }

    public String createFileName(long exportJobId, ZonedDateTime started) {
        return "tiamat-export-" + exportJobId + "-" + started.format(DATE_TIME_FORMATTER) + ".xml";
    }

    public ExportJob getExportJob(long exportJobId) {
        return exportJobRepository.findOne(exportJobId);
    }

    public InputStream getJobFileContent(ExportJob exportJob) {
        return blobStoreService.download(exportJob.getFileName());
    }

    public Collection<ExportJob> getJobs() {
        return exportJobRepository.findAll();
    }


}
