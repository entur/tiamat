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

import java.util.Collection;

@Service
public class AlternativeNamesMerger {

    public void mergeAlternativeNames(Collection<AlternativeName> fromAlternativeNames, Collection<AlternativeName> toAlternativeNames) {
        if (fromAlternativeNames != null) {
            fromAlternativeNames.forEach(altName -> {
                AlternativeName mergedAltName = new AlternativeName();
                ObjectMerger.copyPropertiesNotNull(altName, mergedAltName);
                toAlternativeNames.add(mergedAltName);
            });
        }
    }
}
