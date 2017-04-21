package org.rutebanken.tiamat.rest.graphql;

import org.junit.Test;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class GraphQLResourceTopographicPlaceIntegrationTest extends AbstractGraphQLResourceIntegrationTest{

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;

    @Test
    public void getTopographicPlaces() {
        
        TopographicPlace topographicPlace = new TopographicPlace();
        topographicPlace.setName(new EmbeddableMultilingualString("Fylke"));
        topographicPlace.setTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY);

        
        topographicPlaceRepository.save(topographicPlace);
        
        String query = "{" +
                "\"query\":\"" +
                    "{ topographicPlace:" + GraphQLNames.TOPOGRAPHIC_PLACE + " {" +
                    "    id" +
                    "    name {" +
                    "      value" +
                    "      __typename" +
                    "    }" +
                    "    topographicPlaceType" +
                    "    __typename" +
                    "  }" +
                    "}" +
                    "}\",\"variables\":\"\"}";

        System.out.println(query);

        executeGraphQL(query)
                .body("data.topographicPlace.find { it.id == \"" + topographicPlace.getNetexId() +"\" } ", notNullValue());



    }
}
