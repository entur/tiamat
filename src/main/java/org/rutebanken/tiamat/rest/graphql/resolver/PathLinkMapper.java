package org.rutebanken.tiamat.rest.graphql.resolver;

import org.rutebanken.tiamat.model.PathLink;
import org.rutebanken.tiamat.model.PathLinkEnd;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.TransferDuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;

@Component
public class PathLinkMapper {

    private static final Logger logger = LoggerFactory.getLogger(PathLinkMapper.class);


    private final GeometryResolver geometryResolver;
    private final IdResolver idResolver;

    @Autowired
    public PathLinkMapper(GeometryResolver geometryResolver, IdResolver idResolver) {
        this.geometryResolver = geometryResolver;
        this.idResolver = idResolver;
    }

    public PathLink map(Map input) {

        PathLink pathLink = new PathLink();
        idResolver.extractAndSetNetexId(ID, input, pathLink);

        if(input.get(PATH_LINK_FROM) != null) {
            pathLink.setFrom(mapToPathLinkEnd(PATH_LINK_FROM, input));
        }

        if(input.get(PATH_LINK_TO) != null) {
            pathLink.setTo(mapToPathLinkEnd(PATH_LINK_TO, input));
        }

        if(input.get(GEOMETRY) != null) {
            pathLink.setLineString(geometryResolver.createGeoJsonLineString((Map) input.get(GEOMETRY)));
        }

        if(input.get(TRANSFER_DURATION) != null) {
            pathLink.setTransferDuration(mapToTransferDuration((Map) input.get(TRANSFER_DURATION)));
        }
        // TODO
        // allowed use


        return pathLink;
    }

    private TransferDuration mapToTransferDuration(Map input) {
        TransferDuration transferDuration = new TransferDuration();
        transferDuration.setFrequentTravellerDuration(ofSeconds(input, FREQUENT_TRAVELLER_DURATION));
        transferDuration.setMobilityRestrictedTravellerDuration(ofSeconds(input, MOBILITY_RESTRICTED_TRAVELLER_DURATION));
        transferDuration.setOccasionalTravellerDuration(ofSeconds(input, OCCASIONAL_TRAVELLER_DURATION));
        transferDuration.setDefaultDuration(ofSeconds(input, DEFAULT_DURATION));
        return transferDuration;
    }

    private Duration ofSeconds(Map input, String field) {
        if(input.get(field) != null) {
            return Duration.ofSeconds((Integer) input.get(field));
        }
        return null;
    }

    private PathLinkEnd mapToPathLinkEnd(String field, Map input) {
        if(input.get(field) != null) {
            PathLinkEnd pathLinkEnd = mapToPathLinkEnd((Map) input.get(field));
            return pathLinkEnd;
        }
        return null;
    }

    private PathLinkEnd mapToPathLinkEnd(Map input) {
        PathLinkEnd pathLinkEnd = new PathLinkEnd();
        idResolver.extractAndSetNetexId(ID, input, pathLinkEnd);

        if(input.get("quay") != null) {
            Optional<String> quayNetexId = idResolver.extractIdIfPresent(ID, (Map) input.get("quay"));
            if(quayNetexId.isPresent()) {
                Quay quay = new Quay();
                quay.setNetexId(quayNetexId.get());
                pathLinkEnd.setQuay(quay);
            }
        } else {
            logger.warn("Could not resolve Quay. Stop Place is not supported yet. Input was: {}", input);
        }

        logger.trace("Mapped {}", pathLinkEnd);
        return pathLinkEnd;
    }
}
