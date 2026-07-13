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

package org.rutebanken.tiamat.importer.modifier;

import org.rutebanken.tiamat.model.Site_VersionStructure;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.repository.reference.ReferenceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Remove topographic place name from site element's name
 */
@Component
public class TopographicPlaceNameRemover {

    private static final Logger logger = LoggerFactory.getLogger(TopographicPlaceNameRemover.class);

    private final ReferenceResolver referenceResolver;

    @Autowired
    public TopographicPlaceNameRemover(ReferenceResolver referenceResolver) {
        this.referenceResolver = referenceResolver;
    }

    public <T extends Site_VersionStructure> T removeIfmatch(T siteVersionStructure) {

        if(siteVersionStructure.getName() == null) {
            logger.debug("No name for site: {}", siteVersionStructure);
            return siteVersionStructure;
        }

        if(siteVersionStructure.getTopographicPlace() == null) {
            logger.debug("No topographic place for {}", siteVersionStructure);
            return siteVersionStructure;
        }

        TopographicPlace topographicPlace = siteVersionStructure.getTopographicPlace();

        Set<String> topographicPlaceNames = new HashSet<>();
        findNamesRecursively(topographicPlace, topographicPlaceNames);

        logger.debug("Resolved topographic place names: {}", topographicPlaceNames);

        for(String topographicPlaceName : topographicPlaceNames) {
            logger.debug("Check for match and remove {} from stop name {}", topographicPlaceName, siteVersionStructure.getName().getValue());
            String newName = siteVersionStructure.getName().getValue().replaceAll("\\s+"+topographicPlaceName + "$", "").trim();
            String oldName = siteVersionStructure.getName().getValue();
            siteVersionStructure.getName().setValue(newName);
            logger.warn("Changed name from '{}' to '{}' for entity {}", oldName, newName, siteVersionStructure);
        }

        return siteVersionStructure;
    }


    private void findNamesRecursively(TopographicPlace topographicPlace, Set<String> topographicPlaceNames) {

        if(topographicPlace == null) {
            return;
        }

        if(topographicPlace.getName() != null) {
            topographicPlaceNames.add(topographicPlace.getName().getValue());
        }

        if(topographicPlace.getParentTopographicPlaceRef() != null) {
            logger.debug("Resolving parent topographic place: {}", topographicPlaceNames);
            TopographicPlace parentTopographicPlace = referenceResolver.resolve(topographicPlace.getParentTopographicPlaceRef());
            findNamesRecursively(parentTopographicPlace, topographicPlaceNames);
        }
    }


}
