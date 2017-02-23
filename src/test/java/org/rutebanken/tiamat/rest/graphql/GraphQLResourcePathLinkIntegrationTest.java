package org.rutebanken.tiamat.rest.graphql;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import graphql.GraphQL;
import org.junit.Test;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.PathLink;
import org.rutebanken.tiamat.model.PathLinkEnd;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.repository.PathLinkRepository;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.*;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.MUTATE_PATH_LINK;

public class GraphQLResourcePathLinkIntegrationTest extends GraphQLResourceIntegrationTest {

    @Autowired
    private QuayRepository quayRepository;

    @Autowired
    private PathLinkRepository pathLinkRepository;

    @Test
    public void retrievePathLinkReferencingTwoQuays() throws Exception {
        Quay firstQuay = new Quay();
        firstQuay.setCentroid(geometryFactory.createPoint(new Coordinate(5, 60)));
        firstQuay.setDescription(new EmbeddableMultilingualString("This is the first quay"));
        quayRepository.save(firstQuay);

        Quay secondQuay = new Quay();
        secondQuay.setCentroid(geometryFactory.createPoint(new Coordinate(5.1, 60.1)));
        secondQuay.setDescription(new EmbeddableMultilingualString("This is the second quay"));
        quayRepository.save(secondQuay);

        PathLink pathLink = new PathLink(new PathLinkEnd(firstQuay), new PathLinkEnd(secondQuay));
        Coordinate[] coordinates = new Coordinate[2];
        coordinates[0] = new Coordinate(11, 60);
        coordinates[1] = new Coordinate(11.1, 60.1);

        CoordinateSequence points = new CoordinateArraySequence(coordinates);

        LineString lineString = new LineString(points, geometryFactory);
        pathLink.setLineString(lineString);

        pathLinkRepository.save(pathLink);

        String graphQlJsonQuery = "{" +
                "\"query\":\"" +
                "{ pathLink:" + GraphQLNames.FIND_PATH_LINK + " (id:\\\"" + NetexIdMapper.getNetexId(pathLink) + "\\\") {" +
                "   id " +
                "    from {" +
                "      id" +
                "      quay {" +
                "        id" +
                "        description {value}" +
                "      }" +
                "    }" +
                "    to {" +
                "      id" +
                "      quay {" +
                "        id" +
                "        description {value}" +
                "      }" +
                "    }" +
                "}" +
                "}\",\"variables\":\"\"}";

        executeGraphQL(graphQlJsonQuery)
                .root("data.pathLink[0]")
                    .body("id", comparesEqualTo(NetexIdMapper.getNetexId(pathLink)))
                .root("data.pathLink[0].from")
                    .body("id", comparesEqualTo(NetexIdMapper.getNetexId(pathLink.getFrom())))
                    .body("quay.id", equalTo(NetexIdMapper.getNetexId(firstQuay)))
                    .body("quay.description.value", equalTo(firstQuay.getDescription().getValue()))
                .root("data.pathLink[0].to")
                    .body("id", comparesEqualTo(NetexIdMapper.getNetexId(pathLink.getTo())))
                    .body("quay.id", equalTo(NetexIdMapper.getNetexId(secondQuay)))
                    .body("quay.description.value", equalTo(secondQuay.getDescription().getValue()));
    }

    @Test
    public void createNewPathLinkBetweenQuays() throws Exception {
        Quay firstQuay = new Quay();
        firstQuay.setCentroid(geometryFactory.createPoint(new Coordinate(5, 60)));
        firstQuay.setDescription(new EmbeddableMultilingualString("This is the first quay"));
        quayRepository.save(firstQuay);

        Quay secondQuay = new Quay();
        secondQuay.setCentroid(geometryFactory.createPoint(new Coordinate(5.1, 60.1)));
        secondQuay.setDescription(new EmbeddableMultilingualString("This is the second quay"));
        quayRepository.save(secondQuay);

        String graphQlJsonQuery = "{" +
                "\"query\":\"mutation { " +
                "  pathLink: " + MUTATE_PATH_LINK + "(PathLink: { " +
                "       from: {quay: {id: \\\"" + NetexIdMapper.getNetexId(firstQuay) + "\\\"}}, " +
                "       to: {quay: {id: \\\"" + NetexIdMapper.getNetexId(secondQuay) + "\\\"}}, " +
                "       geometry: {" +
                "           type: \\\"LineString\\\", coordinates: [[10.3, 59.9], [10.3, 59.9], [10.3, 59.9], [10.3, 59.9], [10.3, 59.9]] " +
                "       }" +
                "   }) {" +
                "   id " +
                "   geometry {" +
                "        type" +
                "        coordinates" +
                "       }" +
                "    from {" +
                "      id" +
                "      quay {" +
                "        id" +
                "        description {value}" +
                "      }" +
                "    }" +
                "    to {" +
                "      id" +
                "      quay {" +
                "        id" +
                "        description {value}" +
                "      }" +
                "    }" +
                "  }" +
                "}\",\"variables\":\"\"}";

        executeGraphQL(graphQlJsonQuery)
                .root("data.pathLink[0]")
                    .body("id", notNullValue())
                    .body("geometry", notNullValue())
                .root("data.pathLink[0].from")
                    .body("id", notNullValue())
                    .body("quay.id", equalTo(NetexIdMapper.getNetexId(firstQuay)))
                    .body("quay.description.value", equalTo(firstQuay.getDescription().getValue()))
                .root("data.pathLink[0].to")
                    .body("id", notNullValue())
                    .body("quay.id", equalTo(NetexIdMapper.getNetexId(secondQuay)))
                    .body("quay.description.value", equalTo(secondQuay.getDescription().getValue()));
    }
}
