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

import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.rutebanken.tiamat.model.BoardingPosition;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.rest.graphql.mappers.BoardingPositionMapper;
import org.rutebanken.tiamat.rest.graphql.mappers.GeometryMapper;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ID;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PUBLIC_CODE;

public class BoardingPositionUpdaterTest {

    private final BoardingPositionUpdater updater = new BoardingPositionUpdater();
    private final BoardingPositionMapper mapper = new BoardingPositionMapper();
    private final Point centroid = new GeometryFactory().createPoint(new Coordinate(10.5, 59.9));

    public BoardingPositionUpdaterTest() {
        ReflectionTestUtils.setField(mapper, "geometryMapper", new GeometryMapper());
        ReflectionTestUtils.setField(updater, "boardingPositionMapper", mapper);
        ReflectionTestUtils.setField(updater, "childEntityReconciler", new ChildEntityReconciler());
    }

    private Quay quayWithPosition(String publicCode) {
        Quay quay = new Quay();
        quay.setNetexId("NSR:Quay:1");
        BoardingPosition position = new BoardingPosition();
        position.setNetexId("NSR:BoardingPosition:1");
        position.setPublicCode(publicCode);
        position.setCentroid(centroid);
        quay.getBoardingPositions().add(position);
        return quay;
    }

    @Test
    public void inputWithIdUpdatesTheStoredPositionInPlace() {
        Quay quay = quayWithPosition("A");
        BoardingPosition stored = quay.getBoardingPositions().getFirst();

        updater.update(quay, List.of(Map.of(ID, "NSR:BoardingPosition:1", PUBLIC_CODE, "B")));

        assertThat(quay.getBoardingPositions()).hasSize(1);
        assertThat(quay.getBoardingPositions().getFirst()).isSameAs(stored);
        assertThat(stored.getNetexId()).isEqualTo("NSR:BoardingPosition:1");
        assertThat(stored.getPublicCode()).isEqualTo("B");
    }

    @Test
    public void inputWithoutIdCreatesANewPosition() {
        Quay quay = quayWithPosition("A");

        boolean changed = updater.update(quay, List.of(Map.of(PUBLIC_CODE, "A")));

        assertThat(changed).isTrue();
        assertThat(quay.getBoardingPositions()).hasSize(1);
        assertThat(quay.getBoardingPositions().getFirst().getNetexId()).isNull();
    }

    @Test
    public void idThatMatchesNothingIsRejectedWithBothIds() {
        Quay quay = quayWithPosition("A");

        assertThatThrownBy(() -> updater.update(quay, List.of(Map.of(ID, "NSR:BoardingPosition:9"))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("NSR:BoardingPosition:9")
                .hasMessageContaining("NSR:Quay:1");
    }

    @Test
    public void resendWithoutEditReportsNoChange() {
        Quay quay = quayWithPosition("A");

        boolean changed = updater.update(quay, List.of(Map.of(ID, "NSR:BoardingPosition:1", PUBLIC_CODE, "A")));

        assertThat(changed).isFalse();
    }

    @Test
    public void editedPublicCodeReportsChange() {
        Quay quay = quayWithPosition("A");

        boolean changed = updater.update(quay, List.of(Map.of(ID, "NSR:BoardingPosition:1", PUBLIC_CODE, "B")));

        assertThat(changed).isTrue();
    }

    @Test
    public void shorterListReportsChange() {
        Quay quay = quayWithPosition("A");

        boolean changed = updater.update(quay, List.of());

        assertThat(changed).isTrue();
        assertThat(quay.getBoardingPositions()).isEmpty();
    }

    @Test
    public void inputWithoutPublicCodeKeepsTheStoredCodeAndCentroid() {
        Quay quay = quayWithPosition("A");

        boolean changed = updater.update(quay, List.of(Map.of(ID, "NSR:BoardingPosition:1")));

        assertThat(changed).isFalse();
        assertThat(quay.getBoardingPositions().getFirst().getPublicCode()).isEqualTo("A");
        assertThat(quay.getBoardingPositions().getFirst().getCentroid()).isEqualTo(centroid);
    }

    @Test
    public void storedPositionWithoutIdDoesNotBreakMatching() {
        Quay quay = quayWithPosition("A");
        quay.getBoardingPositions().add(new BoardingPosition());

        boolean changed = updater.update(quay, List.of(Map.of(ID, "NSR:BoardingPosition:1", PUBLIC_CODE, "A")));

        assertThat(changed).isTrue();
        assertThat(quay.getBoardingPositions()).hasSize(1);
    }
}
