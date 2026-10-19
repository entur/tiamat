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


import org.locationtech.jts.geom.Point;
import org.rutebanken.tiamat.model.BoardingPosition;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.rest.graphql.mappers.BoardingPositionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class BoardingPositionUpdater {

    private record Snapshot(String netexId, String publicCode, Point centroid) {
        static Snapshot of(BoardingPosition position) {
            return new Snapshot(position.getNetexId(), position.getPublicCode(), position.getCentroid());
        }
    }

    @Autowired
    private ChildEntityReconciler childEntityReconciler;

    @Autowired
    private BoardingPositionMapper boardingPositionMapper;

    /**
     * Replaces the boarding positions of the quay with the incoming ones.
     * An incoming position with an id updates the stored position that has this id.
     * An incoming position without an id creates a new position.
     *
     * @param quay the quay that holds the boarding positions
     * @param inputs the GraphQL maps of the incoming boarding positions
     * @return true if the positions differ from the stored ones in count, id, public code or centroid
     * @throws IllegalArgumentException if an id matches no boarding position of the quay, or occurs twice
     */
    public boolean update(Quay quay, List<Map<String, Object>> inputs) {
        List<BoardingPosition> stored = List.copyOf(quay.getBoardingPositions());
        List<Snapshot> before = stored.stream().map(Snapshot::of).toList();

        List<BoardingPosition> reconciled = childEntityReconciler.reconcileAll(
                stored, inputs, quay.getNetexId(), BoardingPosition::new,
                boardingPositionMapper::populateBoardingPosition);

        quay.getBoardingPositions().clear();
        quay.getBoardingPositions().addAll(reconciled);

        List<Snapshot> after = reconciled.stream().map(Snapshot::of).toList();
        return !Objects.equals(before, after);
    }
}
