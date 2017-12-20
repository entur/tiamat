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

package org.rutebanken.tiamat.exporter.async;

import com.google.api.client.util.IOUtils;
import org.rutebanken.tiamat.exporter.StreamingPublicationDelivery;
import org.rutebanken.tiamat.model.job.ExportJob;
import org.rutebanken.tiamat.model.job.JobStatus;
import org.rutebanken.tiamat.netex.validation.NetexXmlReferenceValidator;
import org.rutebanken.tiamat.repository.ExportJobRepository;
import org.rutebanken.tiamat.service.BlobStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
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
    private final NetexXmlReferenceValidator netexXmlReferenceValidator;

    public ExportJobWorker(ExportJob exportJob,
                           StreamingPublicationDelivery streamingPublicationDelivery,
                           String localExportPath,
                           String fileNameWithoutExtention,
                           BlobStoreService blobStoreService,
                           ExportJobRepository exportJobRepository,
                           NetexXmlReferenceValidator netexXmlReferenceValidator) {
        this.exportJob = exportJob;
        this.streamingPublicationDelivery = streamingPublicationDelivery;
        this.localExportPath = localExportPath;
        this.fileNameWithoutExtention = fileNameWithoutExtention;
        this.blobStoreService = blobStoreService;
        this.exportJobRepository = exportJobRepository;
        this.netexXmlReferenceValidator = netexXmlReferenceValidator;
    }


    public void run() {
        logger.info("Started export job: {}", exportJob);
        final File localExportZipFile = new File(localExportPath + File.separator + exportJob.getFileName());
        File localExportXmlFile = new File(localExportPath + File.separator + fileNameWithoutExtention + ".xml");
        try {

            exportToLocalXmlFile(localExportXmlFile);

            netexXmlReferenceValidator.validateNetexReferences(localExportXmlFile);

            localExportZipFile.createNewFile();

            exportToLocalZipFile(localExportZipFile, localExportXmlFile);

            uploadToGcp(localExportZipFile);

            exportJob.setStatus(JobStatus.FINISHED);
            exportJob.setFinished(Instant.now());
            logger.info("Export job done: {}", exportJob);

        } catch (Exception e) {
            exportJob.setStatus(JobStatus.FAILED);
            String message = "Error executing export job " + exportJob.getId() + ". Cause: " + e.getClass().getSimpleName() + " - " + e.getMessage();
            logger.error("{} {}", message, exportJob, e);
            exportJob.setMessage(message);
            if (e instanceof InterruptedException) {
                logger.info("The export job was interrupted: {}", exportJob);
                Thread.currentThread().interrupt();
            }
        } finally {
            exportJobRepository.save(exportJob);
            logger.info("Removing local files: {},{}", localExportZipFile, localExportXmlFile);
            localExportZipFile.delete();
            localExportXmlFile.delete();
        }
    }

    private void exportToLocalXmlFile(File localExportXmlFile) throws InterruptedException, IOException, XMLStreamException, SAXException, JAXBException {
        logger.info("Start streaming publication delivery to local file {}", localExportXmlFile);
        FileOutputStream fileOutputStream = new FileOutputStream(localExportXmlFile);
        streamingPublicationDelivery.stream(exportJob.getExportParams(), fileOutputStream);
    }

    private void uploadToGcp(File localExportFile) throws FileNotFoundException {
        logger.info("Uploading to gcp: {} in folder: {}", exportJob.getFileName(), exportJob.getSubFolder());
        FileInputStream fileInputStream = new FileInputStream(localExportFile);
        blobStoreService.upload(exportJob.getSubFolder() + "/" + exportJob.getFileName(), fileInputStream);
    }

    private void exportToLocalZipFile(File localZipFile, File localExportZipFile) throws IOException, InterruptedException, JAXBException, XMLStreamException, SAXException {
        logger.info("Adding {} to zip file: {}", localExportZipFile, localZipFile);

        final FileOutputStream fileOutputStream = new FileOutputStream(localZipFile);
        final ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);

        try {
            zipOutputStream.putNextEntry(new ZipEntry(fileNameWithoutExtention + ".xml"));

            InputStream fileInputStream = new FileInputStream(localExportZipFile);
            IOUtils.copy(fileInputStream, zipOutputStream);
            zipOutputStream.closeEntry();
            logger.info("Written to disk {}", localZipFile);
        } finally {
            try {
                zipOutputStream.close();
            } catch (IOException e) {
                throw new IOException("Could not close zipoutput stream for file: "+localZipFile, e);
            }
        }
    }
}
