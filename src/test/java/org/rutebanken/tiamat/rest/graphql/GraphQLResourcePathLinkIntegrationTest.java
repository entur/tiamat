package org.rutebanken.tiamat.rest.graphql;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import org.junit.Test;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.*;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;

public class GraphQLResourcePathLinkIntegrationTest extends AbstractGraphQLResourceIntegrationTest {

    @Autowired
    private QuayRepository quayRepository;

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
    public void findPathLinkFromStopPlaceId() throws Exception {

        Quay firstQuay = new Quay();
        firstQuay.setCentroid(geometryFactory.createPoint(new Coordinate(5, 60)));
        firstQuay.setDescription(new EmbeddableMultilingualString("This is the first quay"));

        Quay secondQuay = new Quay();
        secondQuay.setCentroid(geometryFactory.createPoint(new Coordinate(5.1, 60.1)));
        secondQuay.setDescription(new EmbeddableMultilingualString("This is the second quay"));

        StopPlace stopPlace = new StopPlace();
        stopPlace.getQuays().add(firstQuay);
        stopPlace.getQuays().add(secondQuay);

        stopPlaceRepository.save(stopPlace);

        PathLink pathLink = new PathLink(new PathLinkEnd(firstQuay), new PathLinkEnd(secondQuay));
        pathLinkRepository.save(pathLink);

        String graphQlJsonQuery = "{" +
                "\"query\":\"" +
                "{ pathLink:" + GraphQLNames.FIND_PATH_LINK + " (stopPlaceId:\\\"" + NetexIdMapper.getNetexId(stopPlace) + "\\\") {" +
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
                "  pathLink: " + MUTATE_PATH_LINK + "(PathLink: [{ " +
                "       from: {quay: {id: \\\"" + NetexIdMapper.getNetexId(firstQuay) + "\\\"}}, " +
                "       to: {quay: {id: \\\"" + NetexIdMapper.getNetexId(secondQuay) + "\\\"}}, " +
                "       geometry: {" +
                "           type: LineString, coordinates: [[10.3, 59.9], [10.3, 59.9], [10.3, 59.9], [10.3, 59.9], [10.3, 59.9]] " +
                "       }" +
                "   }]) {" +
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

    @Test
    public void updatePathLinkWithTransferDurationWithoutClearingLineString() throws Exception {

        String graphQlJsonQuery = "{" +
                "\"query\":\"mutation { " +
                "  pathLink: " + MUTATE_PATH_LINK + "(PathLink: [{ " +
                "       geometry: {" +
                "           type: LineString, coordinates: [[10.3, 59.9], [10.3, 59.9], [10.3, 59.9], [10.3, 59.9], [10.3, 59.9]] " +
                "       }" +
                "   }]) {" +
                "   id " +
                "   geometry {" +
                "        type" +
                "        coordinates" +
                "       }" +
                "  }" +
                "}\",\"variables\":\"\"}";

        String pathLinkId = executeGraphQL(graphQlJsonQuery)
                .root("data.pathLink[0]")
                    .body("id", notNullValue())
                    .body("geometry", notNullValue())
                    .extract().path("data.pathLink[0].id");

        System.out.println("Got path link ID: "+ pathLinkId);

        String secondGraphQlJsonQuery = "{" +
                "\"query\":\"mutation { " +
                "  pathLink: " + MUTATE_PATH_LINK + "(PathLink: { " +
                "       id: \\\""+pathLinkId+"\\\"," +
                "       "+TRANSFER_DURATION+": {" +
                "           " + DEFAULT_DURATION + ": 1," +
                "           " + FREQUENT_TRAVELLER_DURATION + ": 2" +
                "       }" +
                "   }) {" +
                "   id " +
                "   geometry {" +
                "        type" +
                "        coordinates" +
                "       }" +
                "   " + TRANSFER_DURATION + " { " + DEFAULT_DURATION + " " + FREQUENT_TRAVELLER_DURATION + " }" +
                "  }" +
                "}\",\"variables\":\"\"}";

        executeGraphQL(secondGraphQlJsonQuery)
                .root("data.pathLink[0]")
                    .body("id", notNullValue())
                    .body("geometry", notNullValue())
                    .body("transferDuration", notNullValue())
                    .body("transferDuration." + DEFAULT_DURATION, notNullValue())
                    .body("transferDuration." + FREQUENT_TRAVELLER_DURATION, notNullValue())
                .extract().path("id");

    }
}
