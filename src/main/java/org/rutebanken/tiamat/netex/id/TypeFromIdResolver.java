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

package org.rutebanken.tiamat.netex.id;

import org.rutebanken.tiamat.model.DataManagedObjectStructure;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.springframework.stereotype.Component;

@Component
public class TypeFromIdResolver {

    public <T extends EntityInVersionStructure> Class<T> resolveClassFromId(String netexId) {

        String memberClass = NetexIdHelper.extractIdType(netexId);
        String canonicalName = EntityInVersionStructure.class.getPackage().getName() + "." + memberClass;
        try {
            @SuppressWarnings("unchecked")
            Class<T> clazz =  (Class<T>) Class.forName(canonicalName);
            return clazz;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Type " + canonicalName + " (member class " + memberClass +") cannot be found", e);
        }
    }
}
