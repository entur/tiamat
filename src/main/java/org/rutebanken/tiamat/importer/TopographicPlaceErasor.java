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

package org.rutebanken.tiamat.importer;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class TopographicPlaceErasor {

    private final TopographicPlaceRepository topographicPlaceRepository;

    private static final Logger logger = LoggerFactory.getLogger(TopographicPlaceErasor.class);

    @Autowired
    public TopographicPlaceErasor(TopographicPlaceRepository topographicPlaceRepository) {
        this.topographicPlaceRepository = topographicPlaceRepository;
    }

    public void erase(String idPrefix, TopographicPlaceTypeEnumeration topographicPlaceType) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(idPrefix), "idPrefix must not be null or empty");
        Preconditions.checkArgument(topographicPlaceType != null, "topographicPlaceType must not be null");

        int count = topographicPlaceRepository.countByTopographicPlaceTypeAndNetexIdStartingWith(topographicPlaceType, idPrefix);

        logger.warn("About to delete ALL topographic places ({}) with idPrefix {} and type {}.", count, idPrefix, topographicPlaceType);
        topographicPlaceRepository.deleteAllByTopographicPlaceTypeAndNetexIdStartingWith(topographicPlaceType,idPrefix);
        logger.warn("Deleted {} topographic places with idPrefix {} and type {}", count, idPrefix, topographicPlaceType);
        topographicPlaceRepository.flush();
    }


}
