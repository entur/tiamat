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

package org.rutebanken.tiamat.diff;

import com.google.common.collect.Sets;
import org.locationtech.jts.geom.Geometry;
import org.rutebanken.tiamat.diff.generic.Difference;
import org.rutebanken.tiamat.diff.generic.GenericDiffConfig;
import org.rutebanken.tiamat.diff.generic.GenericObjectDiffer;
import org.rutebanken.tiamat.model.DataManagedObjectStructure;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Diff objects by using configuration suited for Tiamat model.
 */
@Service
public class TiamatObjectDiffer {

    private static final Logger logger = LoggerFactory.getLogger(TiamatObjectDiffer.class);

    private static final Set<String> DIFF_IGNORE_FIELDS = Sets.newHashSet("id", "version", "changed", "status", "modification", "envelope");

    private final GenericDiffConfig genericDiffConfig;

    private final GenericObjectDiffer genericObjectDiffer;

    @Autowired
    public TiamatObjectDiffer(GenericObjectDiffer genericObjectDiffer) {
        this.genericObjectDiffer = genericObjectDiffer;
        this.genericDiffConfig = GenericDiffConfig.builder()
                .identifiers(Sets.newHashSet("netexId", "ref"))
                .ignoreFields(DIFF_IGNORE_FIELDS)
                .onlyDoEqualsCheck(Sets.newHashSet(Geometry.class))
                .build();
    }

    public List<Difference> compareObjects(IdentifiedEntity oldObject, IdentifiedEntity newObject) throws IllegalAccessException {
        return genericObjectDiffer.compareObjects(oldObject, newObject, genericDiffConfig);
    }

    public void logDifference(IdentifiedEntity oldObject, IdentifiedEntity newObject) {
        try {
            List<Difference> differences = genericObjectDiffer.compareObjects(oldObject, newObject, genericDiffConfig);
            String diffString = genericObjectDiffer.diffListToString(differences);
            String changedByLogString = "";
            if (newObject instanceof DataManagedObjectStructure) {
                changedByLogString = " - changes made by '" + ((DataManagedObjectStructure) newObject).getChangedBy() + "'";
            }
            logger.info("Difference from previous version of {}{}: {}", oldObject.getNetexId(), changedByLogString, diffString);
        } catch (Exception e) {
            logger.warn("Could not diff objects. Old object: {}. New object: {}", oldObject, newObject, e);
        }
    }
}
