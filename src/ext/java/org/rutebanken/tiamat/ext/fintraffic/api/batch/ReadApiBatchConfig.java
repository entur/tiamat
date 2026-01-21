package org.rutebanken.tiamat.ext.fintraffic.api.batch;

import org.rutebanken.tiamat.ext.fintraffic.api.ReadApiNetexMarshallingService;
import org.rutebanken.tiamat.ext.fintraffic.api.repository.NetexRepository;
import org.rutebanken.tiamat.repository.ParkingRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("(fintraffic-update-task & fintraffic-read-api)")
@Configuration
public class ReadApiBatchConfig {
    @Bean
    public ReadApiBatchUpdateService readApiBatchUpdateService(
            ReadApiNetexMarshallingService marshallingService,
            StopPlaceRepository stopPlaceRepository,
            ParkingRepository parkingRepository
    ) {
        return new ReadApiBatchUpdateService(
                marshallingService,
                stopPlaceRepository,
                parkingRepository
        );
    }

    @Bean
    public ReadApiBatchUpdateTask readApiBatchUpdateTask(
            NetexRepository netexRepository,
            ApplicationContext applicationContext,
            ReadApiBatchUpdateService readApiBatchUpdateService,
            ReadApiBatchWriteService readApiBatchWriteService
    ) {
        return new ReadApiBatchUpdateTask(
                netexRepository,
                applicationContext,
                readApiBatchUpdateService,
                readApiBatchWriteService
        );
    }

    @Bean
    public ReadApiBatchWriteService readApiBatchWriteService(
            NetexRepository netexRepository
    ) {
        return new ReadApiBatchWriteService(netexRepository);
    }
}
