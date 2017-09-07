package org.rutebanken.tiamat.service.merge;

import org.rutebanken.tiamat.model.AlternativeName;
import org.rutebanken.tiamat.rest.graphql.helpers.ObjectMerger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlternativeNamesMerger {

    public void mergeAlternativeNames(List<AlternativeName> fromAlternativeNames, List<AlternativeName> toAlternativeNames) {
        if (fromAlternativeNames != null) {
            fromAlternativeNames.forEach(altName -> {
                AlternativeName mergedAltName = new AlternativeName();
                ObjectMerger.copyPropertiesNotNull(altName, mergedAltName);
                toAlternativeNames.add(mergedAltName);
            });
        }
    }
}
