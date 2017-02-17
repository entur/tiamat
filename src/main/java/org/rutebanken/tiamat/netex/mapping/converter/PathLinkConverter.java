package org.rutebanken.tiamat.netex.mapping.converter;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.common.collect.Sets;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.*;
import org.rutebanken.netex.model.EntranceRefStructure;
import org.rutebanken.netex.model.PathLink;
import org.rutebanken.netex.model.PlaceRef;
import org.rutebanken.netex.model.PlaceRefStructure;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.model.AddressablePlace;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Optional;

@Component
public class PathLinkConverter extends BidirectionalConverter<PathLink, org.rutebanken.tiamat.model.PathLink> {

    private static final Logger logger = LoggerFactory.getLogger(PathLinkConverter.class);


    private StopPlaceRepository stopPlaceRepository;

    private QuayRepository quayRepository;

    @Autowired
    public PathLinkConverter(StopPlaceRepository stopPlaceRepository, QuayRepository quayRepository) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.quayRepository = quayRepository;
    }

    public PathLinkConverter() {

    }
    
    @Override
    public org.rutebanken.tiamat.model.PathLink convertTo(PathLink netexPathLink, Type<org.rutebanken.tiamat.model.PathLink> type) {


        logger.debug("Converting path link from netex to tiamat model: {}", netexPathLink);
        org.rutebanken.tiamat.model.PathLink tiamatPathLink = new org.rutebanken.tiamat.model.PathLink();

        tiamatPathLink.setFrom(resolvePathLinkEndFromNetexPlaceRef(netexPathLink.getFrom().getPlaceRef()));
        tiamatPathLink.setTo(resolvePathLinkEndFromNetexPlaceRef(netexPathLink.getTo().getPlaceRef()));

        return tiamatPathLink;
    }

    private PathLinkEnd resolvePathLinkEndFromNetexPlaceRef(PlaceRefStructure placeRefStructure) {

        if(Strings.isNullOrEmpty(placeRefStructure.getNameOfMemberClass())) {
            logger.warn("Received place ref without name of member class: {}", placeRefStructure);
            return null;
        }
        PathLinkEnd pathLinkEnd = new PathLinkEnd();

        Optional<Long> tiamatId;
        if(isInternalTiamatId(placeRefStructure.getRef())) {
            tiamatId = Optional.of(NetexIdMapper.getTiamatId(placeRefStructure.getRef()));
            logger.debug("Tiamat ID in ref {}", placeRefStructure.getRef());
        } else {
            tiamatId = Optional.empty();
        }


        if(placeRefStructure.getNameOfMemberClass().equals(org.rutebanken.netex.model.StopPlace.class.getSimpleName())) {
            StopPlace tiamatStopPlace;

            if(tiamatId.isPresent()) {
                tiamatStopPlace = stopPlaceRepository.findOne(tiamatId.get());
            } else {
                long idFromOriginalId = stopPlaceRepository.findByKeyValue(NetexIdMapper.ORIGINAL_ID_KEY, Sets.newHashSet(placeRefStructure.getRef()));
                tiamatStopPlace = stopPlaceRepository.findOne(idFromOriginalId);
            }

            logger.info("Found stop place {}", tiamatStopPlace);
            pathLinkEnd.setStopPlace(tiamatStopPlace);

        } else if(placeRefStructure.getNameOfMemberClass().equals(org.rutebanken.netex.model.Quay.class.getSimpleName())) {
            logger.info("Reference to Quay: {}", placeRefStructure);
            Quay tiamatQuay;

            if(tiamatId.isPresent()) {
                tiamatQuay = quayRepository.findOne(tiamatId.get());
            } else {
                long idFromOriginalId = quayRepository.findByKeyValue(NetexIdMapper.ORIGINAL_ID_KEY, Sets.newHashSet(placeRefStructure.getRef()));
                tiamatQuay = quayRepository.findOne(idFromOriginalId);
            }

            logger.info("Found quay {}", tiamatQuay);
            pathLinkEnd.setQuay(tiamatQuay);
        }
        return pathLinkEnd;
    }

    private boolean isInternalTiamatId(String ref) {
        return ref.contains(NetexIdMapper.NSR);
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
