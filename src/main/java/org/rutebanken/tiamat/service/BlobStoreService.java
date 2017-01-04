package org.rutebanken.tiamat.service;

import com.google.cloud.storage.Storage;
import org.rutebanken.helper.gcp.BlobStoreHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
public class BlobStoreService {

    private static final Logger logger = LoggerFactory.getLogger(BlobStoreService.class);

    private final String bucketName;

    private final String blobPath;

    private final Storage storage;

    public BlobStoreService(@Value("${blobstore.gcs.credential.path}") String credentialPath,
                            @Value("${blobstore.gcs.bucket.name}") String bucketName,
                            @Value("${blobstore.gcs.blob.path}") String blobPath,
                            @Value("${blobstore.gcs.project.id}") String projectId) {

        this.bucketName = bucketName;
        this.blobPath = blobPath;
        logger.info("Creating storage for project {}", projectId);
        storage = BlobStoreHelper.getStorage(credentialPath, projectId);

    }

    public void upload(String fileName, String fileContents) {
        String blobIdName = createBlobIdName(blobPath, fileName);
        InputStream inputStream = new ByteArrayInputStream(fileContents.getBytes());
        try {
            logger.info("Uploading {} to path {} in bucket {}", fileName, blobPath, bucketName);
            BlobStoreHelper.uploadBlob(storage, bucketName, blobIdName, inputStream, false);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error uploading file "+fileName + ", blobIdName " + blobIdName + " to bucket "+ bucketName, e);
        }
    }

    public String createBlobIdName(String blobPath, String fileName) {
        return blobPath + '/' + fileName;
    }
}
