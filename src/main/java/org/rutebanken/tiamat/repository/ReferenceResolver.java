package org.rutebanken.tiamat.repository;

import org.apache.commons.lang.StringUtils;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.VersionOfObjectRefStructure;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;

import static org.rutebanken.tiamat.model.VersionOfObjectRefStructure.ANY_VERSION;

@Component
public class ReferenceResolver {

    private static final Logger logger = LoggerFactory.getLogger(ReferenceResolver.class);

    @Autowired
    private EntityInVersionStructureRepository entityInVersionStructureRepository;

    public <T extends EntityInVersionStructure> T resolve(VersionOfObjectRefStructure versionOfObjectRefStructure) {

        logger.debug("Received reference: {}", versionOfObjectRefStructure);

        assertNotNull(versionOfObjectRefStructure, "ref", versionOfObjectRefStructure.getRef());

        String ref = versionOfObjectRefStructure.getRef();
        if(StringUtils.countMatches(ref, ":") != 2) {
            throw new IllegalArgumentException("Expected two number of colons in ref. Got: '" + ref + "'");

        }
        String memberClass = ref.substring(ref.indexOf(':') + 1, ref.lastIndexOf(':'));

        String canonicalName = EntityInVersionStructure.class.getPackage().getName() + "." + memberClass;
        try {
            @SuppressWarnings("unchecked")
            Class<T> clazz = (Class<T>) Class.forName(canonicalName);

            if (ANY_VERSION.equals(versionOfObjectRefStructure.getVersion()) || versionOfObjectRefStructure.getVersion() == null) {
                return entityInVersionStructureRepository.findFirstByNetexIdOrderByVersionDesc(ref, clazz);
            } else {
                long version = Long.valueOf(versionOfObjectRefStructure.getVersion());
                return entityInVersionStructureRepository.findFirstByNetexIdAndVersion(ref, version, clazz);
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Type " + canonicalName + " cannot be found", e);
        }
    }

    private void assertNotNull(VersionOfObjectRefStructure versionOfObjectRefStructure, String name, String fieldValue) {
        if (fieldValue == null) {
            throw new IllegalArgumentException(name + " value cannot be null: " + versionOfObjectRefStructure);
        }
    }
}
