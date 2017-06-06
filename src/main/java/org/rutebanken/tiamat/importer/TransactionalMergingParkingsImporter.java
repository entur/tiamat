package org.rutebanken.tiamat.importer;

import org.rutebanken.tiamat.model.Parking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.toList;

/**
 * When importing site frames with the matching stops concurrently, not thread safe.
 */
@Component
@Transactional
public class TransactionalMergingParkingsImporter {

    private static final Logger logger = LoggerFactory.getLogger(TransactionalMergingParkingsImporter.class);

    private final MergingParkingImporter mergingParkingImporter;

    @Autowired
    public TransactionalMergingParkingsImporter(MergingParkingImporter mergingParkingImporter) {
        this.mergingParkingImporter = mergingParkingImporter;
    }


    public Collection<org.rutebanken.netex.model.Parking> importParkings(List<Parking> parkings, AtomicInteger parkingsCreated) {
        List<org.rutebanken.netex.model.Parking> createdParkings = parkings
                .stream()
                .filter(Objects::nonNull)
                .map(parking -> {
                    org.rutebanken.netex.model.Parking importedParking = null;
                    try {
                        importedParking = mergingParkingImporter.importParking(parking);
                    } catch (Exception e) {
                        throw new RuntimeException("Could not import stop place " + parking, e);
                    }
                    parkingsCreated.incrementAndGet();
                    return importedParking;
                })
                .filter(Objects::nonNull)
                .collect(toList());

        return distinctByIdAndHighestVersion(createdParkings);
    }

    /**
     * In order to get a distinct list over stop places, and the newest version if duplicates.
     *
     * @param parkings
     * @return unique list with stop places based on ID
     */
    public Collection<org.rutebanken.netex.model.Parking> distinctByIdAndHighestVersion(List<org.rutebanken.netex.model.Parking> parkings) {
        Map<String, org.rutebanken.netex.model.Parking> uniqueParkings = new HashMap<>();
        for (org.rutebanken.netex.model.Parking parking : parkings) {
            if (uniqueParkings.containsKey(parking.getId())) {
                org.rutebanken.netex.model.Parking existingParking = uniqueParkings.get(parking.getId());
                long existingParkingVersion = tryParseLong(existingParking.getVersion());
                long parkingVersion = tryParseLong(parking.getVersion());
                if (existingParkingVersion < parkingVersion) {
                    logger.info("Returning newest version of stop place with ID {}: {}", parking.getId(), parking.getVersion());
                    uniqueParkings.put(parking.getId(), parking);
                }
            } else {
                uniqueParkings.put(parking.getId(), parking);
            }
        }
        return uniqueParkings.values();
    }

    private long tryParseLong(String version) {
        try {
            return Long.parseLong(version);
        } catch (NumberFormatException | NullPointerException e) {
            return 0L;
        }
    }
}
