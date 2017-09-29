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

package org.rutebanken.tiamat.versioning.util;

import org.rutebanken.tiamat.model.DataManagedObjectStructure;
import org.rutebanken.tiamat.model.VersionOfObjectRefStructure;
import org.rutebanken.tiamat.repository.reference.ReferenceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Component
public class ReferenceVersionUpdater {

    private static final Logger logger = LoggerFactory.getLogger(ReferenceVersionUpdater.class);

    @Autowired
    private ReferenceResolver referenceResolver;

    public <T extends VersionOfObjectRefStructure> Set<T> updateReferencesToNewestVersion(Set<T> listOfRefs, Class<T> clazz) {

        if(listOfRefs == null) {
            return null;
        }

        return listOfRefs.stream()
                .map(ref -> {
                    logger.debug("Updating reference to newest version {}", ref);
                    ref.setVersion(null);
                    DataManagedObjectStructure dataManagedObjectStructure = referenceResolver.resolve(ref);
                    try {
                        T newRef = clazz.newInstance();
                        newRef.setRef(dataManagedObjectStructure.getNetexId());
                        newRef.setVersion(String.valueOf(dataManagedObjectStructure.getVersion()));
                        return newRef;
                    } catch (InstantiationException|IllegalAccessException e) {
                        throw new RuntimeException("Cannot create new instance", e);
                    }
                })
                .collect(toSet());
    }

}
