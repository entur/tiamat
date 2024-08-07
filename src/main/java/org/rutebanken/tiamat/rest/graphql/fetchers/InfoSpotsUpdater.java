package org.rutebanken.tiamat.rest.graphql.fetchers;

import com.google.api.client.util.Preconditions;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.tiamat.model.DisplayTypeEnumeration;
import org.rutebanken.tiamat.model.InfoSpot;
import org.rutebanken.tiamat.model.InfoSpotPoster;
import org.rutebanken.tiamat.model.InfoSpotPosterRef;
import org.rutebanken.tiamat.model.InfoSpotTypeEnumeration;
import org.rutebanken.tiamat.model.PosterSizeEnumeration;
import org.rutebanken.tiamat.repository.InfoSpotPosterRepository;
import org.rutebanken.tiamat.repository.InfoSpotRepository;
import org.rutebanken.tiamat.rest.graphql.mappers.GeometryMapper;
import org.rutebanken.tiamat.versioning.VersionCreator;
import org.rutebanken.tiamat.versioning.VersionIncrementor;
import org.rutebanken.tiamat.versioning.save.InfoSpotPosterVersionedSaverService;
import org.rutebanken.tiamat.versioning.save.InfoSpotVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_EDIT_STOPS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.BACKLIGHT;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.DESCRIPTION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.DISPLAY_TYPE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.FLOOR;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.GEOMETRY;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ID;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.INFO_SPOT_LOCATIONS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.INFO_SPOT_TYPE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.LABEL;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.LINES;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.MAINTENANCE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.OUTPUT_TYPE_INFO_SPOT;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.OUTPUT_TYPE_POSTER;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.POSTER_PLACE_SIZE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.POSTER_SIZE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PURPOSE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.RAIL_INFORMATION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SPEECH_PROPERTY;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ZONE_LABEL;
import static org.rutebanken.tiamat.rest.graphql.mappers.EmbeddableMultilingualStringMapper.getEmbeddableString;

@Service("infoSpotsUpdater")
@Transactional
public class InfoSpotsUpdater implements DataFetcher {

    private static final Logger logger = LoggerFactory.getLogger(InfoSpotsUpdater.class);

    @Autowired
    private InfoSpotRepository infoSpotRepository;

    @Autowired
    private InfoSpotPosterRepository infoSpotPosterRepository;

    @Autowired
    private ReflectionAuthorizationService authorizationService;

    @Autowired
    private InfoSpotVersionedSaverService infoSpotVersionedSaverService;

    @Autowired
    private InfoSpotPosterVersionedSaverService infoSpotPosterVersionedSaverService;

    @Autowired
    private VersionCreator versionCreator;

    @Autowired
    private VersionIncrementor versionIncrementor;

    @Autowired
    private GeometryMapper geometryMapper;

    @Override
    public Object get(DataFetchingEnvironment environment) throws Exception {
        List<Map> input = environment.getArgument(OUTPUT_TYPE_INFO_SPOT);

        if (input != null) {
            return input.stream()
                    .map(this::createOrUpdateInfoSpot)
                    .toList();
        }
        return null;
    }

    private InfoSpot createOrUpdateInfoSpot(Map input) {

        InfoSpot updatedInfoSpot;
        InfoSpot existingVersion = null;
        String netexId = (String) input.get(ID);
        if (netexId != null) {
            logger.info("Updating Info Spot {}", netexId);
            existingVersion = infoSpotRepository.findFirstByNetexIdOrderByVersionDesc(netexId);
            Preconditions.checkArgument(existingVersion != null, "Attempting to update InfoSpot [id = %s], but InfoSpot does not exist.", netexId);
            updatedInfoSpot = versionCreator.createCopy(existingVersion, InfoSpot.class);
        } else {
            logger.info("Creating new InfoSpot");
            updatedInfoSpot = new InfoSpot();
        }
        boolean isUpdated = populateInfoSpot(input, updatedInfoSpot);

        if (isUpdated) {
            authorizationService.assertAuthorized(ROLE_EDIT_STOPS, Arrays.asList(existingVersion, updatedInfoSpot));

            versionIncrementor.initiateOrIncrementInfoSpot(updatedInfoSpot);

            logger.info("Saving new version of InfoSpot {}", updatedInfoSpot);
            updatedInfoSpot = infoSpotVersionedSaverService.saveNewVersion(updatedInfoSpot);

            return updatedInfoSpot;
        } else {
            logger.info("No changes - InfoSpot {} NOT updated", netexId);
        }
        return existingVersion;

    }

    private boolean populateInfoSpot(Map input, InfoSpot target) {

        boolean isUpdated = false;

        if (input.containsKey(LABEL)) {
            var label = (String) input.get(LABEL);
            isUpdated = !label.equals(target.getLabel());
            target.setLabel(label);
        }
        if (input.containsKey(INFO_SPOT_TYPE)) {
            var infoSpotType = (InfoSpotTypeEnumeration) input.get(INFO_SPOT_TYPE);
            isUpdated |= !infoSpotType.equals(target.getInfoSpotType());
            target.setInfoSpotType(infoSpotType);
        }
        if (input.containsKey(PURPOSE)) {
            var purpose = (String) input.get(PURPOSE);
            isUpdated |= !purpose.equals(target.getPurpose());
            target.setPurpose(purpose);
        }
        if (input.containsKey(DESCRIPTION)) {
            var description = (Map) input.get(DESCRIPTION);
            isUpdated |= !description.equals(target.getDescription());
            target.setDescription(getEmbeddableString(description));
        }
        if (input.containsKey(POSTER_PLACE_SIZE)) {
            var posterPlaceSize = (PosterSizeEnumeration) input.get(POSTER_PLACE_SIZE);
            isUpdated |= !posterPlaceSize.equals(target.getPosterPlaceSize());
            target.setPosterPlaceSize(posterPlaceSize);
        }
        if (input.containsKey(BACKLIGHT)) {
            var backlight = (Boolean) input.get(BACKLIGHT);
            isUpdated |= !backlight.equals(target.getBacklight());
            target.setBacklight(backlight);
        }
        if (input.containsKey(MAINTENANCE)) {
            var maintenance = (String) input.get(MAINTENANCE);
            isUpdated |= !maintenance.equals(target.getMaintenance());
            target.setMaintenance(maintenance);
        }
        if (input.containsKey(ZONE_LABEL)) {
            var zoneLabel = (String) input.get(ZONE_LABEL);
            isUpdated |= !zoneLabel.equals(target.getZoneLabel());
            target.setZoneLabel(zoneLabel);
        }
        if (input.containsKey(RAIL_INFORMATION)) {
            var railInformation = (String) input.get(RAIL_INFORMATION);
            isUpdated |= !railInformation.equals(target.getRailInformation());
            target.setRailInformation(railInformation);
        }
        if (input.containsKey(FLOOR)) {
            var floor = (String) input.get(FLOOR);
            isUpdated |= !floor.equals(target.getFloor());
            target.setFloor(floor);
        }
        if (input.containsKey(SPEECH_PROPERTY)) {
            var speechProperty = (Boolean) input.get(SPEECH_PROPERTY);
            isUpdated |= !speechProperty.equals(target.getSpeechProperty());
            target.setSpeechProperty(speechProperty);
        }
        if (input.containsKey(DISPLAY_TYPE)) {
            var displayType = (DisplayTypeEnumeration) input.get(DISPLAY_TYPE);
            isUpdated |= !displayType.equals(target.getDisplayType());
            target.setDisplayType(displayType);
        }
        if (input.containsKey(INFO_SPOT_LOCATIONS)) {
            var locations = (List<String>) input.get((INFO_SPOT_LOCATIONS));
            isUpdated |= target.getInfoSpotLocations() == null ||
                         !(new HashSet<>(locations).containsAll(target.getInfoSpotLocations()) && target.getInfoSpotLocations().containsAll(locations));
            target.setInfoSpotLocations(locations);
        }

        if (input.containsKey(OUTPUT_TYPE_POSTER)) {
            List<Map> posters = (List<Map>) input.get(OUTPUT_TYPE_POSTER);
            Set<InfoSpotPosterRef> posterRefs = target.getPosters();

            Set<InfoSpotPoster> existingPosters = posterRefs.stream()
                    .map(p -> infoSpotPosterRepository.findFirstByNetexIdAndVersion(p.getRef(), Long.parseLong(p.getVersion())))
                    .collect(Collectors.toSet());

            Set<InfoSpotPosterRef> updatedPosters = posters.stream()
                    .map(p -> createPoster(p, existingPosters))
                    .map(InfoSpotPosterRef::new)
                    .collect(Collectors.toSet());

            target.setPosters(updatedPosters);
        }
        if (input.containsKey(GEOMETRY)) {

            target.setCentroid(geometryMapper.createGeoJsonPoint((Map) input.get(GEOMETRY)));
            isUpdated = true;
        }


        return isUpdated;
    }

    private InfoSpotPoster createPoster(Map input, Set<InfoSpotPoster> existingPosters) {
        if (input.containsKey(LABEL)) {
            String label = (String) input.get(LABEL);
            var poster = existingPosters.stream()
                    .filter(p -> p.getLabel().equals(label))
                    .findFirst()
                    .orElseGet(() -> {
                        var isp = new InfoSpotPoster();
                        isp.setLabel(label);
                        return isp;
                    });

            if (input.containsKey(LINES)) {
                poster.setLines((String) input.get(LINES));
            }

            if (input.containsKey(POSTER_SIZE)) {
                poster.setPosterSize((PosterSizeEnumeration) input.get(POSTER_SIZE));
            }

            return infoSpotPosterVersionedSaverService.saveNewVersion(poster);
        }
        else {
            throw new IllegalArgumentException("Expected label for poster, none provided");
        }
    }
}
