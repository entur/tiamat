package org.rutebanken.tiamat.netexmapping.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.rutebanken.netex.model.DataManagedObjectStructure;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.tiamat.netexmapping.NetexIdMapper;

public class DataManagedObjectStructureIdMapper extends CustomMapper<DataManagedObjectStructure, org.rutebanken.tiamat.model.DataManagedObjectStructure> {

    private NetexIdMapper netexIdMapper = new NetexIdMapper();

    @Override
    public void mapAtoB(DataManagedObjectStructure netexEntity, org.rutebanken.tiamat.model.DataManagedObjectStructure tiamatEntity, MappingContext context) {
        netexIdMapper.toTiamatModel(netexEntity, tiamatEntity);
    }

    @Override
    public void mapBtoA(org.rutebanken.tiamat.model.DataManagedObjectStructure internalEntity, DataManagedObjectStructure netexEntity, MappingContext context) {
        netexIdMapper.toNetexModel(internalEntity, netexEntity);
    }
}

