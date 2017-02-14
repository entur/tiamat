package org.rutebanken.tiamat.netex.mapping.converter;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.*;
import org.rutebanken.netex.model.EntranceRefStructure;
import org.rutebanken.netex.model.PathLink;
import org.rutebanken.netex.model.PlaceRef;
import org.rutebanken.netex.model.PlaceRefStructure;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.model.AddressablePlace;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public class PathLinkConverter extends BidirectionalConverter<PathLink, org.rutebanken.tiamat.model.PathLink> {

    private static final Logger logger = LoggerFactory.getLogger(PathLinkConverter.class);

    @Override
    public org.rutebanken.tiamat.model.PathLink convertTo(PathLink pathLink, Type<org.rutebanken.tiamat.model.PathLink> type) {
        return null;
    }

    @Override
    public PathLink convertFrom(org.rutebanken.tiamat.model.PathLink tiamatPathLink, Type<PathLink> type) {
        if(tiamatPathLink == null) {
            return null;
        }

        PathLink netexPathLink = new PathLink();
        if(tiamatPathLink.getCreated() != null ) {
            netexPathLink.setCreated(tiamatPathLink.getCreated().toOffsetDateTime());
        }
        netexPathLink.setId(NetexIdMapper.getNetexId(tiamatPathLink, tiamatPathLink.getId()));


        netexPathLink.setFrom(mapPathLinkEndToNetex(tiamatPathLink.getFrom()));
        netexPathLink.setTo(mapPathLinkEndToNetex(tiamatPathLink.getTo()));

        return netexPathLink;
    }

    private PathLinkEndStructure mapPathLinkEndToNetex(PathLinkEnd tiamatPathLinkEnd) {
        PathLinkEndStructure netexPathLinkEnd = new PathLinkEndStructure();
        Optional<AddressablePlace> optionalPlace = getPlace(tiamatPathLinkEnd);
        Optional<SiteEntrance> optionalSiteEntrance = Optional.ofNullable(tiamatPathLinkEnd.getEntrance());
        if(optionalPlace.isPresent()) {
            PlaceRef placeRef = new PlaceRef();
            setRefValues(placeRef, optionalPlace.get());
            netexPathLinkEnd.setPlaceRef(placeRef);
        } else if (optionalSiteEntrance.isPresent()){
            EntranceRefStructure entranceRefStructure = new EntranceRefStructure();
            setRefValues(entranceRefStructure, optionalSiteEntrance.get());
            netexPathLinkEnd.setEntranceRef(entranceRefStructure);
        }
        return netexPathLinkEnd;
    }

    private Optional<AddressablePlace> getPlace(PathLinkEnd tiamatPathLinkEnd) {
        AddressablePlace addressablePlace;

        if(tiamatPathLinkEnd.getQuay() != null) {
            addressablePlace = tiamatPathLinkEnd.getQuay();
        } else if(tiamatPathLinkEnd.getStopPlace() != null) {
            addressablePlace = tiamatPathLinkEnd.getStopPlace();
        } else {
            return Optional.empty();
        }
        logger.debug("PathLinkEnd points to StopPlace {}", tiamatPathLinkEnd.getStopPlace());
        return Optional.of(addressablePlace);
    }

    private PlaceRefStructure setRefValues(PlaceRefStructure placeRef, Place place) {
        return placeRef.withRef(NetexIdMapper.getNetexId(place, place.getId()))
                .withVersion("1")
                .withCreated(OffsetDateTime.now());
    }

}
