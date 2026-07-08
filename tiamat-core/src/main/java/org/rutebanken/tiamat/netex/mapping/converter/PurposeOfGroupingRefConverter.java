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

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.PurposeOfGroupingRefStructure;
import org.rutebanken.tiamat.model.PurposeOfGrouping;
import org.rutebanken.tiamat.netex.mapping.NetexMappingException;
import org.rutebanken.tiamat.repository.PurposeOfGroupingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Component
public class PurposeOfGroupingRefConverter extends BidirectionalConverter<PurposeOfGroupingRefStructure, PurposeOfGrouping> {

    private static final Logger logger = LoggerFactory.getLogger(PurposeOfGroupingRefConverter.class);



    private final PurposeOfGroupingRepository purposeOfGroupingRepository;

    @Autowired
    public PurposeOfGroupingRefConverter(PurposeOfGroupingRepository purposeOfGroupingRepository) {
        this.purposeOfGroupingRepository = purposeOfGroupingRepository;
    }

    @Override
    public PurposeOfGrouping convertTo(PurposeOfGroupingRefStructure purposeOfGroupingRefStructure, Type<PurposeOfGrouping> type, MappingContext mappingContext) {

        if(purposeOfGroupingRefStructure.getVersion() == null) {
            logger.debug("Version is null for purposeOfGrouping ref. Finding newest version. ref: {}", purposeOfGroupingRefStructure);
            PurposeOfGrouping purposeOfGrouping = purposeOfGroupingRepository.findFirstByNetexIdOrderByVersionDesc(purposeOfGroupingRefStructure.getRef());
            if(purposeOfGrouping != null) {
                return purposeOfGrouping;
            }
            throw new NetexMappingException("Cannot find purposeOfGrouping  from ref: " +purposeOfGroupingRefStructure.getRef());
        }

        throw new NetexMappingException("Cannot find purposeOfGrouping  from ref: " +purposeOfGroupingRefStructure.getRef());

    }

    @Override
    public PurposeOfGroupingRefStructure convertFrom(PurposeOfGrouping purposeOfGrouping, Type<PurposeOfGroupingRefStructure> type, MappingContext mappingContext) {
            PurposeOfGroupingRefStructure purposeOfGroupingRefStructure = new PurposeOfGroupingRefStructure()
                    .withRef(purposeOfGrouping.getNetexId());
            logger.debug("Mapped purposeOfGrouping  ref structure: {}", purposeOfGroupingRefStructure);
            return purposeOfGroupingRefStructure;

    }
}
