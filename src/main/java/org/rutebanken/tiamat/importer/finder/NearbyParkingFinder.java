/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.importer.finder;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.repository.ParkingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class NearbyParkingFinder implements ParkingFinder {

    private static final double BOUNDING_BOX_BUFFER = 0.004;

    private static final Logger logger = LoggerFactory.getLogger(NearbyParkingFinder.class);

    private ParkingRepository parkingRepository;

    /**
     * Key is generated by using parking's name, type and envelope.
     * Value is optional NetexId
     */
    private final Cache<String, Optional<String>> nearbyParkingCache;

    @Autowired
    public NearbyParkingFinder(ParkingRepository parkingRepository,
                               @Value("${nearbyParkingFinderCache.maxSize:50000}") int maximumSize,
                               @Value("${nearbyParkingFinderCache.expiresAfter:30}") int expiresAfter,
                               @Value("${nearbyParkingFinderCache.expiresAfterTimeUnit:DAYS}") TimeUnit expiresAfterTimeUnit) {
        this.parkingRepository = parkingRepository;
        this.nearbyParkingCache = CacheBuilder.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterWrite(expiresAfter, expiresAfterTimeUnit)
                .build();

    }

    @Override
    public Parking find(Parking parking) {
        if(!parking.hasCoordinates()) {
            return null;
        }

        try {
            Optional<String> parkingNetexId = nearbyParkingCache.get(createKey(parking), () -> {
                Envelope boundingBox = createBoundingBox(parking.getCentroid());

                String matchingParkingId = parkingRepository.findNearbyParking(boundingBox, parking.getName().getValue(), parking.getParkingType());

                return Optional.ofNullable(matchingParkingId);
            });
            if(parkingNetexId.isPresent()) {
                return parkingRepository.findFirstByNetexIdOrderByVersionDesc(parkingNetexId.get());
            }
            return null;
        } catch (ExecutionException e) {
            logger.warn("Caught exception while finding parking by key and value.", e);
            throw new RuntimeException(e);
        }
    }

    public void update(Parking savedParking) {
        if(savedParking.hasCoordinates() && savedParking.getParkingType() != null) {
            nearbyParkingCache.put(createKey(savedParking), Optional.ofNullable(savedParking.getNetexId()));
        }
    }

    public final String createKey(Parking parking, Envelope envelope) {
        return parking.getName() + "-" + envelope.toString();
    }

    public final String createKey(Parking parking) {
        return createKey(parking, createBoundingBox(parking.getCentroid()));
    }

    public Envelope createBoundingBox(Point point) {

        Geometry buffer = point.buffer(BOUNDING_BOX_BUFFER);

        Envelope envelope = buffer.getEnvelopeInternal();
        logger.trace("Created envelope {}", envelope.toString());

        return envelope;
    }
}
