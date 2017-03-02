package org.rutebanken.tiamat.netex.mapping.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.TopographicPlace;
import org.rutebanken.netex.model.TopographicPlaceDescriptor_VersionedChildStructure;
import org.rutebanken.netex.model.TopographicPlaceRefStructure;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TopographicPlaceMapper extends CustomMapper<TopographicPlace, org.rutebanken.tiamat.model.TopographicPlace> {

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;

    @Override
    public void mapAtoB(TopographicPlace netexTopographicPlace, org.rutebanken.tiamat.model.TopographicPlace tiamatTopographicPlace, MappingContext context) {
        super.mapAtoB(netexTopographicPlace, tiamatTopographicPlace, context);

        TopographicPlaceRefStructure parentTopographicPlaceRef = netexTopographicPlace.getParentTopographicPlaceRef();
        if (parentTopographicPlaceRef != null && parentTopographicPlaceRef.getRef() != null) {
            org.rutebanken.tiamat.model.TopographicPlace parentTopographicPlace = topographicPlaceRepository.findByNetexId(parentTopographicPlaceRef.getRef());

            tiamatTopographicPlace.setParentTopographicPlace(parentTopographicPlace);
        }
    }

    @Override
    public void mapBtoA(org.rutebanken.tiamat.model.TopographicPlace tiamatTopographicPlace, TopographicPlace netexTopographicPlace, MappingContext context) {
        super.mapBtoA(tiamatTopographicPlace, netexTopographicPlace, context);
        netexTopographicPlace.withDescriptor(
                new TopographicPlaceDescriptor_VersionedChildStructure());

        if(tiamatTopographicPlace.getName() != null) {
            netexTopographicPlace.getDescriptor().withName(new MultilingualString().withValue(tiamatTopographicPlace.getName().getValue()));
        }

        if (tiamatTopographicPlace.getParentTopographicPlace() != null) {
            TopographicPlaceRefStructure parentTopographicPlaceRef =  new TopographicPlaceRefStructure();
            parentTopographicPlaceRef.withRef(
                    tiamatTopographicPlace.getParentTopographicPlace().getNetexId());

            netexTopographicPlace.withParentTopographicPlaceRef(parentTopographicPlaceRef);
        }

        // TODO: versioning
        netexTopographicPlace.setVersion("any");

    }
}
