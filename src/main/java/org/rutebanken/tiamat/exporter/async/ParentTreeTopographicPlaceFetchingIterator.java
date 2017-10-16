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

package org.rutebanken.tiamat.exporter.async;

import org.rutebanken.tiamat.model.PlaceRefStructure;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceRefStructure;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ParentTreeTopographicPlaceFetchingIterator implements Iterator<TopographicPlace> {

    private static final Logger logger = LoggerFactory.getLogger(ParentTreeTopographicPlaceFetchingIterator.class);

    private final Iterator<TopographicPlace> iterator;
    private final TopographicPlaceRepository topographicPlaceRepository;

    private Set<String> fetchedTopographicRefs = new HashSet<>();

    private LinkedList<TopographicPlace> parents = new LinkedList<>();

    private TopographicPlace next;

    public ParentTreeTopographicPlaceFetchingIterator(Iterator<TopographicPlace> iterator, TopographicPlaceRepository topographicPlaceRepository) {
        this.iterator = iterator;
        this.topographicPlaceRepository = topographicPlaceRepository;
    }

    @Override
    public boolean hasNext() {
        return !parents.isEmpty() || next != null || iterator.hasNext();
    }

    @Override
    public TopographicPlace next() {

        if(!parents.isEmpty()) {
            logger.debug("Return parent");
            return parents.pollLast();
        }

        if(next != null) {
            TopographicPlace returnVal = next;
            next = null;
            return returnVal;
        }

        TopographicPlace topographicPlace = iterator.next();

        fetchTopographicParents(topographicPlace);

        if(parents.isEmpty()) {
            logger.debug("No parents. Returning from iterator");
            return topographicPlace;
        } else {
            logger.debug("There are newly fetched parents. Return parent");
            next = topographicPlace;
            return parents.pollLast();
        }

    }

    private void fetchTopographicParents(TopographicPlace topographicPlace) {
        if(topographicPlace.getParentTopographicPlaceRef() != null) {
            TopographicPlaceRefStructure refStructure = topographicPlace.getParentTopographicPlaceRef();

            String parentRefString = refString(refStructure);

            if(!fetchedTopographicRefs.contains(parentRefString)) {
                TopographicPlace parentTopographicPlace;
                if(refStructure.getVersion() != null) {
                    parentTopographicPlace = topographicPlaceRepository.findFirstByNetexIdAndVersion(refStructure.getRef(), Long.parseLong(refStructure.getVersion()));
                } else {
                    logger.warn("No version for parent topographic place: {}", refStructure);
                    parentTopographicPlace = topographicPlaceRepository.findFirstByNetexIdOrderByVersionDesc(refStructure.getRef());
                }

                if(parentTopographicPlace == null) {
                    logger.warn("Cannot find parent topographic place from ref: {}", refStructure);
                } else {

                    logger.info("Fetched parent topographic place {}", parentRefString);
                    fetchedTopographicRefs.add(parentRefString);
                    parents.add(parentTopographicPlace);

                    // Parent topographic place can have parents itself
                    fetchTopographicParents(parentTopographicPlace);
                }
            }
        }
    }


    private String refString(PlaceRefStructure placeRef) {
        return placeRef.getRef()+"-"+placeRef.getVersion();
    }
}
