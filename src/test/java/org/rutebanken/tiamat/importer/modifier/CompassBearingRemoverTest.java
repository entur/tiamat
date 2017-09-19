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

import org.junit.Test;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class CompassBearingRemoverTest {

    private CompassBearingRemover compassBearingRemover = new CompassBearingRemover(new String[]{"airport"});

    @Test
    public void removeCompassBearingForAirportQuay() throws Exception {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setStopPlaceType(StopTypeEnumeration.AIRPORT);

        Quay quay = new Quay();
        quay.setCompassBearing(123.123f);
        stopPlace.getQuays().add(quay);

        compassBearingRemover.remove(stopPlace);

        assertThat(quay.getCompassBearing()).isNull();
    }

    @Test
    public void keepCompassBearingForBusQuay() throws Exception {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setStopPlaceType(StopTypeEnumeration.BUS_STATION);

        Quay quay = new Quay();
        quay.setCompassBearing(123.123f);
        stopPlace.getQuays().add(quay);

        compassBearingRemover.remove(stopPlace);

        assertThat(quay.getCompassBearing()).isNotNull();
    }
}