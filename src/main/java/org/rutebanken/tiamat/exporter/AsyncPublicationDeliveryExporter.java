package org.rutebanken.tiamat.exporter;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.rutebanken.tiamat.exporter.async.ExportJobWorker;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class AsyncPublicationDeliveryExporter {

    public static final String ASYNC_JOB_URL = "async/job";

    private static final Logger logger = LoggerFactory.getLogger(AsyncPublicationDeliveryExporter.class);

    private static final ExecutorService exportService = Executors.newFixedThreadPool(2, new ThreadFactoryBuilder()
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
                                            @Value("${async.export.path:/deployments/data/}") String localExportPath) {
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
        exportJob.setExportParams(exportParams);
        exportJob.setSubFolder(generateSubFolderName());

        exportJobRepository.save(exportJob);
        String fileNameWithoutExtention = createFileNameWithoutExtention(exportJob.getId(), exportJob.getStarted());
        exportJob.setFileName(fileNameWithoutExtention + ".zip");
        exportJob.setJobUrl(ASYNC_JOB_URL + '/' + exportJob.getId());
        exportJobRepository.save(exportJob);

        ExportJobWorker exportJobWorker = new ExportJobWorker(exportJob, streamingPublicationDelivery, localExportPath, fileNameWithoutExtention, blobStoreService, exportJobRepository);
        exportService.submit(exportJobWorker);
        logger.info("Returning started export job {}", exportJob);
        return exportJob;
    }

    public String createFileNameWithoutExtention(long exportJobId, Instant started) {
        return "tiamat-export-" + exportJobId + "-" + started.atZone(exportTimeZone.getDefaultTimeZone()).format(DATE_TIME_FORMATTER);
    }

    public ExportJob getExportJob(long exportJobId) {
        return exportJobRepository.findOne(exportJobId);
    }

    public InputStream getJobFileContent(ExportJob exportJob) {
        return blobStoreService.download(exportJob.getSubFolder() + "/" + exportJob.getFileName());
    }

    public Collection<ExportJob> getJobs() {
        return exportJobRepository.findAll();
    }

    private String generateSubFolderName() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        String gcpSubfolder = localDateTime.getYear() + "-" + String.format("%02d", localDateTime.getMonthValue());
        return gcpSubfolder;
    }
}
