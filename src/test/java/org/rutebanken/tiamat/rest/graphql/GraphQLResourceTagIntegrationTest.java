package org.rutebanken.tiamat.rest.graphql;

import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.tag.Tag;
import org.rutebanken.tiamat.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;

import static org.hamcrest.Matchers.*;

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

        String graphQlJsonQuery = """
                mutation {
                  tag: %s (
                    idReference:"%s",
                    name:"%s",
                    comment:"%s"
                    )
                    {
                       idReference
                       name
                       comment
                       created
                       removed
                       removedBy
                    }
                }
                """
                .formatted(GraphQLNames.CREATE_TAG, stopPlace.getNetexId(), tagName, comment);

        executeGraphQLQueryOnly(graphQlJsonQuery)
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

        String graphQlJsonQuery = """
                mutation {
                    tag: %s (
                        idReference:"%s",
                        name:"%s"
                    )
                    {
                        idReference
                        name
                        created
                        removed
                        removedBy
                    }
                }
                """
                .formatted(GraphQLNames.REMOVE_TAG, stopPlace.getNetexId(), tagName);

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .body("data.tag.name", equalTo(tagName))
                .body("data.tag.idReference", equalTo(stopPlace.getNetexId()))
                .body("data.tag.created", notNullValue())
                .body("data.tag.removed", notNullValue());
        // user name not available in test: .body("data.tag.removedBy", notNullValue());
    }
}
