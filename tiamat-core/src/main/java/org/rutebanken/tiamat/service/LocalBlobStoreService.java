package org.rutebanken.tiamat.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Profile("local-blobstore")
public class LocalBlobStoreService implements BlobStoreService {
    private static final Logger logger = LoggerFactory.getLogger(LocalBlobStoreService.class);


    @Value("${blobstore.local.folder:files/blob}")
    private String baseFolder;

    @Override
    public void upload(String fileName, InputStream inputStream) {
        logger.debug("Upload blob called in local-disk blob store on " + fileName);
        try {
            Path localPath = Paths.get(fileName);

            Path folder = Paths.get(baseFolder).resolve(localPath.getParent());
            Files.createDirectories(folder);

            Path fullPath = Paths.get(baseFolder).resolve(localPath);
            Files.deleteIfExists(fullPath);

            Files.copy(inputStream, fullPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream download(String fileName) {

        logger.debug("get blob called in local-disk blob store on " + fileName);
        Path path = Paths.get(baseFolder).resolve(fileName);
        if (!path.toFile().exists()) {
            logger.debug("getBlob(): File not found in local-disk blob store: " + path);
            return null;
        }
        logger.debug("getBlob(): File found in local-disk blob store: " + path);
        try {
            // converted as ByteArrayInputStream so that Camel stream cache can reopen it
            // since ByteArrayInputStream.close() does nothing
            return new ByteArrayInputStream(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
