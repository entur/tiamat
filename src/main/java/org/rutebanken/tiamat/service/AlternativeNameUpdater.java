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

package org.rutebanken.tiamat.service;

import org.apache.commons.lang3.tuple.Pair;
import org.rutebanken.tiamat.model.AlternativeName;
import org.rutebanken.tiamat.model.EntityWithAlternativeNames;
import org.rutebanken.tiamat.model.NameTypeEnumeration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class AlternativeNameUpdater {

    private static final Logger logger = LoggerFactory.getLogger(AlternativeNameUpdater.class);

    /**
     * Saving alternative names always overwrites existing alternative names,
     * except when the incoming alternative name matches an existing one.
     *
     * @param entity           with alternative names
     * @param alternativeNames incoming alternative names
     * @return if alternative names were updated
     */
    public boolean updateAlternativeNames(EntityWithAlternativeNames entity, List<AlternativeName> alternativeNames) {
        final AtomicInteger matchedExisting = new AtomicInteger();

        avoidDuplicateTranslationsPerLanguage(alternativeNames);


        List<AlternativeName> result = alternativeNames.stream()
                .map(incomingAlternativeName -> {
                    Optional<AlternativeName> optionalExisting = matchExisting(entity, incomingAlternativeName);

                    if (optionalExisting.isPresent()) {
                        AlternativeName existingAlternativeName = optionalExisting.get();
                        logger.debug("Found matching alternative name on name and nametype. Keeping existing: {} incoming: {}",
                                existingAlternativeName, incomingAlternativeName);
                        return existingAlternativeName;
                    } else {
                        matchedExisting.incrementAndGet();
                        return incomingAlternativeName;
                    }
                })
                .collect(Collectors.toList());


        entity.getAlternativeNames().clear();
        entity.getAlternativeNames().addAll(result);

        return matchedExisting.get() > 0;
    }

    private void avoidDuplicateTranslationsPerLanguage(List<AlternativeName> alternativeNames) {
        // Avoid multiple translations per language
        alternativeNames.stream()
                .filter(a -> a.getNameType().equals(NameTypeEnumeration.TRANSLATION))
                .collect(Collectors.groupingBy(a -> Pair.of(a.getName().getLang(), a.getNameType())))
                .entrySet()
                .stream()
                .filter(pairListEntry -> pairListEntry.getValue().size() > 1)
                .findFirst()
                .ifPresent(entry -> {
                    throw new IllegalArgumentException("Multiple translations with the same language and type is not permitted: "
                            + entry.getKey() + " " + entry.getValue());
                });
    }

    private Optional<AlternativeName> matchExisting(EntityWithAlternativeNames entity, AlternativeName incomingAlternativeName) {
        return entity.getAlternativeNames()
                .stream()
                .filter(existingAlternativeName -> existingAlternativeName.getName().equals(incomingAlternativeName.getName())
                        && existingAlternativeName.getNameType().equals(incomingAlternativeName.getNameType()))
                .findFirst();
    }

}
