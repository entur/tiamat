package org.rutebanken.tiamat.config;

import org.rutebanken.helper.aws.repository.S3BlobStoreRepository;
import org.rutebanken.helper.storage.repository.BlobStoreRepository;
import org.rutebanken.helper.storage.repository.InMemoryBlobStoreRepository;
import org.rutebanken.helper.storage.repository.LocalDiskBlobStoreRepository;
import org.rutebanken.tiamat.service.BlobStoreService;
import org.rutebanken.tiamat.service.RutebankenBlobStoreService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

import java.net.URI;
import java.time.Duration;
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
@Lazy
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

    @Bean
    BlobStoreRepository blobStoreRepository(
            @Value("${blobstore.s3.bucket}") String containerName,
            S3Client s3Client
    ) {
        S3BlobStoreRepository s3BlobStoreRepository = new S3BlobStoreRepository(s3Client);
        s3BlobStoreRepository.setContainerName(containerName);
        return s3BlobStoreRepository;
    }

    @Profile("local | test")
    @Bean
    public AwsCredentialsProvider localCredentials(
            @Value("blobstore.s3.access-key-id") String accessKeyId,
            @Value("blobstore.s3.secret-key") String secretKey
    ) {
        return StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKeyId, secretKey)
        );
    }

    @Profile("!local & !test")
    @Bean
    public AwsCredentialsProvider cloudCredentials() {
        return DefaultCredentialsProvider.create();
    }

    @Bean
    public S3Client s3Client(
            @Value("${blobstore.s3.region}") String region,
            @Value("${blobstore.s3.endpoint-override:#{null}}") String endpointOverride,
            AwsCredentialsProvider credentialsProvider
    ) {
        S3ClientBuilder builder = S3Client
                .builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .overrideConfiguration(
                        ClientOverrideConfiguration
                                .builder()
                                .apiCallAttemptTimeout(Duration.ofSeconds(15))
                                .apiCallTimeout(Duration.ofSeconds(15))
                                .retryPolicy(retryPolicy -> retryPolicy.numRetries(5))
                                .build()
                );
        if (endpointOverride != null) {
            builder = builder.endpointOverride(URI.create(endpointOverride));
        }
        return builder.build();
    }
}
