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

package org.rutebanken.tiamat.versioning;

import org.junit.Test;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class VersionIncrementorTest {
    @Test
    public void stopPlaceQuayShouldAlsoHaveItsVersionIncremented() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(1L);
        stopPlace.setNetexId("NSR:StopPlace:1");

        Quay quay = new Quay();
        quay.setVersion(2L);
        quay.setNetexId("NSR:Quay:2");

        stopPlace.getQuays().add(quay);


        StopPlace newVersion = new VersionIncrementor().initiateOrIncrementVersions(stopPlace);
        assertThat(newVersion.getVersion()).isEqualTo(2L);
        assertThat(newVersion.getQuays()).isNotEmpty();
        assertThat(newVersion.getQuays().iterator().next().getVersion()).isEqualTo(3L);
    }

}