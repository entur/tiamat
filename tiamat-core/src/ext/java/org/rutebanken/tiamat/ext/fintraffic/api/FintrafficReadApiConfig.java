package org.rutebanken.tiamat.ext.fintraffic.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.rutebanken.tiamat.exporter.ServiceFrameElementCreator;
import org.rutebanken.tiamat.ext.fintraffic.api.background.ReadApiBackgroundJobs;
import org.rutebanken.tiamat.ext.fintraffic.api.repository.FintrafficNetexRepository;
import org.rutebanken.tiamat.ext.fintraffic.api.repository.NetexRepository;
import org.rutebanken.tiamat.netex.id.ValidPrefixList;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.time.ExportTimeZone;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Profile("fintraffic-read-api")
@Configuration
@EnableConfigurationProperties(AreaCodeMappingConfig.class)
public class FintrafficReadApiConfig {
    @Bean
    public NetexRepository netexRepository(
            DataSource dataSource,
            ObjectMapper objectMapper
    ) {
        // A dedicated JdbcTemplate with fetchSize enables PostgreSQL server-side cursors
        // in queryForStream. Without fetchSize > 0, the driver fetches all rows at once
        // — the entire ext_fintraffic_netex_entity table (~300 MB) into the heap, causing OOM.
        // fetchSize=1000 instructs the driver to stream rows in batches of 1000 from the DB.
        // This only works because streamStopPlaces() is always called inside a transaction
        // (autoCommit=false is required for PostgreSQL server-side cursors).
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        jdbc.setFetchSize(1000);
        return new FintrafficNetexRepository(jdbc, objectMapper);
    }

    @Bean
    public ReadApiNetexPublicationDeliveryService readApiNetexPublicationDeliveryService(
            NetexRepository netexRepository,
            ValidPrefixList validPrefixList,
            ExportTimeZone exportTimeZone,
            @Value("${tiamat.locals.language.default:nor}") String defaultLanguage,
            @Value("${netex.profile.version:1.12:NO-NeTEx-stops:1.4}") String publicationDeliveryId
    ) {
        return new ReadApiNetexPublicationDeliveryService(
                netexRepository,
                validPrefixList,
                exportTimeZone,
                defaultLanguage,
                publicationDeliveryId
        );
    }

    @Bean
    public SearchKeyService searchKeyService(
            ObjectMapper objectMapper,
            AreaCodeMappingConfig areaCodeMappingConfig,
            StopPlaceRepository stopPlaceRepository
    ) {
        return new FintrafficSearchKeyService(
                objectMapper,
                areaCodeMappingConfig,
                stopPlaceRepository
        );
    }

    @Bean
    public ReadApiNetexMarshallingService readApiNetexMarshallingService(
            NetexMapper netexMapper,
            NetexRepository netexRepository,
            ServiceFrameElementCreator serviceFrameElementCreator,
            SearchKeyService searchKeyService,
            NetexEntityEnricher netexEntityEnricher
    ) {
        return new ReadApiNetexMarshallingService(
                netexMapper,
                netexRepository,
                serviceFrameElementCreator,
                searchKeyService,
                netexEntityEnricher
        );
    }

    @Bean
    public ReadApiEntityChangedPublisher fintrafficEntityChangedPublisher(
            ReadApiNetexMarshallingService readApiNetexMarshallingService
    ) {
        return new ReadApiEntityChangedPublisher(readApiNetexMarshallingService);
    }

    @Bean
    public ReadApiBackgroundJobs readApiBackgroundJobs(
            NetexRepository netexRepository,
            @Value("${tiamat.ext.fintraffic.read-api-background-jobs.enabled:false}") boolean backgroundJobsEnabled
    ) {
        return new ReadApiBackgroundJobs(
                netexRepository,
                backgroundJobsEnabled
        );
    }

    @Bean
    public NetexEntityEnricher fintrafficNetexEntityEnricher() {
        return new FintrafficNetexEntityEnricher();
    }
}
