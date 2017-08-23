package org.rutebanken.tiamat.rest.graphql.fetchers;

import com.google.common.collect.Sets;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;
import org.rutebanken.tiamat.model.tag.Tag;
import org.rutebanken.tiamat.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class TagFetcher implements DataFetcher<Set<Tag>> {

    @Autowired
    private TagRepository tagRepository;

    @Override
    public Set<Tag> get(DataFetchingEnvironment dataFetchingEnvironment) {
        IdentifiedEntity source = (IdentifiedEntity) dataFetchingEnvironment.getSource();
        if (source != null) {
            return tagRepository.findByIdReference(source.getNetexId());
        } else {
            return Sets.newHashSet();
        }
    }
}
