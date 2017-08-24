package org.rutebanken.tiamat.rest.graphql.fetchers;

import com.google.common.collect.Sets;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;
import org.rutebanken.tiamat.model.tag.Tag;
import org.rutebanken.tiamat.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toSet;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TAG_NAME;

@Component
public class TagFetcher implements DataFetcher<Set<Tag>> {

    @Autowired
    private TagRepository tagRepository;

    @Override
    public Set<Tag> get(DataFetchingEnvironment dataFetchingEnvironment) {
        if(dataFetchingEnvironment.getSource() instanceof IdentifiedEntity) {
            IdentifiedEntity source = (IdentifiedEntity) dataFetchingEnvironment.getSource();
            if (source != null) {
                return tagRepository.findByIdReference(source.getNetexId())
                        .stream()
                        .filter(tag -> tag.getRemoved() == null)
                        .collect(toSet());
            }
            return Sets.newHashSet();
        } else if (dataFetchingEnvironment.getArgument(TAG_NAME) != null) {

            String tagName = dataFetchingEnvironment.getArgument(TAG_NAME);
            return tagRepository.findByName(tagName)
                    .stream()
                    .filter(tag -> tag.getRemoved() == null)
                    .filter(distinctByKey(Tag::getName))
                    .map(tag -> {
                        // Remove fields not relevant for tag suggestions
                        Tag tagSuggestion = new Tag();
                        tagSuggestion.setName(tag.getName());
                        return tagSuggestion;
                    })
                    .collect(toSet());
        } else {
            return Sets.newHashSet();
        }
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
