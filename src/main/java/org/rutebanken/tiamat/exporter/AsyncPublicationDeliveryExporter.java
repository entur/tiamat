package org.rutebanken.tiamat.exporter;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.model.job.ExportJob;
import org.rutebanken.tiamat.model.job.JobStatus;
import org.rutebanken.tiamat.repository.ExportJobRepository;
import org.rutebanken.tiamat.service.BlobStoreService;
import org.rutebanken.tiamat.time.ExportTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class AsyncPublicationDeliveryExporter {

    public static final String ASYNC_JOB_URL = "async/job";

    private static final Logger logger = LoggerFactory.getLogger(AsyncPublicationDeliveryExporter.class);

    private static final ExecutorService exportService = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder()
            .setNameFormat("exporter-%d").build());

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("YYYYMMdd-HHmmss");

    private final ExportJobRepository exportJobRepository;

    private final BlobStoreService blobStoreService;

    private final StreamingPublicationDelivery streamingPublicationDelivery;

    private final ExportTimeZone exportTimeZone;

    private final String localExportPath;

    @Autowired
    public AsyncPublicationDeliveryExporter(ExportJobRepository exportJobRepository,
                                            BlobStoreService blobStoreService,
                                            StreamingPublicationDelivery streamingPublicationDelivery,
                                            ExportTimeZone exportTimeZone,
                                            @Value("${async.export.path:/deployments/date}") String localExportPath) {
        this.exportJobRepository = exportJobRepository;
        this.blobStoreService = blobStoreService;
        this.streamingPublicationDelivery = streamingPublicationDelivery;
        this.exportTimeZone = exportTimeZone;
        this.localExportPath = localExportPath;
    }

    /**
     * Start export job with upload to google cloud storage
     * @param exportParams search params for stops
     * @return export job with information about the started process
     */
    public ExportJob startExportJob(ExportParams exportParams) {



        ExportJob exportJob = new ExportJob(JobStatus.PROCESSING);
        exportJob.setStarted(Instant.now());
        exportJobRepository.save(exportJob);
        String fileNameWithoutExtention = createFileNameWithoutExtention(exportJob.getId(), exportJob.getStarted());
        exportJob.setFileName(fileNameWithoutExtention + ".zip");
        exportJob.setJobUrl(ASYNC_JOB_URL + '/' + exportJob.getId());
        exportJobRepository.save(exportJob);

        final String localExportFile = localExportPath + File.pathSeparator + exportJob.getFileName();

        exportService.submit(() -> {
                try {
                    logger.info("Started export job {}", exportJob);

                    final FileOutputStream fileOutputStream = new FileOutputStream(localExportFile);

                    Thread outputStreamThread = new Thread(() -> {
                        final ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);

                        try {
                            logger.info("Streaming output thread running");
                            zipOutputStream.putNextEntry(new ZipEntry(fileNameWithoutExtention + ".xml"));
                            streamingPublicationDelivery.stream(exportParams, zipOutputStream);
                            zipOutputStream.closeEntry();
                        } catch (Exception e) {
                            exportJob.setStatus(JobStatus.FAILED);
                            String message = "Error executing export job " + exportJob.getId() + ". Cause: " + e.getClass().getSimpleName() + " - " + e.getMessage();
                            logger.error(message + " " + exportJob, e);
                            exportJob.setMessage(message);
                            if (e instanceof InterruptedException) {
                                Thread.currentThread().interrupt();
                            }
                        } finally {

                            try {
                                zipOutputStream.close();
                            } catch (IOException e) {
                                logger.warn("Could not close stream", e);
                            }
                        }
                    }
                    );

                    outputStreamThread.setName("outstream-" + exportJob.getId());
                    outputStreamThread.start();
                    outputStreamThread.join();

                    logger.info("{} written to disk", localExportFile);

                    logger.info("{} uploading to gcp", exportJob.getFileName());
                    FileInputStream fileInputStream = new FileInputStream(localExportFile);
                    blobStoreService.upload(exportJob.getFileName(), fileInputStream);

                    if (!exportJob.getStatus().equals(JobStatus.FAILED)) {
                        exportJob.setStatus(JobStatus.FINISHED);
                        exportJob.setFinished(Instant.now());
                        logger.info("Export job {} done", exportJob);
                    }
                } catch (Exception e) {
                    logger.error("Error while exporting asynchronously", e);
                    exportJob.setStatus(JobStatus.FAILED);
                    exportJob.setMessage(e.getMessage());
                } finally {
                    exportJobRepository.save(exportJob);
                }
            });
        logger.info("Returning export job {}", exportJob);
        return exportJob;
    }

    public String createFileNameWithoutExtention(long exportJobId, Instant started) {
        return "tiamat-export-" + exportJobId + "-" + started.atZone(exportTimeZone.getDefaultTimeZone()).format(DATE_TIME_FORMATTER);
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
