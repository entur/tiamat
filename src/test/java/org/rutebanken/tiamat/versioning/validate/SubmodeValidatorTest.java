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

package org.rutebanken.tiamat.versioning.validate;

import org.junit.Test;
import org.rutebanken.tiamat.model.BusSubmodeEnumeration;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TramSubmodeEnumeration;
import org.rutebanken.tiamat.versioning.validate.SubmodeValidator;

public class SubmodeValidatorTest {

    @Test(expected = IllegalArgumentException.class)
    public void validateExpectIllegalArgumentException() {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setBusSubmode(BusSubmodeEnumeration.RAIL_REPLACEMENT_BUS);
        stopPlace.setTramSubmode(TramSubmodeEnumeration.LOCAL_TRAM);

        new SubmodeValidator().validate(stopPlace);
    }

    @Test
    public void validate() {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setTramSubmode(TramSubmodeEnumeration.LOCAL_TRAM);

        new SubmodeValidator().validate(stopPlace);
    }

    @Test
    public void validateUnknown() {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setTramSubmode(TramSubmodeEnumeration.LOCAL_TRAM);
        stopPlace.setBusSubmode(BusSubmodeEnumeration.UNKNOWN);

        new SubmodeValidator().validate(stopPlace);
    }

    @Test
    public void validateUndefined() {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setTramSubmode(TramSubmodeEnumeration.LOCAL_TRAM);
        stopPlace.setBusSubmode(BusSubmodeEnumeration.UNDEFINED);

        new SubmodeValidator().validate(stopPlace);
    }
}