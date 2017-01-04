package org.rutebanken.tiamat.exporters;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.tiamat.model.job.ExportJob;
import org.rutebanken.tiamat.model.job.JobStatus;
import org.rutebanken.tiamat.repository.ExportJobRepository;
import org.rutebanken.tiamat.repository.StopPlaceSearch;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.PublicationDeliveryStreamingOutput;
import org.rutebanken.tiamat.service.BlobStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class AsyncPublicationDeliveryExporter {

    private static final Logger logger = LoggerFactory.getLogger(AsyncPublicationDeliveryExporter.class);

    private static final ExecutorService exportService = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder()
            .setNameFormat("publication-delivery-exporter-%d").build());

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("YYYYMMdd-HHmmss");

    private final PublicationDeliveryExporter publicationDeliveryExporter;

    private final PublicationDeliveryStreamingOutput publicationDeliveryStreamingOutput;

    private final ExportJobRepository exportJobRepository;

    private final BlobStoreService blobStoreService;

    @Autowired
    public AsyncPublicationDeliveryExporter(PublicationDeliveryExporter publicationDeliveryExporter, PublicationDeliveryStreamingOutput publicationDeliveryStreamingOutput, ExportJobRepository exportJobRepository, BlobStoreService blobStoreService) {
        this.publicationDeliveryExporter = publicationDeliveryExporter;
        this.publicationDeliveryStreamingOutput = publicationDeliveryStreamingOutput;
        this.exportJobRepository = exportJobRepository;
        this.blobStoreService = blobStoreService;
    }

    public ExportJob startExportJob(StopPlaceSearch stopPlaceSearch) {

        ExportJob exportJob = new ExportJob(JobStatus.PROCESSING);
        exportJob.setStarted(ZonedDateTime.now());
        exportJobRepository.save(exportJob);
        exportJob.setFileName(createFileName(exportJob.getId(), exportJob.getStarted()));
        exportJob.setJobUrl("export_job/" + exportJob.getId());

        exportService.submit(new Runnable() {
            @Override
            public void run() {
                logger.info("Started export job {}", exportJob);
                PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryExporter.exportStopPlaces(stopPlaceSearch);

                logger.info("Got publication delivery from exporter: {}", publicationDeliveryStructure);

                try {

                    final PipedInputStream in = new PipedInputStream();
                    final PipedOutputStream out = new PipedOutputStream(in);

                    Thread outputStreamThread = new Thread(
                            new Runnable() {
                                public void run() {
                                    try {
                                        logger.info("Streaming output thread running");

                                        StreamingOutput streamingOutput = publicationDeliveryStreamingOutput.stream(publicationDeliveryStructure);
                                        logger.info("Write to streaming output which is piped to input stream");

                                        streamingOutput.write(out);
                                        out.close();

                                    } catch (JAXBException | IOException | SAXException e) {
                                        exportJob.setStatus(JobStatus.FAILED);
                                        String message = "Error executing export job " + exportJob;
                                        logger.error(message, e);
                                    }
                                }
                            }
                    );
                    outputStreamThread.setName("export-output" + exportJob.getFileName());
                    outputStreamThread.start();

                    blobStoreService.upload(exportJob.getFileName(), in);
                    outputStreamThread.join();

                    if (!exportJob.getStatus().equals(JobStatus.FAILED)) {
                        exportJob.setStatus(JobStatus.FINISHED);
                        exportJob.setFinished(ZonedDateTime.now());
                        logger.info("Export job {} done", exportJob);
                    }
                } catch (IOException | InterruptedException e) {
                    logger.error("Error while exporting asynchronously", e);
                } finally {
                    exportJobRepository.save(exportJob);
                }
            }
        });
        exportJobRepository.save(exportJob);
        logger.info("Returning export job {}", exportJob);
        return exportJob;
    }

    public String createFileName(long exportJobId, ZonedDateTime started) {
        return "tiamat-export-" + exportJobId + "-" + started.format(DATE_TIME_FORMATTER) + ".xml";
    }

    public InputStream getJobFileContent(long exportJobId) {
        ExportJob exportJob = exportJobRepository.findOne(exportJobId);

        logger.info("Found export job by id: {}", exportJob);

        if(!exportJob.getStatus().equals(JobStatus.FINISHED)) {
            throw new RuntimeException("Job status is not FINISHED for job: "+exportJob);
        }

        return blobStoreService.download(exportJob.getFileName());
    }

    public Collection<ExportJob> getJobs() {
        return exportJobRepository.findAll();
    }


}
