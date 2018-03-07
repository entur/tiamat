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

package org.rutebanken.tiamat.service.stopplace;

import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.*;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PathLinkUpdaterServiceTest extends TiamatIntegrationTest {

    @Autowired
    private PathLinkUpdaterService pathLinkUpdaterService;

    @Test
    public void testThatFromAndToMustBePresent() {

        StopPlace stopPlace = new StopPlace();

        stopPlaceRepository.save(stopPlace);

        PathLinkEnd from = new PathLinkEnd(new AddressablePlaceRefStructure(stopPlace));

        PathLink pathLink = new PathLink();
        pathLink.setFrom(from);

        // The path link lacks pathlinkend (to) and must not be persisted

        assertThatThrownBy(() -> pathLinkUpdaterService.createOrUpdatePathLink(pathLink))
                .hasMessageContaining("PathLinkEnd");
    }
}