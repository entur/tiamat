package org.rutebanken.tiamat.service;

import org.rutebanken.helper.storage.model.BlobDescriptor;
import org.rutebanken.helper.storage.repository.BlobStoreRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * Shim for adapting {@link BlobStoreService} to use <code>rutebanken-helpers</code>
 * {@link org.rutebanken.helper.storage.repository.BlobStoreRepository} which enables reuse of additional storage
 * implementations. See also {@link org.rutebanken.tiamat.config.RutebankenBlobStoreConfiguration}
 */
@Service
@Profile("rutebanken-blobstore")
public class RutebankenBlobStoreService implements BlobStoreService {

    private final BlobStoreRepository blobStoreRepository;

    public RutebankenBlobStoreService(BlobStoreRepository blobStoreRepository) {
        this.blobStoreRepository = blobStoreRepository;
    }

    @Override
    public void upload(String fileName, InputStream inputStream) {
        blobStoreRepository.uploadBlob(new BlobDescriptor(fileName, inputStream));
    }

    @Override
    public InputStream download(String fileName) {
        return blobStoreRepository.getBlob(fileName);
    }
}
