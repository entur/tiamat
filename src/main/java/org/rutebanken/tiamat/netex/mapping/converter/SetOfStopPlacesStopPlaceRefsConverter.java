package org.rutebanken.tiamat.netex.mapping.converter;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.StopPlaceRefStructure;
import org.rutebanken.netex.model.StopPlaceRefs_RelStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.VersionOfObjectRefStructure;
import org.rutebanken.tiamat.repository.reference.ReferenceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Component
public class SetOfStopPlacesStopPlaceRefsConverter extends BidirectionalConverter<Set<StopPlace>, StopPlaceRefs_RelStructure> {

    private static final Logger logger = LoggerFactory.getLogger(SetOfStopPlacesStopPlaceRefsConverter.class);

    @Autowired
    private ReferenceResolver referenceResolver;

    @Override
    public StopPlaceRefs_RelStructure convertTo(Set<StopPlace> stopPlaces, Type<StopPlaceRefs_RelStructure> type, MappingContext mappingContext) {

        if(stopPlaces != null && !stopPlaces.isEmpty()) {
            logger.debug("Mapping set of stop places to netex. stops: {}", stopPlaces.size());
//            return new StopPlaceRefs_RelStructure() TODO
//                    .withStopPlaceRef(
//                        stopPlaces.stream().map(stopPlace -> {
//                            StopPlaceRefStructure stopPlaceRefStructure = new StopPlaceRefStructure();
//                            stopPlaceRefStructure.withVersion(String.valueOf(stopPlace.getVersion()));
//                            stopPlaceRefStructure.withRef(stopPlace.getNetexId());
//                            return stopPlaceRefStructure;
//                        })
//                        .collect(toList()));
        }
        return null;
    }

    @Override
    public Set<StopPlace> convertFrom(StopPlaceRefs_RelStructure stopPlaceRefs_relStructure, Type<Set<StopPlace>> type, MappingContext mappingContext) {
//        if(stopPlaceRefs_relStructure != null && stopPlaceRefs_relStructure.getStopPlaceRef() != null && !stopPlaceRefs_relStructure.getStopPlaceRef().isEmpty()) { //TODO
//            logger.debug("Mapping set stopPlaceRefs_relStructure from netex. stops {}", stopPlaceRefs_relStructure.getStopPlaceRef().size());
//            return stopPlaceRefs_relStructure.getStopPlaceRef()
//                    .stream()
//                    .map(stopPlaceRefStructure -> (StopPlace) referenceResolver.resolve(new VersionOfObjectRefStructure(stopPlaceRefStructure.getRef(), stopPlaceRefStructure.getVersion())))
//                    .collect(toSet());
//        }

        return null;
    }
}
