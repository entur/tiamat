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
