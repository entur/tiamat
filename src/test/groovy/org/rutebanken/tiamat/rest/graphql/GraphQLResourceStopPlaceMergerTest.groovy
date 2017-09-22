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

package org.rutebanken.tiamat.rest.graphql;

import com.vividsolutions.jts.geom.Coordinate;
import org.junit.Test;
import org.rutebanken.tiamat.model.*;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

public class GraphQLResourceStopPlaceMergerTest extends AbstractGraphQLResourceIntegrationTest {

    @Test
    public void mergeStopPlaces() {

        StopPlace fromStopPlace = new StopPlace();
        fromStopPlace.setName(new EmbeddableMultilingualString("Name"));
        fromStopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)));
        fromStopPlace.getOriginalIds().add("TEST:StopPlace:1234");
        fromStopPlace.getOriginalIds().add("TEST:StopPlace:5678");

        AlternativeName altName = new AlternativeName();
        altName.setNameType(NameTypeEnumeration.ALIAS);
        altName.setName(new EmbeddableMultilingualString("Navn", "no"));

        AlternativeName altName2 = new AlternativeName();
        altName2.setNameType(NameTypeEnumeration.ALIAS);
        altName2.setName(new EmbeddableMultilingualString("Name", "en"));

        fromStopPlace.getAlternativeNames().add(altName);
        fromStopPlace.getAlternativeNames().add(altName2);

        Quay fromQuay = new Quay();
        fromQuay.setCompassBearing(new Float(90));
        fromQuay.setCentroid(geometryFactory.createPoint(new Coordinate(11.2, 60.2)));
        fromQuay.getOriginalIds().add("TEST:Quay:123401");
        fromQuay.getOriginalIds().add("TEST:Quay:567801");

        fromStopPlace.getQuays().add(fromQuay);


        StopPlace toStopPlace = new StopPlace();
        toStopPlace.setName(new EmbeddableMultilingualString("Name 2"));
        toStopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.11, 60.11)));
        toStopPlace.getOriginalIds().add("TEST:StopPlace:4321");
        toStopPlace.getOriginalIds().add("TEST:StopPlace:8765");

        AlternativeName toAltName = new AlternativeName();
        toAltName.setNameType(NameTypeEnumeration.ALIAS);
        toAltName.setName(new EmbeddableMultilingualString("Navn2", "no"));

        toStopPlace.getAlternativeNames().add(toAltName);

        Quay toQuay = new Quay();
        toQuay.setCompassBearing(new Float(90));
        toQuay.setCentroid(geometryFactory.createPoint(new Coordinate(11.21, 60.21)));
        toQuay.getAlternativeNames().add(toAltName);
        toQuay.getOriginalIds().add("TEST:Quay:432101");
        toQuay.getOriginalIds().add("TEST:Quay:876501");

        toStopPlace.getQuays().add(toQuay);


        fromStopPlace = saveStopPlaceTransactional(fromStopPlace);
        toStopPlace = saveStopPlaceTransactional(toStopPlace);

        //Calling GraphQL-api to merge StopPlaces
        String graphQlJsonQuery = """
                mutation {
                  stopPlace: mergeStopPlaces (
                          fromStopPlaceId: "${fromStopPlace.getNetexId()}",
                          toStopPlaceId:"${toStopPlace.getNetexId()}"
                       ) {
                      id
                      importedId

                      ...on StopPlace {
                          name { value }
                          quays {
                            id
                            geometry { type coordinates }
                            compassBearing
                          }
                      }
                    }
                }
                """;

        Set<String> originalIds = new HashSet<>();
        originalIds.addAll(fromStopPlace.getOriginalIds());
        originalIds.addAll(toStopPlace.getOriginalIds());

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace.id", comparesEqualTo(toStopPlace.getNetexId()))
                .body("data.stopPlace.importedId", containsInAnyOrder(originalIds.toArray()))
                .body("data.stopPlace.quays", hasSize(fromStopPlace.getQuays().size() + toStopPlace.getQuays().size()));


    }
}
