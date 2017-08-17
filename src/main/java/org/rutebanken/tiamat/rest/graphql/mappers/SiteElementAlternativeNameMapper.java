package org.rutebanken.tiamat.rest.graphql.mappers;

import org.rutebanken.tiamat.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.mappers.EmbeddableMultilingualStringMapper.getEmbeddableString;

@Component
public class SiteElementAlternativeNameMapper {

    private static final Logger logger = LoggerFactory.getLogger(SiteElementAlternativeNameMapper.class);

    public boolean populateAlternativeNameFromInput(SiteElement entity, Map entry) {
        boolean isUpdated = false;
        AlternativeName altName;

        NameTypeEnumeration nameType = (NameTypeEnumeration) entry.getOrDefault(NAME_TYPE, NameTypeEnumeration.OTHER);
        EmbeddableMultilingualString name = getEmbeddableString((Map) entry.get(NAME));

        if (name != null) {

            Optional<AlternativeName> existing = entity.getAlternativeNames()
                    .stream()
                    .filter(alternativeName -> alternativeName != null)
                    .filter(alternativeName -> alternativeName.getName() != null)
                    .filter(alternativeName -> {
                        return (alternativeName.getName().getLang() != null &&
                                alternativeName.getName().getLang().equals(name.getLang()) &&
                                alternativeName.getNameType() != null && alternativeName.getNameType().equals(nameType));
                    })
                    .findFirst();
            if (existing.isPresent()) {
                altName = existing.get();
            } else {
                altName = new AlternativeName();
            }
            if (name.getValue() != null) {
                altName.setName(name);
                altName.setNameType(nameType);
                isUpdated = true;
            }

            if (altName.getName() == null || altName.getName().getValue() == null || altName.getName().getValue().isEmpty()) {
                entity.getAlternativeNames().remove(altName);
            } else if (isUpdated && altName.getNetexId() == null) {
                entity.getAlternativeNames().add(altName);
            }
        }

        return isUpdated;
    }
}
