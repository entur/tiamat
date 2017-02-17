package org.rutebanken.tiamat.netex.mapping.converter;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.common.collect.Sets;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.EntranceRefStructure;
import org.rutebanken.netex.model.PathLink;
import org.rutebanken.netex.model.PlaceRef;
import org.rutebanken.netex.model.PlaceRefStructure;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.netex.model.PathLinkEndStructure;
import org.rutebanken.tiamat.netex.mapping.NetexMappingException;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

@Component
@Transactional(propagation = Propagation.MANDATORY)
public class PathLinkEndConverter extends BidirectionalConverter<PathLinkEndStructure, org.rutebanken.tiamat.model.PathLinkEnd> {

    private static final Logger logger = LoggerFactory.getLogger(PathLinkEndConverter.class);

    private static final String STOP_PLACE_NAME = "StopPlace";
    private static final String QUAY_PLACE_NAME = "Quay";

    private StopPlaceRepository stopPlaceRepository;

    private QuayRepository quayRepository;

    @Autowired
    public PathLinkEndConverter(StopPlaceRepository stopPlaceRepository, QuayRepository quayRepository) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.quayRepository = quayRepository;
    }
    public PathLinkEndConverter() {}

    @Override
    public org.rutebanken.tiamat.model.PathLinkEnd convertTo(PathLinkEndStructure netexPathLinkEnd, Type<org.rutebanken.tiamat.model.PathLinkEnd> type) {

        logger.debug("Converting path link end from netex to tiamat model: {}", netexPathLinkEnd);

        PlaceRefStructure placeRefStructure = netexPathLinkEnd.getPlaceRef();

        if(Strings.isNullOrEmpty(placeRefStructure.getNameOfMemberClass())) {
            throw new NetexMappingException("Received place ref without name of member class: " + placeRefStructure +". Cannot determine type.");

        }
        PathLinkEnd pathLinkEnd = new PathLinkEnd();


        Optional<Long> tiamatId = NetexIdMapper.getOptionalTiamatId(placeRefStructure.getRef());

        if(placeRefStructure.getNameOfMemberClass().equals(STOP_PLACE_NAME)) {
            StopPlace tiamatStopPlace;

            if(tiamatId.isPresent()) {
                tiamatStopPlace = stopPlaceRepository.findOne(tiamatId.get());
            } else {
                long idFromOriginalId = stopPlaceRepository.findByKeyValue(NetexIdMapper.ORIGINAL_ID_KEY, Sets.newHashSet(placeRefStructure.getRef()));
                tiamatStopPlace = stopPlaceRepository.findOne(idFromOriginalId);
            }

            logger.info("Found stop place {}", tiamatStopPlace);
            pathLinkEnd.setStopPlace(tiamatStopPlace);

        } else if(placeRefStructure.getNameOfMemberClass().equals(QUAY_PLACE_NAME)) {
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
        } else {
            throw new NetexMappingException("Cannot map placeRefStructure with value: "+placeRefStructure.getNameOfMemberClass());
        }
        return pathLinkEnd;
    }

    @Override
    public PathLinkEndStructure convertFrom(PathLinkEnd tiamatPathLinkEnd, Type<PathLinkEndStructure> type) {

        PathLinkEndStructure netexPathLinkEnd = new PathLinkEndStructure();
        Optional<AddressablePlace> optionalPlace = getPlace(tiamatPathLinkEnd);
        Optional<SiteEntrance> optionalSiteEntrance = Optional.ofNullable(tiamatPathLinkEnd.getEntrance());
        if(optionalPlace.isPresent()) {
            PlaceRefStructure placeRef = new PlaceRefStructure();
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
