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

import org.junit.Test;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.hasSize;
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
                    "}\",\"variables\":\"\"}";

        System.out.println(query);

        executeGraphQL(query)
                .body("data.topographicPlace.find { it.id == \"" + topographicPlace.getNetexId() +"\" } ", notNullValue());


    }

    @Test
    public void getTopographicPlaceByName() {

        TopographicPlace topographicPlace = new TopographicPlace();
        topographicPlace.setName(new EmbeddableMultilingualString("Vestfold"));
        topographicPlace.setTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY);


        topographicPlaceRepository.save(topographicPlace);

        String query = "{" +
                "\"query\":\"" +
                "{ topographicPlace:" + GraphQLNames.TOPOGRAPHIC_PLACE + " (query: \\\"vestfolD\\\") {" +
                "    id" +
                "    name {" +
                "      value" +
                "      __typename" +
                "    }" +
                "    topographicPlaceType" +
                "    __typename" +
                "  }" +
                "}\",\"variables\":\"\"}";

        System.out.println(query);

        executeGraphQL(query)
                .body("data.topographicPlace.find { it.id == \"" + topographicPlace.getNetexId() +"\" } ", notNullValue());

    }

    @Test
    public void getTopographicPlaceByNameNoMatch() {

        TopographicPlace topographicPlace = new TopographicPlace();
        topographicPlace.setName(new EmbeddableMultilingualString("Oppfold"));
        topographicPlace.setTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY);


        topographicPlaceRepository.save(topographicPlace);

        String query = "{" +
                "\"query\":\"" +
                "{ topographicPlace:" + GraphQLNames.TOPOGRAPHIC_PLACE + " (query: \\\"SomethingElse\\\") {" +
                "    id" +
                "    name {" +
                "      value" +
                "      __typename" +
                "    }" +
                "    topographicPlaceType" +
                "    __typename" +
                "  }" +
                "}\",\"variables\":\"\"}";

        System.out.println(query);

        executeGraphQL(query)
                .body("data.topographicPlace ", hasSize(0));

    }

    @Test
    public void getTopographicPlaceByPartOfName() {

        TopographicPlace topographicPlace = new TopographicPlace();
        topographicPlace.setName(new EmbeddableMultilingualString("Vestfold"));
        topographicPlace.setTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY);


        topographicPlaceRepository.save(topographicPlace);

        String query = "{" +
                "\"query\":\"" +
                "{ topographicPlace:" + GraphQLNames.TOPOGRAPHIC_PLACE + " (query: \\\"tfolD\\\") {" +
                "    id" +
                "    name {" +
                "      value" +
                "      __typename" +
                "    }" +
                "    topographicPlaceType" +
                "    __typename" +
                "  }" +
                "}\",\"variables\":\"\"}";

        System.out.println(query);

        executeGraphQL(query)
                .body("data.topographicPlace.find { it.id == \"" + topographicPlace.getNetexId() +"\" } ", notNullValue());

    }

    @Test
    public void getTopographicPlaceByType() {

        TopographicPlace topographicPlace = new TopographicPlace();
        topographicPlace.setTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY);


        topographicPlaceRepository.save(topographicPlace);

        String query = "{" +
                "\"query\":\"" +
                "{ topographicPlace:" + GraphQLNames.TOPOGRAPHIC_PLACE + " (topographicPlaceType: county) {" +
                "    id" +
                "    topographicPlaceType" +
                "    __typename" +
                "  }" +
                "}\",\"variables\":\"\"}";

        System.out.println(query);

        executeGraphQL(query)
                .body("data.topographicPlace.find { it.id == \"" + topographicPlace.getNetexId() +"\" } ", notNullValue());
    }

    @Test
    public void getTopographicPlaceByTypeAndName() {

        TopographicPlace topographicPlace = new TopographicPlace();
        topographicPlace.setTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY);
        topographicPlace.setName(new EmbeddableMultilingualString("Somewhere"));


        topographicPlaceRepository.save(topographicPlace);

        String query = "{" +
                "\"query\":\"" +
                "{ topographicPlace:" + GraphQLNames.TOPOGRAPHIC_PLACE + " (topographicPlaceType: county, query: \\\"Somewhere\\\") {" +
                "    id" +
                "    name {" +
                "      value" +
                "      __typename" +
                "    }" +
                "    topographicPlaceType" +
                "    __typename" +
                "  }" +
                "}\",\"variables\":\"\"}";

        System.out.println(query);

        executeGraphQL(query)
                .body("data.topographicPlace.find { it.id == \"" + topographicPlace.getNetexId() +"\" } ", notNullValue());
    }
}
