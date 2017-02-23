package org.rutebanken.tiamat.rest.graphql.resolver;

import org.rutebanken.tiamat.model.PathLink;
import org.rutebanken.tiamat.model.PathLinkEnd;
import org.rutebanken.tiamat.model.Quay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.GEOMETRY;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ID;

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
        idResolver.extractAndSetId(ID, input, pathLink);

        if(input.get("from") != null) {
            pathLink.setFrom(mapToPathLinkEnd("from", input));
        }

        if(input.get("to") != null) {
            pathLink.setTo(mapToPathLinkEnd("to", input));
        }

        if(input.get(GEOMETRY) != null) {
            pathLink.setLineString(geometryResolver.createGeoJsonLineString((Map) input.get(GEOMETRY)));
        }

        // TODO
        // transfer duration
        // allowed use


        return pathLink;
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
        idResolver.extractAndSetId(ID, input, pathLinkEnd);

        if(input.get("quay") != null) {
            Optional<Long> quayId = idResolver.extractIdIfPresent(ID, (Map) input.get("quay"));
            if(quayId.isPresent()) {
                Quay quay = new Quay();
                quay.setId(quayId.get());
                pathLinkEnd.setQuay(quay);
            }
        } else {
            logger.warn("Could not resolve Quay. Stop Place is not supported yet. Input was: {}", input);
        }

        logger.trace("Mapped {}", pathLinkEnd);
        return pathLinkEnd;
    }
}
