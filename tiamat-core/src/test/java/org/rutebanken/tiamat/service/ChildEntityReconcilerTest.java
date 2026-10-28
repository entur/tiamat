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
import org.rutebanken.tiamat.model.ParkingEntranceForVehicles;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ChildEntityReconcilerTest {

    private final ChildEntityReconciler reconciler = new ChildEntityReconciler();

    @Test
    public void inputWithIdPopulatesTheMatchingChildInPlace() {
        ParkingEntranceForVehicles stored = entrance("PRK:ParkingEntranceForVehicles:1");

        ParkingEntranceForVehicles result = reconcile(List.of(stored), Map.of("id", "PRK:ParkingEntranceForVehicles:1", "code", "A"));

        assertThat(result).isSameAs(stored);
        assertThat(result.getPublicCode()).isEqualTo("A");
    }

    @Test
    public void inputWithoutIdCreatesANewChild() {
        ParkingEntranceForVehicles stored = entrance("PRK:ParkingEntranceForVehicles:1");

        ParkingEntranceForVehicles result = reconcile(List.of(stored), Map.of("code", "B"));

        assertThat(result).isNotSameAs(stored);
        assertThat(result.getNetexId()).isNull();
        assertThat(result.getPublicCode()).isEqualTo("B");
        assertThat(stored.getPublicCode()).isNull();
    }

    @Test
    public void idThatMatchesNoChildIsRejectedWithChildAndParentId() {
        ParkingEntranceForVehicles stored = entrance("PRK:ParkingEntranceForVehicles:1");

        assertThatThrownBy(() -> reconcile(List.of(stored), Map.of("id", "PRK:ParkingEntranceForVehicles:2")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("PRK:ParkingEntranceForVehicles:2")
                .hasMessageContaining("PRK:Parking:7");
    }

    @Test
    public void idOfAChildOfAnotherParentIsRejected() {
        ParkingEntranceForVehicles ofOtherParent = entrance("PRK:ParkingEntranceForVehicles:9");

        assertThatThrownBy(() -> reconcile(List.of(entrance("PRK:ParkingEntranceForVehicles:1")),
                Map.of("id", ofOtherParent.getNetexId())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("PRK:ParkingEntranceForVehicles:9");
    }

    @Test
    public void sameIdTwiceInOneListIsRejected() {
        ParkingEntranceForVehicles stored = entrance("PRK:ParkingEntranceForVehicles:1");
        Map<String, Object> input = Map.of("id", "PRK:ParkingEntranceForVehicles:1");

        assertThatThrownBy(() -> reconciler.reconcileAll(List.of(stored), List.of(input, input), "PRK:Parking:7",
                ParkingEntranceForVehicles::new, (map, child) -> { }))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("PRK:ParkingEntranceForVehicles:1")
                .hasMessageContaining("PRK:Parking:7");
    }

    @Test
    public void reconcileAllKeepsTheInputOrderAndMixesMatchedAndNewChildren() {
        ParkingEntranceForVehicles stored = entrance("PRK:ParkingEntranceForVehicles:1");

        List<ParkingEntranceForVehicles> result = reconciler.reconcileAll(List.of(stored),
                List.of(Map.of("code", "new"), Map.of("id", "PRK:ParkingEntranceForVehicles:1", "code", "kept")),
                "PRK:Parking:7", ParkingEntranceForVehicles::new,
                (map, child) -> child.setPublicCode((String) map.get("code")));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getPublicCode()).isEqualTo("new");
        assertThat(result.get(1)).isSameAs(stored);
    }

    private ParkingEntranceForVehicles reconcile(List<ParkingEntranceForVehicles> existing, Map<String, Object> input) {
        return reconciler.reconcile(existing, input, "PRK:Parking:7", ParkingEntranceForVehicles::new,
                (map, child) -> child.setPublicCode((String) map.get("code")));
    }

    private static ParkingEntranceForVehicles entrance(String netexId) {
        ParkingEntranceForVehicles entrance = new ParkingEntranceForVehicles();
        entrance.setNetexId(netexId);
        return entrance;
    }
}
