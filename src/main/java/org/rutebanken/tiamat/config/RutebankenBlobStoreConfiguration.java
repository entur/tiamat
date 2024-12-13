package org.rutebanken.tiamat.config;

import org.rutebanken.helper.storage.repository.BlobStoreRepository;
import org.rutebanken.helper.storage.repository.InMemoryBlobStoreRepository;
import org.rutebanken.helper.storage.repository.LocalDiskBlobStoreRepository;
import org.rutebanken.tiamat.service.BlobStoreService;
import org.rutebanken.tiamat.service.RutebankenBlobStoreService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.concurrent.ConcurrentHashMap;

/**
 * <a href="https://github.com/entur/rutebanken-helpers">rutebanken-helpers</a> based repository configuration for blob
 * storing. Support is enabled by activating <code>rutebanken-blobstore</code> profile and then one of the following:
 * <ul>
 *     <li><code>local-disk-blobstore</code></li>
 *     <li><code>in-memory-blobstore</code></li>
 * </ul>
 * <p>
 * Tiamat also has its own storage abstraction, {@link org.rutebanken.tiamat.service.BlobStoreService}. An adapter is
 * provided ({@link org.rutebanken.tiamat.service.RutebankenBlobStoreService}) which will be used if the
 * <code>rutebanken-blobstore</code> profile is active. The adapted blob stores complement Tiamat's own implementation
 * in non-conflicting manner, meaning that of Tiamat's blobstore profiles (<code>gcs-blobstore</code>, <code>local-blobstore</code>)
 * neither will clash with what this configuration class provides.
 * <p>
 * The names of the supported profiles and configuration keys match with the ones in <a href="https://github.com/entur/uttu">entur/uttu</a>.
 * There is a legacy naming problem with the <code>gcs-blobstore</code> profile and its configuration injection, but as
 * this <code>rutebanken-helpers adaptation</code> is not needed for GCS support within Tiamat but for the other
 * implementations it provides, in practice these do not clash. If the GCS implementation is ever to be taken into use,
 * {@link org.rutebanken.tiamat.service.GcsBlobStoreService} first needs to be adapted directly, and additional feature
 * gap fixing will probably need to be done in <code>rutebanken-helpers</code> as well.
 */
@Configuration
@Profile("rutebanken-blobstore")
public class RutebankenBlobStoreConfiguration {

    @Bean
    BlobStoreService blobStoreService(BlobStoreRepository blobStoreRepository) {
        return new RutebankenBlobStoreService(blobStoreRepository);
    }

    @Profile("local-disk-blobstore")
    @Bean
    BlobStoreRepository localDiskBlobStoreRepository(
            @Value("${blobstore.local.folder:files/blob}") String baseFolder,
            @Value("${blobstore.local.container.name}") String containerName
    ) {
        LocalDiskBlobStoreRepository localDiskBlobStoreRepository = new LocalDiskBlobStoreRepository(baseFolder);
        localDiskBlobStoreRepository.setContainerName(containerName);
        return localDiskBlobStoreRepository;
    }

    @Profile("in-memory-blobstore")
    @Bean
    BlobStoreRepository inMemoryBlobStoreRepository(
            @Value("${blobstore.local.container.name}") String containerName
    ) {
        InMemoryBlobStoreRepository inMemoryBlobStoreRepository = new InMemoryBlobStoreRepository(new ConcurrentHashMap<>());
        inMemoryBlobStoreRepository.setContainerName(containerName);
        return inMemoryBlobStoreRepository;
    }
}
