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

package org.rutebanken.tiamat.service;


import org.rutebanken.tiamat.model.BoardingPosition;
import org.rutebanken.tiamat.model.Quay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;


@Component
public class BoardingPositionUpdater {

    private static final Logger logger = LoggerFactory.getLogger(BoardingPositionUpdater.class);

    /**
     * Saving boarding position always overwrites existing boarding position,
     * except when the incoming boarding position matches an existing one.
     *
     * @param quay       quay with boarding positions
     * @param boardingPositions incoming boarding positions
     * @return if boarding positions were updated
     */
    public boolean update(Quay quay, List<BoardingPosition> boardingPositions) {
        final AtomicInteger matchedExisting = new AtomicInteger();

        List<BoardingPosition> result = boardingPositions.stream()
                .map(incomingBoardingPosition -> {
                    Optional<BoardingPosition> optionalExisting = matchExisting(quay, incomingBoardingPosition);

                    if (optionalExisting.isPresent()) {
                        BoardingPosition existingBoardingPosition = optionalExisting.get();
                        logger.debug("Found matching boarding position on id. Keeping existing: {} incoming: {}",
                                existingBoardingPosition, incomingBoardingPosition);
                        return existingBoardingPosition;
                    } else {
                        matchedExisting.incrementAndGet();
                        return incomingBoardingPosition;
                    }
                })
                .toList();


        quay.getBoardingPositions().clear();
        quay.getBoardingPositions().addAll(result);

        return matchedExisting.get() > 0;
    }



    private Optional<BoardingPosition> matchExisting(Quay entity, BoardingPosition incomingBoardingPosition) {
        return entity.getBoardingPositions()
                .stream()
                .filter(existingBoardingPosition -> existingBoardingPosition.getNetexId().equals(incomingBoardingPosition.getNetexId()))
                .findFirst();
    }

}
