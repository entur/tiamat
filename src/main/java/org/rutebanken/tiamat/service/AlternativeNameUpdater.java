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

import org.rutebanken.tiamat.model.AlternativeName;
import org.rutebanken.tiamat.model.SiteElement;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AlternativeNameUpdater {

    public boolean updateAlternativeName(SiteElement entity, AlternativeName alternativeName) {
        boolean isUpdated = false;
        AlternativeName altName;


        if (alternativeName.getName() != null) {

            Optional<AlternativeName> existing = entity.getAlternativeNames()
                    .stream()
                    .filter(existingAlternativeName -> alternativeName != null)
                    .filter(existingAlternativeName -> alternativeName.getName() != null)
                    .filter(existingAlternativeName -> {
                        return (alternativeName.getName().getLang() != null &&
                                alternativeName.getName().getLang().equals(alternativeName.getName().getLang()) &&
                                alternativeName.getNameType() != null && alternativeName.getNameType().equals(alternativeName.getNameType()));
                    })
                    .findFirst();
            if (existing.isPresent()) {
                altName = existing.get();
            } else {
                altName = new AlternativeName();
            }
            if (alternativeName.getName().getValue() != null) {
                altName.setName(alternativeName.getName());
                altName.setNameType(alternativeName.getNameType());
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
