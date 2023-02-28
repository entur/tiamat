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

package org.rutebanken.tiamat.netex.mapping.mapper.mapStruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.rutebanken.netex.model.Quays_RelStructure;
import org.rutebanken.tiamat.model.Quay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@Mapper(uses = {QuayMapper.class})
public abstract class QuayListConverter {

    private static final Logger logger = LoggerFactory.getLogger(QuayListConverter.class);

    @Mapping(target = "quayRefOrQuay")
    public abstract Quays_RelStructure convertTo(Set<Quay> quays);

/*    {
        if(quays == null || quays.isEmpty()) {
            return null;
        }

        Quays_RelStructure quays_relStructure = new Quays_RelStructure();

        logger.debug("Mapping {} quays to netex", quays.size());

        quays.forEach(quay -> {
            org.rutebanken.netex.model.Quay netexQuay = mapperFacade.map(quay, org.rutebanken.netex.model.Quay.class);
            quays_relStructure.getQuayRefOrQuay().add(netexQuay);
        });
        return quays_relStructure;
    }
 */

    @Mapping(source = "quayRefOrQuay", target = "")
    public abstract Set<Quay> convertFrom(Quays_RelStructure quays_relStructure);

    /*{
        logger.debug("Mapping {} quays to internal model", quays_relStructure != null ? quays_relStructure.getQuayRefOrQuay().size() : 0);
        Set<Quay> quays = new HashSet<>();
        if(quays_relStructure != null && quays_relStructure.getQuayRefOrQuay() != null) {
            quays_relStructure.getQuayRefOrQuay().stream()
                    .filter(object -> object instanceof org.rutebanken.netex.model.Quay)
                    .map(object -> ((org.rutebanken.netex.model.Quay) object))
                    .map(netexQuay -> {
                        return mapperFacade.map(netexQuay, Quay.class);
                    })
                    .forEach(quays::add);
        }
        
        return quays;
    }*/
}
