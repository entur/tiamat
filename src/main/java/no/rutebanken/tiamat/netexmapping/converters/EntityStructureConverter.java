package no.rutebanken.tiamat.netexmapping.converters;


import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import no.rutebanken.tiamat.model.EntityStructure;

public class EntityStructureConverter extends BidirectionalConverter<EntityStructure, no.rutebanken.netex.model.EntityStructure>

        @Override
        public no.rutebanken.netex.model.EntityStructure convertTo(EntityStructure entityStructure, Type<no.rutebanken.netex.model.EntityStructure> type) {
            return null;
        }

        @Override
        public EntityStructure convertFrom(no.rutebanken.netex.model.EntityStructure entityStructure, Type<EntityStructure> type) {
            return null;
        }
}
