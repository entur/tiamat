package org.rutebanken.tiamat.service;

import java.io.InputStream;

public interface BlobStoreService {
    void upload(String fileName, InputStream inputStream);

    InputStream download(String fileName);
}
