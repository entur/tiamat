package org.rutebanken.tiamat.exporter.async;

import org.rutebanken.tiamat.exporter.StreamingPublicationDelivery;
import org.rutebanken.tiamat.model.job.ExportJob;
import org.rutebanken.tiamat.model.job.JobStatus;
import org.rutebanken.tiamat.repository.ExportJobRepository;
import org.rutebanken.tiamat.service.BlobStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ExportJobWorker implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ExportJobWorker.class);
    private final ExportJob exportJob;
    private final StreamingPublicationDelivery streamingPublicationDelivery;
    private final String localExportPath;
    private final String fileNameWithoutExtention;
    private final BlobStoreService blobStoreService;
    private final ExportJobRepository exportJobRepository;

    public ExportJobWorker(ExportJob exportJob,
                           StreamingPublicationDelivery streamingPublicationDelivery,
                           String localExportPath,
                           String fileNameWithoutExtention,
                           BlobStoreService blobStoreService,
                           ExportJobRepository exportJobRepository) {
        this.exportJob = exportJob;
        this.streamingPublicationDelivery = streamingPublicationDelivery;
        this.localExportPath = localExportPath;
        this.fileNameWithoutExtention = fileNameWithoutExtention;
        this.blobStoreService = blobStoreService;
        this.exportJobRepository = exportJobRepository;
    }


    public void run() {
        logger.info("Started export job: {}", exportJob);
        final File localExportFile = new File(localExportPath + File.separator + exportJob.getFileName());
        try {
            localExportFile.createNewFile();
            exportToLocalZipFile(localExportFile);
            uploadToGcp(localExportFile);

            exportJob.setStatus(JobStatus.FINISHED);
            exportJob.setFinished(Instant.now());
            logger.info("Export job done: {}", exportJob);

        } catch (Exception e) {
            exportJob.setStatus(JobStatus.FAILED);
            String message = "Error executing export job " + exportJob.getId() + ". Cause: " + e.getClass().getSimpleName() + " - " + e.getMessage();
            logger.error(message + " " + exportJob, e);
            exportJob.setMessage(message);
            if (e instanceof InterruptedException) {
                logger.info("The export job was interrupted: {}", exportJob);
                Thread.currentThread().interrupt();
            }
        } finally {
            exportJobRepository.save(exportJob);
            logger.info("Removing local file: {}", localExportFile);
            localExportFile.delete();
        }
    }

    private void uploadToGcp(File localExportFile) throws FileNotFoundException {
        Instant now = Instant.now();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        String gcpSubfolder = localDateTime.getYear() + "-" + String.format("%02d", localDateTime.getMonthValue());
        logger.info("Uploading to gcp: {} in folder: {}", exportJob.getFileName(), gcpSubfolder);
        FileInputStream fileInputStream = new FileInputStream(localExportFile);
        blobStoreService.upload(gcpSubfolder + "/" + exportJob.getFileName(), fileInputStream);
    }

    private void exportToLocalZipFile(File localExportFile) throws IOException, InterruptedException, JAXBException, XMLStreamException {
        final FileOutputStream fileOutputStream = new FileOutputStream(localExportFile);
        final ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);

        try {
            zipOutputStream.putNextEntry(new ZipEntry(fileNameWithoutExtention + ".xml"));
            logger.info("Start streaming publication delivery to zip file {}", localExportFile);
            streamingPublicationDelivery.stream(exportJob.getExportParams(), zipOutputStream);
            zipOutputStream.closeEntry();
            logger.info("Written to disk {}", localExportFile);
        } finally {
            try {
                zipOutputStream.close();
            } catch (IOException e) {
                throw new IOException("Could not close zipoutput stream for file: "+localExportFile, e);
            }
        }
    }
}
