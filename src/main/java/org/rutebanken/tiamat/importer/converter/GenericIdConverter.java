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

package org.rutebanken.tiamat.importer.converter;

import org.rutebanken.tiamat.model.DataManagedObjectStructure;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.netex.id.ValidPrefixList;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.repository.generic.GenericEntityInVersionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Matches stops from nsr ID or imported/original ID
 */
public class GenericIdConverter<X extends org.rutebanken.netex.model.DataManagedObjectStructure> {

    private static final Logger logger = LoggerFactory.getLogger(GenericIdConverter.class);


    private final ValidPrefixList validPrefixList;
    private final NetexIdHelper netexIdHelper;
    private final GenericEntityInVersionRepository genericEntityInVersionRepository;
    private final Class<? extends DataManagedObjectStructure> tiamatType;

    public GenericIdConverter(ValidPrefixList validPrefixList, NetexIdHelper netexIdHelper, GenericEntityInVersionRepository genericEntityInVersionRepository, Class<? extends DataManagedObjectStructure> tiamatType) {
        this.validPrefixList = validPrefixList;
        this.netexIdHelper = netexIdHelper;
        this.genericEntityInVersionRepository = genericEntityInVersionRepository;
        this.tiamatType = tiamatType;
    }

    public X convertIncomingId(X object) {
        // If ID alredy null - do nothing
        if(object.getId() == null) {
            return object;
        }

        // If ID already valid - do nothing
        if(validPrefixList.isValidPrefixForType(netexIdHelper.extractIdPrefix(object.getId()), tiamatType)) {
            logger.debug("Detected ID with valid prefix: {}. ", object.getId());
            return object;
        }

        // Try to find an existing object that has used the same ID previously
        try {
            var existingObjectId = genericEntityInVersionRepository.findByKeyValue(NetexIdMapper.ORIGINAL_ID_KEY, Set.of(object.getId()), tiamatType);
            if (existingObjectId != null) {
                // We found it!
                object.setId(existingObjectId);
                return object;
            }
        } catch (Exception e) {
            logger.error("Error while trying to find existing object with ID {}", object.getId(), e);
        }
        // We didn't find a match - this will be imported as a new vehicle type
        return object;
    }
}
