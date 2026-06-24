package org.rutebanken.tiamat.ext.fintraffic.netex;

import org.rutebanken.tiamat.importer.PublicationDeliveryImporter;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.PublicationDeliveryUnmarshaller;
import org.rutebanken.tiamat.service.BlobStoreService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

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
