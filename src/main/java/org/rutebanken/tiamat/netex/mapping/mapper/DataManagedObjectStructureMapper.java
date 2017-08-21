package org.rutebanken.tiamat.netex.mapping.mapper;

import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Longs;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.rutebanken.netex.model.DataManagedObjectStructure;
import org.rutebanken.netex.model.KeyListStructure;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.tiamat.netex.mapping.NetexMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.rutebanken.tiamat.model.VersionOfObjectRefStructure.ANY_VERSION;

@Component
public class DataManagedObjectStructureMapper extends CustomMapper<DataManagedObjectStructure, org.rutebanken.tiamat.model.DataManagedObjectStructure> {

    private static final Logger logger = LoggerFactory.getLogger(DataManagedObjectStructureMapper.class);

    public static final String CHANGED_BY = "CHANGED_BY";
    public static final String VERSION_COMMENT = "VERSION_COMMENT";

    /**
     * Setters for internal tiamat model when mapping from netex.
     */
    private static final Map<String, BiConsumer<String, org.rutebanken.tiamat.model.DataManagedObjectStructure>> tiamatEntitySetFunctions = new ImmutableMap.Builder<String, BiConsumer<String, org.rutebanken.tiamat.model.DataManagedObjectStructure>>()
            .put(CHANGED_BY, (value, tiamatEntity) -> tiamatEntity.setChangedBy(value))
            .put(VERSION_COMMENT, (value, tiamatEntity) -> tiamatEntity.setVersionComment(value))
            .build();

    /**
     * Properties to map to key values in netex format. Getters for the tiamat entity.
     */
    private static final Map<String, Function<org.rutebanken.tiamat.model.DataManagedObjectStructure, String>> tiamatEntityGetFunctions = new ImmutableMap.Builder<String, Function<org.rutebanken.tiamat.model.DataManagedObjectStructure, String>>()
            .put(CHANGED_BY, org.rutebanken.tiamat.model.DataManagedObjectStructure::getChangedBy)
            .put(VERSION_COMMENT, org.rutebanken.tiamat.model.DataManagedObjectStructure::getVersionComment)
            .build();


    @Autowired
    private final NetexIdMapper netexIdMapper;

    public DataManagedObjectStructureMapper(NetexIdMapper netexIdMapper) {
        this.netexIdMapper = netexIdMapper;
    }

    @Override
    public void mapAtoB(DataManagedObjectStructure netexEntity, org.rutebanken.tiamat.model.DataManagedObjectStructure tiamatEntity, MappingContext context) {
        netexIdMapper.toTiamatModel(netexEntity, tiamatEntity);

        if(netexEntity.getVersion() != null) {
            if (netexEntity.getVersion().equals(ANY_VERSION)) {
                tiamatEntity.setVersion(-1L); // Need to handle this value in import.
            } else {
                Long longVersion = Longs.tryParse(netexEntity.getVersion());
                if (longVersion != null) {
                    tiamatEntity.setVersion(longVersion);
                } else {
                    throw new NetexMappingException("Received version in netex format. " +
                            "But cannot parse version. Expecting a long value or the String 'any'. " +
                            "Value is: " + netexEntity.getVersion() + " Object: " + netexEntity);
                }
            }
        }

        if(netexEntity.getKeyList() != null && netexEntity.getKeyList().getKeyValue() != null) {
            netexEntity.getKeyList().getKeyValue().forEach(keyValueStructure -> {
                if (tiamatEntitySetFunctions.containsKey(keyValueStructure.getKey())) {
                    tiamatEntitySetFunctions.get(keyValueStructure.getKey()).accept(keyValueStructure.getValue(), tiamatEntity);
                }
            });
        }
    }

    @Override
    public void mapBtoA(org.rutebanken.tiamat.model.DataManagedObjectStructure tiamatEntity, DataManagedObjectStructure netexEntity, MappingContext context) {
        netexIdMapper.toNetexModel(tiamatEntity, netexEntity);
        netexEntity.setVersion(String.valueOf(tiamatEntity.getVersion()));

        tiamatEntityGetFunctions.forEach((property, function) -> setKey(netexEntity, property, function.apply(tiamatEntity)));
    }

    private void setKey(DataManagedObjectStructure netexEntity, String key, String value) {
        if(value == null) return;

        if(netexEntity.getKeyList() == null) {
            netexEntity.withKeyList(new KeyListStructure());

        }
        netexEntity.getKeyList()
                .withKeyValue(new KeyValueStructure()
                        .withKey(key)
                        .withValue(value));

    }
}

