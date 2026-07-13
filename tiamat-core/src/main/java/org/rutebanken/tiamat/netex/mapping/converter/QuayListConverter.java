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
import org.rutebanken.netex.model.Quays_RelStructure;
import org.rutebanken.tiamat.model.Quay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class QuayListConverter extends BidirectionalConverter<Set<Quay>, Quays_RelStructure> {

    private static final Logger logger = LoggerFactory.getLogger(QuayListConverter.class);

    @Override
    public Quays_RelStructure convertTo(Set<Quay> quays, Type<Quays_RelStructure> type, MappingContext mappingContext) {
        if(quays == null || quays.isEmpty()) {
            return null;
        }

        Quays_RelStructure quays_relStructure = new Quays_RelStructure();

        logger.debug("Mapping {} quays to netex", quays.size());

        quays.forEach(quay -> {
            org.rutebanken.netex.model.Quay netexQuay = mapperFacade.map(quay, org.rutebanken.netex.model.Quay.class);
            quays_relStructure.withQuayRefOrQuay(List.of(new ObjectFactory().createQuay(netexQuay)));
        });
        return quays_relStructure;
    }

    @Override
    public Set<Quay> convertFrom(Quays_RelStructure quays_relStructure, Type<Set<Quay>> type, MappingContext mappingContext) {
        logger.debug("Mapping {} quays to internal model", quays_relStructure != null ? quays_relStructure.getQuayRefOrQuay().size() : 0);
        Set<Quay> quays = new HashSet<>();
        if(quays_relStructure != null && quays_relStructure.getQuayRefOrQuay() != null) {
            quays_relStructure.getQuayRefOrQuay().stream()
                    .map(JAXBElement::getValue)
                    .filter(object -> object instanceof org.rutebanken.netex.model.Quay)
                    .map(object -> ((org.rutebanken.netex.model.Quay) object))
                    .map(netexQuay -> mapperFacade.map(netexQuay, Quay.class))
                    .forEach(quays::add);
        }
        
        return quays;
    }
}
