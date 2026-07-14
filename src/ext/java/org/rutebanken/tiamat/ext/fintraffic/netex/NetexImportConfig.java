package org.rutebanken.tiamat.ext.fintraffic.netex;

import org.rutebanken.tiamat.importer.PublicationDeliveryImporter;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.PublicationDeliveryUnmarshaller;
import org.rutebanken.tiamat.service.BlobStoreService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Registers {@link NetexImportTask} as a Spring bean when the
 * {@code fintraffic-netex-import-task} profile is active.
 * <p>
 * The following properties must be set for the task to start correctly:
 * <ul>
 *   <li>{@code spring.security.oauth2.resourceserver.jwt.jwk-set-uri} — a placeholder
 *       value is sufficient; Spring Security requires a {@code JwtDecoder} bean at
 *       startup even when no tokens are validated. Use {@code jwk-set-uri} rather than
 *       {@code issuer-uri} to avoid an OIDC discovery HTTP request at startup.</li>
 *   <li>{@code authorization.enabled=false} — the task runs with no authenticated user;
 *       without this, all import operations are rejected by
 *       {@code ReflectionAuthorizationService}.</li>
 *   <li>{@code netex.import.enabled.types} — must include the desired import type
 *       (e.g. {@code INITIAL,MERGE}); imports are silently rejected otherwise.</li>
 * </ul>
 */
@Profile("fintraffic-netex-import-task")
@Configuration
public class NetexImportConfig {

    @Bean
    public NetexImportTask netexImportTask(
            BlobStoreService blobStoreService,
            PublicationDeliveryUnmarshaller unmarshaller,
            PublicationDeliveryImporter importer) {
        return new NetexImportTask(blobStoreService, unmarshaller, importer);
    }
}
