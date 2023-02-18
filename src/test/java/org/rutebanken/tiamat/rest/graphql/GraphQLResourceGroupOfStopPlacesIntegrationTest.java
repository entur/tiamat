package org.rutebanken.tiamat.rest.graphql;

import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

public class GraphQLResourceGroupOfStopPlacesIntegrationTest extends AbstractGraphQLResourceIntegrationTest {

    @Test
    public void createNewGroupOfStopPlaces() {
        var stopPlace1 = new StopPlace();
        stopPlace1.setCentroid(geometryFactory.createPoint(new Coordinate(12, 53)));
        stopPlace1.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace1);

        var stopPlace2 = new StopPlace();
        stopPlace2.setCentroid(geometryFactory.createPoint(new Coordinate(13, 61)));
        stopPlace2.setStopPlaceType(StopTypeEnumeration.TRAM_STATION);
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace2);

        var groupName = "Group name";
        var versionComment = "VersionComment";

        var graphQlJsonQuery = """
                    mutation {
                    group: ${MUTATE_GROUP_OF_STOP_PLACES}(GroupOfStopPlaces: {
                        name: {value: "${groupName}"},
                        versionComment: "${versionComment}",
                        members: [
                            {ref: "${stopPlace1.getNetexId()}"},
                            {ref: "${stopPlace2.getNetexId()}"}],
                        }) {
                    id
                    version
                    versionComment
                    name {
                      value
                    }
                    members {
                      id
                      name {
                        value
                      }
                      version
                      ... on StopPlace {
                        stopPlaceType
                      }
                    }
                  }
                }
                """;

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .body("data.group.name.value", equalTo(groupName))
                .body("data.group.versionComment", equalTo(versionComment))
                .root("data.group.members.find { it.id == '" + stopPlace2.getNetexId() + "'}")
                .body("version", equalTo(String.valueOf(stopPlace2.getVersion())))
                .body("stopPlaceType", equalTo(StopTypeEnumeration.TRAM_STATION.value()))
                .body("name", nullValue())
                .root("data.group.members.find { it.id == '" + stopPlace1.getNetexId() + "'}")
                .body("name", nullValue())
                .body("stopPlaceType", equalTo(StopTypeEnumeration.BUS_STATION.value()))
                .body("version", equalTo(String.valueOf(stopPlace1.getVersion())));
    }
}
