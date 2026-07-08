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
import org.locationtech.jts.geom.Coordinate;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.tag.Tag;
import org.rutebanken.tiamat.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class GraphQLResourceTagIntegrationTest extends AbstractGraphQLResourceIntegrationTest {

    @Autowired
    private TagRepository tagRepository;

    @Test
    public void addTagToStop() {

        String stopPlaceName = "StopPlace";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));

        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(5, 60)));
        stopPlaceRepository.save(stopPlace);

        String tagName = "followup";
        String comment = "comment for tag";

        String graphQlJsonQuery = "{" +
                "\"query\":\"mutation { " +
                "  tag: " + GraphQLNames.CREATE_TAG + " (idReference:\\\"" + stopPlace.getNetexId() + "\\\", name: \\\"" + tagName + "\\\", comment: \\\"" + comment + "\\\") { " +
                "       idReference" +
                "       name" +
                "       comment" +
                "       created" +
                "       removed" +
                "       removedBy" +
                "  } " +
                "}\",\"variables\":\"\"}";


        executeGraphQL(graphQlJsonQuery)
                .body("data.tag.name", equalTo(tagName))
                .body("data.tag.comment", equalTo(comment))
                .body("data.tag.idReference", equalTo(stopPlace.getNetexId()))
                .body("data.tag.created", notNullValue())
                .body("data.tag.removed", nullValue())
                .body("data.tag.removedBy", nullValue());
    }

    @Test
    public void removeTagFromStop() {

        String stopPlaceName = "StopPlace";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));

        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(5, 60)));
        stopPlaceRepository.save(stopPlace);

        String tagName = "tagname";
        Tag tag = new Tag();
        tag.setName(tagName);
        tag.setIdreference(stopPlace.getNetexId());
        tag.setCreated(Instant.now());
        tagRepository.save(tag);

        String graphQlJsonQuery = "{" +
                "\"query\":\"mutation { " +
                "  tag: " + GraphQLNames.REMOVE_TAG + " (idReference:\\\"" + stopPlace.getNetexId() + "\\\", name: \\\"" + tagName + "\\\") { " +
                "       idReference" +
                "       name" +
                "       created" +
                "       removed" +
                "       removedBy" +
                "  } " +
                "}\",\"variables\":\"\"}";


        executeGraphQL(graphQlJsonQuery)
                .body("data.tag.name", equalTo(tagName))
                .body("data.tag.idReference", equalTo(stopPlace.getNetexId()))
                .body("data.tag.created", notNullValue())
                .body("data.tag.removed", notNullValue());
        // user name not available in test: .body("data.tag.removedBy", notNullValue());
    }
}
