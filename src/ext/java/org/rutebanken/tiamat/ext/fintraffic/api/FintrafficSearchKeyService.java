package org.rutebanken.tiamat.ext.fintraffic.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.rutebanken.tiamat.ext.fintraffic.api.model.FintrafficReadApiSearchKey;
import org.rutebanken.tiamat.ext.fintraffic.api.model.ReadApiSearchKey;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.PrivateCodeStructure;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.VehicleModeEnumeration;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Transactional(readOnly = true)
public class FintrafficSearchKeyService implements SearchKeyService {
    private final Logger logger = LoggerFactory.getLogger(FintrafficSearchKeyService.class);
    private final ObjectMapper objectMapper;
    private final AreaCodeMappingConfig areaCodeMappingConfig;
    private final StopPlaceRepository stopPlaceRepository;

    public FintrafficSearchKeyService(
            ObjectMapper objectMapper,
            AreaCodeMappingConfig areaCodeMappingConfig,
            StopPlaceRepository stopPlaceRepository
    ) {
        this.objectMapper = objectMapper;
        this.areaCodeMappingConfig = areaCodeMappingConfig;
        this.stopPlaceRepository = stopPlaceRepository;
    }

    @Override
    public String generateSearchKeyJSON(EntityInVersionStructure entity) {
        FintrafficReadApiSearchKey searchKey = this.extractSearchKey(entity);
        return createJSONString(searchKey);
    }

    private FintrafficReadApiSearchKey extractSearchKey(EntityInVersionStructure entity) {
        Optional<StopPlace> parentStopPlace = fetchParentStopPlace(entity);
        Optional<FintrafficReadApiSearchKey> searchKeyFromParent = parentStopPlace.map(this::extractSearchKey);
        if (entity instanceof org.rutebanken.tiamat.model.StopPlace stopPlace) {
            FintrafficReadApiSearchKey stopPlaceSearchKey = extractSearchKey(stopPlace);
            if (searchKeyFromParent.isPresent()) {
                // Merge if parent is present as well
                FintrafficReadApiSearchKey parentSearchKey = searchKeyFromParent.get();
                String[] mergedTransportModes = Stream.concat(
                        Stream.of(stopPlaceSearchKey.transportModes()),
                        Stream.of(parentSearchKey.transportModes())
                ).distinct().toArray(String[]::new);

                String[] mergedAreaCodes = Stream.concat(
                        Stream.of(stopPlaceSearchKey.areaCodes()),
                        Stream.of(parentSearchKey.areaCodes())
                ).distinct().toArray(String[]::new);

                String[] mergedMunicipalityCodes = Stream.concat(
                        Stream.of(stopPlaceSearchKey.municipalityCodes()),
                        Stream.of(parentSearchKey.municipalityCodes())
                ).distinct().toArray(String[]::new);

                return new FintrafficReadApiSearchKey(mergedTransportModes, mergedAreaCodes, mergedMunicipalityCodes);
            } else {
                return stopPlaceSearchKey;
            }
        } else if (entity instanceof Parking) {
            // For Parking, use parent StopPlace search key if available
            return searchKeyFromParent.orElse(FintrafficReadApiSearchKey.empty());
        } else {
            return FintrafficReadApiSearchKey.empty();
        }
    }

    private Optional<StopPlace> fetchParentStopPlace(EntityInVersionStructure entity) {
        Optional<SiteRefStructure> parentRef = getParentRefForEntity(entity);
        if (parentRef.isPresent()) {
            SiteRefStructure parentRefValue = parentRef.get();
            if (parentRefValue.getRef() == null) {
                // No valid parent reference
                return Optional.empty();
            }

            if (parentRefValue.getVersion() == null) {
                // No version specified, fetch latest
                return Optional.ofNullable(stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(parentRefValue.getRef()));
            }

            try {
                long version = Long.parseLong(parentRefValue.getVersion());
                return Optional.ofNullable(stopPlaceRepository.findFirstByNetexIdAndVersion(parentRefValue.getRef(), version));
            } catch (NumberFormatException e) {
                logger.error("Failed to parse version number", e);
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    private Optional<SiteRefStructure> getParentRefForEntity(EntityInVersionStructure entity) {
        return switch (entity) {
            case StopPlace sp -> Optional.ofNullable(sp.getParentSiteRef());
            case Parking p -> Optional.ofNullable(p.getParentSiteRef());
            default -> Optional.empty();
        };
    }

    private FintrafficReadApiSearchKey extractSearchKey(org.rutebanken.tiamat.model.StopPlace stopPlace) {
        String[] transportModes = stopPlace.getTransportMode() != null
                ? new String[]{stopPlace.getTransportMode().value()}
                : new String[]{};

        // If StopPlace is parentStopPlace then get transportModes from child stopPlaces as well
        if (stopPlace.isParentStopPlace() && stopPlace.getChildren() != null) {
            String[] childTransportModes = stopPlace.getChildren().stream().map(org.rutebanken.tiamat.model.StopPlace::getTransportMode)
                    .filter(Objects::nonNull)
                    .map(VehicleModeEnumeration::value)
                    .toArray(String[]::new);

            transportModes = Stream.concat(Stream.of(transportModes), Stream.of(childTransportModes))
                    .distinct()
                    .toArray(String[]::new);
        }

        String[] areaCodes = getMunicipalityCode(stopPlace)
                .map(areaCodeMappingConfig::getAreaCodesForMunicipalityCode)
                .map(codes -> codes.toArray(String[]::new))
                .orElse(new String[]{});

        String[] municipalityCodes = getMunicipalityCode(stopPlace)
                .map(code -> new String[]{code})
                .orElse(new String[]{});

        return new FintrafficReadApiSearchKey(transportModes, areaCodes, municipalityCodes);
    }

    private Optional<String> getMunicipalityCode(StopPlace stopPlace) {
        return Optional.ofNullable(stopPlace.getTopographicPlace())
                .map(TopographicPlace::getPrivateCode)
                .map(PrivateCodeStructure::getValue);
    }

    private String createJSONString(ReadApiSearchKey searchKey) {
        try {
            return objectMapper.writeValueAsString(searchKey);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert search key to JSON", e);
        }
    }
}

