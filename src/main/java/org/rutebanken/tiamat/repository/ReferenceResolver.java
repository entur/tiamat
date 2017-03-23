package org.rutebanken.tiamat.repository;

import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.VersionOfObjectRefStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;

import static org.rutebanken.tiamat.model.VersionOfObjectRefStructure.ANY_VERSION;

@Component
public class ReferenceResolver {

    @Autowired
    private EntityInVersionStructureRepository entityInVersionStructureRepository;

    public <T extends EntityInVersionStructure> T resolve(VersionOfObjectRefStructure versionOfObjectRefStructure) {

        assertNotNull(versionOfObjectRefStructure, versionOfObjectRefStructure.getRef());
        assertNotNull(versionOfObjectRefStructure, versionOfObjectRefStructure.getVersion());

        String ref = versionOfObjectRefStructure.getRef();
        String memberClass = ref.substring(ref.indexOf(':') + 1, ref.lastIndexOf(':'));

        String canonicalName = EntityInVersionStructure.class.getPackage().getName() + "." + memberClass;
        try {
            @SuppressWarnings("unchecked")
            Class<T> clazz = (Class<T>) Class.forName(canonicalName);

            if (ANY_VERSION.equals(versionOfObjectRefStructure.getVersion())) {
                return entityInVersionStructureRepository.findFirstByNetexIdOrderByVersionDesc(ref, clazz);
            } else {
                long version = Long.valueOf(versionOfObjectRefStructure.getVersion());
                return entityInVersionStructureRepository.findFirstByNetexIdAndVersion(ref, version, clazz);
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Type " + canonicalName + " cannot be found", e);
        }
    }

    private void assertNotNull(VersionOfObjectRefStructure versionOfObjectRefStructure, String field) {
        if (field == null) {
            throw new IllegalArgumentException(field + " value cannot be null: " + versionOfObjectRefStructure);
        }
    }
}
