package org.rutebanken.tiamat.netex.mapping.mapper;

import com.google.common.primitives.Longs;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.rutebanken.netex.model.DataManagedObjectStructure;
import org.rutebanken.tiamat.netex.mapping.NetexMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.rutebanken.tiamat.model.VersionOfObjectRefStructure.ANY_VERSION;

@Component
public class DataManagedObjectStructureMapper extends CustomMapper<DataManagedObjectStructure, org.rutebanken.tiamat.model.DataManagedObjectStructure> {

    private static final Logger logger = LoggerFactory.getLogger(DataManagedObjectStructureMapper.class);

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
    }

    @Override
    public void mapBtoA(org.rutebanken.tiamat.model.DataManagedObjectStructure tiamatEntity, DataManagedObjectStructure netexEntity, MappingContext context) {
        netexIdMapper.toNetexModel(tiamatEntity, netexEntity);
        netexEntity.setVersion(String.valueOf(tiamatEntity.getVersion()));
    }
}

