package org.rutebanken.tiamat.diff;


import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.container.CollectionChange;
import org.javers.core.metamodel.clazz.EntityDefinition;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.core.metamodel.object.ValueObjectId;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.PersistablePolygon;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Object's must have their netex ID set.
 */
@Component
public class EntityInVersionDiffer {

    private static final Logger logger = LoggerFactory.getLogger(EntityInVersionDiffer.class);

    private Javers javers;
    public EntityInVersionDiffer() {
        //javers = JaversBuilder.javers().registerEntity(new EntityDefinition(EntityInVersionStructure.class, "netexId", Arrays.asList("id"))).build();
        javers = JaversBuilder.javers().registerValueObject(IdentifiedEntity.class).registerValueObject(PersistablePolygon.class).build();
    }

    public Diff diff(EntityInVersionStructure first, EntityInVersionStructure secoond) {
        Diff diff = javers.compare(first, secoond);
        try {
            logger.trace("Diff: {}", customDiffString(diff));
        } catch(Exception e) {
            logger.trace("error creating custom diff string", e);
        }
        return diff;
    }

    /**
     * WIP: Attempt to print diff more pretty. Not completely implemented
     */
    public String customDiffString(Diff diff) {
        StringBuilder stringBuilder = new StringBuilder();
        if (diff.hasChanges()) {


            diff.getChanges().forEach(change -> {
                String globalId = change.getAffectedGlobalId().value();
                String field = globalId.substring(globalId.lastIndexOf("#")+1);


                if (change instanceof ValueChange) {
                    ValueChange valueChange = (ValueChange) change;

                    stringBuilder
                            .append(field)
                            .append(".")
                            .append(valueChange.getPropertyName())
                            .append(": '")
                            .append(valueChange.getLeft())
                            .append("' -> '")
                            .append(valueChange.getRight())
                            .append("'")
                            .append("\n");
                } else if (change instanceof CollectionChange) {
                    CollectionChange collectionChange = (CollectionChange) change;

                    if (!collectionChange.getRemovedValues().isEmpty()) {
                        stringBuilder.append(collectionChange.getPropertyName()).append(" removed:\n\t");
                        appendAddedOrRemoved(collectionChange.getRemovedValues(), stringBuilder);
                        stringBuilder.append("\n");
                    }

                    if (!collectionChange.getAddedValues().isEmpty()) {
                        stringBuilder.append(collectionChange.getPropertyName()).append(" added:\n\t");
                        appendAddedOrRemoved(collectionChange.getAddedValues(), stringBuilder);
                        stringBuilder.append("\n");
                    }
                }

            });
        }
        return stringBuilder.toString();
    }

    private void appendAddedOrRemoved(List<?> values, StringBuilder stringBuilder) {
        values.forEach(o -> stringBuilder.append(collectionChange((ValueObjectId) o)));
    }

    private String collectionChange(ValueObjectId addedOrRemoved) {
        return addedOrRemoved.getOwnerId().toString();
    }
}
