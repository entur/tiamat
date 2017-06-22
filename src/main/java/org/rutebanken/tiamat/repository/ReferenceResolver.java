package org.rutebanken.tiamat.repository;

import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.rutebanken.tiamat.model.DataManagedObjectStructure;
import org.rutebanken.tiamat.model.VersionOfObjectRefStructure;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.netex.id.TypeFromIdResolver;
import org.rutebanken.tiamat.netex.id.ValidPrefixList;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

import static org.rutebanken.tiamat.model.VersionOfObjectRefStructure.ANY_VERSION;

/**
 * Note that: Not all references are references to DataManagedObjectStructure.
 * For instance, AccomodationRefStructure.
 * This means that key value lookup cannot happen for this entity.
 */
@Component
public class ReferenceResolver {

    private static final Logger logger = LoggerFactory.getLogger(ReferenceResolver.class);

    @Autowired
    private GenericEntityInVersionRepository genericEntityInVersionRepository;

    @Autowired
    private ValidPrefixList validPrefixList;

    @Autowired
    private TypeFromIdResolver typeFromIdResolver;

    public <T extends DataManagedObjectStructure> T resolve(VersionOfObjectRefStructure versionOfObjectRefStructure) {

        logger.debug("Received reference: {}", versionOfObjectRefStructure);

        assertNotNull(versionOfObjectRefStructure, "ref", versionOfObjectRefStructure.getRef());

        String ref = versionOfObjectRefStructure.getRef();
        if (StringUtils.countMatches(ref, ":") != 2) {
            throw new IllegalArgumentException("Expected two number of colons in ref. Got: '" + ref + "'");

        }
        String memberClass = NetexIdHelper.extractIdType(ref);

        Class<T> clazz = typeFromIdResolver.resolveClassFromId(ref);

        String prefix = NetexIdHelper.extractIdPrefix(ref);

        final String netexId;
        if (!validPrefixList.isValidPrefixForType(prefix, memberClass)) {
            logger.debug("Detected ID without valid prefix: {} and type {}. Will try to find it from original ID: {}.", prefix, memberClass, ref);
            Set<String> valuesArgument = Sets.newHashSet(ref);
            netexId = genericEntityInVersionRepository.findByKeyValue(NetexIdMapper.ORIGINAL_ID_KEY, valuesArgument, clazz);
        } else {
            netexId = ref;
        }

        if (ANY_VERSION.equals(versionOfObjectRefStructure.getVersion()) || versionOfObjectRefStructure.getVersion() == null) {
            return genericEntityInVersionRepository.findFirstByNetexIdOrderByVersionDesc(netexId, clazz);
        } else {
            long version = Long.valueOf(versionOfObjectRefStructure.getVersion());
            return genericEntityInVersionRepository.findFirstByNetexIdAndVersion(netexId, version, clazz);
        }

    }

    private void assertNotNull(VersionOfObjectRefStructure versionOfObjectRefStructure, String name, String fieldValue) {
        if (fieldValue == null) {
            throw new IllegalArgumentException(name + " value cannot be null: " + versionOfObjectRefStructure);
        }
    }
}
