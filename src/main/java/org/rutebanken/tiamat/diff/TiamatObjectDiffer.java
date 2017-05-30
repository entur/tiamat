package org.rutebanken.tiamat.diff;

import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Geometry;
import org.rutebanken.tiamat.diff.generic.Difference;
import org.rutebanken.tiamat.diff.generic.GenericDiffConfig;
import org.rutebanken.tiamat.diff.generic.GenericObjectDiffer;
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

    public void logDifference(IdentifiedEntity oldObject, IdentifiedEntity newObject) {
        try {
            List<Difference> differences = genericObjectDiffer.compareObjects(oldObject, newObject, genericDiffConfig);
            String diffString = genericObjectDiffer.diffListToString(differences);
            logger.info("Difference from previous version of {}: {}", oldObject.getNetexId(), diffString);
        } catch (Exception e) {
            logger.warn("Could not diff objects. Old object: {}. New object: {}", oldObject, newObject, e);
        }
    }
}
