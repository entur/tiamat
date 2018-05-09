package org.rutebanken.tiamat.versioning;

import org.junit.Test;
import org.rutebanken.tiamat.model.BusSubmodeEnumeration;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TramSubmodeEnumeration;

import static org.junit.Assert.*;

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

}