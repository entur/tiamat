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

package org.rutebanken.tiamat.service.merge;

import org.rutebanken.tiamat.model.AlternativeName;
import org.rutebanken.tiamat.service.ObjectMerger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlternativeNamesMerger {

    public void mergeAlternativeNames(List<AlternativeName> fromAlternativeNames, List<AlternativeName> toAlternativeNames) {
        if (fromAlternativeNames != null) {
            fromAlternativeNames.forEach(altName -> {
                AlternativeName mergedAltName = new AlternativeName();
                ObjectMerger.copyPropertiesNotNull(altName, mergedAltName);
                // Reset identity fields to ensure a new entity is created
                // This prevents unique constraint violation on (netex_id, version)
                mergedAltName.setNetexId(null);
                mergedAltName.setVersion(0L);
                toAlternativeNames.add(mergedAltName);
            });
        }
    }
}
