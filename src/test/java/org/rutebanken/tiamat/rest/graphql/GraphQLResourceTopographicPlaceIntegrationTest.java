package org.rutebanken.tiamat.rest.graphql;

import org.junit.Test;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

public class GraphQLResourceTopographicPlaceIntegrationTest extends AbstractGraphQLResourceIntegrationTest {

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;

    @Test
    public void getTopographicPlaces() {

        TopographicPlace topographicPlace = new TopographicPlace();
        topographicPlace.setName(new EmbeddableMultilingualString("Fylke"));
        topographicPlace.setTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY);

        topographicPlaceRepository.save(topographicPlace);

        String query = """
                {
                    topographicPlace: %s {
                      id
                      name {
                        value
                        __typename
                      }
                      topographicPlaceType
                      __typename
                    }
                }
                """.formatted(GraphQLNames.TOPOGRAPHIC_PLACE);

        executeGraphQLQueryOnly(query)
                .body("data.topographicPlace.find { it.id == \"" + topographicPlace.getNetexId() + "\" } ", notNullValue());
    }

    @Test
    public void getTopographicPlaceByName() {

        TopographicPlace topographicPlace = new TopographicPlace();
        topographicPlace.setName(new EmbeddableMultilingualString("Vestfold"));
        topographicPlace.setTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY);

        topographicPlaceRepository.save(topographicPlace);

        String query = """
                {
                    topographicPlace: %s (
                        query: "vestfolD"
                    )
                    {
                        id
                        name {
                            value
                            __typename
                        }
                        topographicPlaceType
                        __typename
                    }
                }
                """
                .formatted(GraphQLNames.TOPOGRAPHIC_PLACE);

        executeGraphQLQueryOnly(query)
                .body("data.topographicPlace.find { it.id == \"" + topographicPlace.getNetexId() + "\" } ", notNullValue());
    }

    @Test
    public void getTopographicPlaceByNameNoMatch() {

        TopographicPlace topographicPlace = new TopographicPlace();
        topographicPlace.setName(new EmbeddableMultilingualString("Oppfold"));
        topographicPlace.setTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY);

        topographicPlaceRepository.save(topographicPlace);

        String query = """
                {
                    topographicPlace: %s (
                        query: "SomethingElse"
                    )
                    {
                        id
                        name {
                            value
                            __typename
                        }
                        topographicPlaceType
                        __typename
                    }
                }
                """
                .formatted(GraphQLNames.TOPOGRAPHIC_PLACE);

        executeGraphQLQueryOnly(query)
                .body("data.topographicPlace ", hasSize(0));
    }

    @Test
    public void getTopographicPlaceByPartOfName() {

        TopographicPlace topographicPlace = new TopographicPlace();
        topographicPlace.setName(new EmbeddableMultilingualString("Vestfold"));
        topographicPlace.setTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY);

        topographicPlaceRepository.save(topographicPlace);

        String query = """
                {
                    topographicPlace: %s (
                        query: "tfolD"
                    )
                    {
                        id
                        name {
                            value
                            __typename
                        }
                        topographicPlaceType
                        __typename
                    }
                }
                """
                .formatted(GraphQLNames.TOPOGRAPHIC_PLACE);

        executeGraphQLQueryOnly(query)
                .body("data.topographicPlace.find { it.id == \"" + topographicPlace.getNetexId() + "\" } ", notNullValue());
    }

    @Test
    public void getTopographicPlaceByType() {

        TopographicPlace topographicPlace = new TopographicPlace();
        topographicPlace.setTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY);

        topographicPlaceRepository.save(topographicPlace);

        String query = """
                {
                    topographicPlace: %s (
                        topographicPlaceType: county
                    )
                    {
                        id
                        topographicPlaceType
                        __typename
                    }
                }
                """
                .formatted(GraphQLNames.TOPOGRAPHIC_PLACE);

        executeGraphQLQueryOnly(query)
                .body("data.topographicPlace.find { it.id == \"" + topographicPlace.getNetexId() + "\" } ", notNullValue());
    }

    @Test
    public void getTopographicPlaceByTypeAndName() {

        TopographicPlace topographicPlace = new TopographicPlace();
        topographicPlace.setTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY);
        topographicPlace.setName(new EmbeddableMultilingualString("Somewhere"));

        topographicPlaceRepository.save(topographicPlace);

        String query = """
                {
                    topographicPlace: %s (
                        topographicPlaceType: county,
                        query: "Somewhere"
                    )
                    {
                        id
                        name {
                            value
                            __typename
                        }
                        topographicPlaceType
                        __typename
                    }
                }"""
                .formatted(GraphQLNames.TOPOGRAPHIC_PLACE);

        executeGraphQLQueryOnly(query)
                .body("data.topographicPlace.find { it.id == \"" + topographicPlace.getNetexId() + "\" } ", notNullValue());
    }
}