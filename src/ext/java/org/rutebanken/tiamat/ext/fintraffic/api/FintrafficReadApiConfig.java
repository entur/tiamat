package org.rutebanken.tiamat.ext.fintraffic.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.rutebanken.tiamat.exporter.ServiceFrameElementCreator;
import org.rutebanken.tiamat.ext.fintraffic.api.background.ReadApiBackgroundJobs;
import org.rutebanken.tiamat.ext.fintraffic.api.repository.FintrafficNetexRepository;
import org.rutebanken.tiamat.ext.fintraffic.api.repository.NetexRepository;
import org.rutebanken.tiamat.netex.id.ValidPrefixList;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.rutebanken.tiamat.time.ExportTimeZone;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

@Profile("fintraffic-read-api")
@Configuration
public class FintrafficReadApiConfig {
    @Bean
    public NetexRepository netexRepository(
            JdbcTemplate jdbc,
            ObjectMapper objectMapper
    ) {
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
            TopographicPlaceRepository topographicPlaceRepository,
            StopPlaceRepository stopPlaceRepository
    ) {
        return new FintrafficSearchKeyService(
                objectMapper,
                topographicPlaceRepository,
                stopPlaceRepository
        );
    }

    @Bean
    public ReadApiNetexMarshallingService readApiNetexMarshallingService(
            NetexMapper netexMapper,
            NetexRepository netexRepository,
            ServiceFrameElementCreator serviceFrameElementCreator,
            SearchKeyService searchKeyService
    ) {
        return new ReadApiNetexMarshallingService(
                netexMapper,
                netexRepository,
                serviceFrameElementCreator,
                searchKeyService
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
}
