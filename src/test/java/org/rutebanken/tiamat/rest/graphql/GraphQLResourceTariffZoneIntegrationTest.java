package org.rutebanken.tiamat.rest.graphql;

import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.TariffZone;
import org.rutebanken.tiamat.repository.TariffZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class GraphQLResourceTariffZoneIntegrationTest extends AbstractGraphQLResourceIntegrationTest {

    @Autowired
    private TariffZoneRepository tariffZoneRepository;

    @Test
    public void searchForTariffZone() {

        var tariffZone = new TariffZone();
        tariffZone.setNetexId("BRA:TariffZone:112");
        tariffZone.setName(new EmbeddableMultilingualString("Somewhere"));
        tariffZone.setVersion(1L);
        Coordinate[] coordinates = Arrays.asList(new Coordinate(0, 0), new Coordinate(1, 0), new Coordinate(1, 1), new Coordinate(0, 0)).toArray(new Coordinate[4]);
        tariffZone.setPolygon(new GeometryFactory().createPolygon(coordinates));
        tariffZoneRepository.save(tariffZone);

        String graphQlJsonQuery = """
                {
                    tariffZones(query:"112") {
                        id
                        name {value}
                        version
                        geometry {
                            type
                            coordinates
                        }
                        polygon {
                            type
                            coordinates
                        }
                    }
                }
                """;

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .root("data.tariffZones[0]")
                .body("name.value", equalTo(tariffZone.getName().getValue()))
                .body("id", equalTo(tariffZone.getNetexId()))
                .body("version", equalTo(Long.toString(tariffZone.getVersion())))
                .body("polygon", notNullValue());
    }
}