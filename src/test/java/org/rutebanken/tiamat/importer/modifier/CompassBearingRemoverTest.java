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
    public void keepCompassBearingForAirportQuay() throws Exception {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setStopPlaceType(StopTypeEnumeration.BUS_STATION);

        Quay quay = new Quay();
        quay.setCompassBearing(123.123f);
        stopPlace.getQuays().add(quay);

        compassBearingRemover.remove(stopPlace);

        assertThat(quay.getCompassBearing()).isNotNull();
    }
}