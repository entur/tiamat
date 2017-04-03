package org.rutebanken.tiamat.repository;

import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.rutebanken.tiamat.model.DataManagedObjectStructure;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.VersionOfObjectRefStructure;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

import static org.rutebanken.tiamat.model.VersionOfObjectRefStructure.ANY_VERSION;

@Component
public class ReferenceResolver {

    private static final Logger logger = LoggerFactory.getLogger(ReferenceResolver.class);

    @Autowired
    private GenericDataManagedObjectRepository genericDataManagedObjectRepository;

    public <T extends DataManagedObjectStructure> T resolve(VersionOfObjectRefStructure versionOfObjectRefStructure) {

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

            final String netexId;
            if(!NetexIdHelper.isNsrId(ref)) {
                logger.debug("Detected ID without expected prefix: {}. Will try to find it from original ID: {}.", NetexIdHelper.NSR, ref);
                Set<String> valuesArgument = Sets.newHashSet(ref);
                netexId = genericDataManagedObjectRepository.findByKeyValue(NetexIdMapper.ORIGINAL_ID_KEY, valuesArgument, clazz);
            } else {
                netexId = ref;
            }

            if (ANY_VERSION.equals(versionOfObjectRefStructure.getVersion()) || versionOfObjectRefStructure.getVersion() == null) {
                return genericDataManagedObjectRepository.findFirstByNetexIdOrderByVersionDesc(netexId, clazz);
            } else {
                long version = Long.valueOf(versionOfObjectRefStructure.getVersion());
                return genericDataManagedObjectRepository.findFirstByNetexIdAndVersion(netexId, version, clazz);
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
