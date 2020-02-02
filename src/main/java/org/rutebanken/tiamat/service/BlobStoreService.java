package org.rutebanken.tiamat.service;

import com.google.cloud.storage.Storage;

import java.io.InputStream;

public interface BlobStoreService {
    void upload(String fileName, InputStream inputStream);

    Storage getStorage();

    InputStream download(String fileName);

    String createBlobIdName(String blobPath, String fileName);
}
