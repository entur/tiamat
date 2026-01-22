/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.netex.mapping.converter;

import jakarta.xml.bind.JAXBElement;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.StopPlaceRefStructure;
import org.rutebanken.netex.model.StopPlaceRefs_RelStructure;
import org.rutebanken.tiamat.model.StopPlaceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * Converts between Collection of StopPlaceReference (tiamat) and StopPlaceRefs_RelStructure (NeTEx)
 */
@Component
public class StopPlaceRefsRelStructureConverter extends BidirectionalConverter<Set<StopPlaceReference>, StopPlaceRefs_RelStructure> {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceRefsRelStructureConverter.class);
    private final ObjectFactory objectFactory = new ObjectFactory();

    @Override
    public StopPlaceRefs_RelStructure convertTo(Set<StopPlaceReference> stopPlaceReferences, Type<StopPlaceRefs_RelStructure> type, MappingContext mappingContext) {

        if (!CollectionUtils.isEmpty(stopPlaceReferences)) {

            StopPlaceRefs_RelStructure stopPlaceRefs_relStructure = new StopPlaceRefs_RelStructure();
            List<JAXBElement<? extends StopPlaceRefStructure>> jaxbRefs = mapperFacade.mapAsList(stopPlaceReferences, StopPlaceRefStructure.class)
                    .stream()
                    .<JAXBElement<? extends StopPlaceRefStructure>>map(objectFactory::createStopPlaceRef)
                    .toList();
            stopPlaceRefs_relStructure.withStopPlaceRef(jaxbRefs);
            return stopPlaceRefs_relStructure;
        }
        return null;
    }

    @Override
    public Set<StopPlaceReference> convertFrom(StopPlaceRefs_RelStructure stopPlaceRefs_relStructure, Type<Set<StopPlaceReference>> type, MappingContext mappingContext) {
        logger.info("Converting from StopPlaceRefs_RelStructure to tiamat internal model is not implemented");
        return null;
    }
}
