package no.rutebanken.tiamat.service;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import no.rutebanken.tiamat.nvdb.service.NvdbSearchService;
import no.rutebanken.tiamat.pelias.CountyAndMunicipalityLookupService;
import no.rutebanken.tiamat.repository.ifopt.QuayRepository;
import no.rutebanken.tiamat.repository.ifopt.StopPlaceRepository;
import org.junit.Test;
import uk.org.netex.netex.MultilingualString;
import uk.org.netex.netex.Quay;
import uk.org.netex.netex.SimplePoint;
import uk.org.netex.netex.StopPlace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StopPlaceFromQuaysCorrelationServiceTest {
    private GeometryFactory geometryFactory = new GeometryFactory();

    private QuayRepository quayRepository = mock(QuayRepository.class);

    private StopPlaceRepository stopPlaceRepository = mock(StopPlaceRepository.class);

    private StopPlaceFromQuaysCorrelationService stopPlaceFromQuaysCorrelationService =
            new StopPlaceFromQuaysCorrelationService(quayRepository,
                    stopPlaceRepository,
                    geometryFactory,
                    mock(CountyAndMunicipalityLookupService.class), mock(NvdbSearchService.class));

    @Test
    public void quaysAreNotClose() throws Exception {
        Quay quay = quayWithCentroid(9.489552, 59.866439);
        Quay nearbyQuay = quayWithCentroid(10.488425, 59.865190);
        assertThat(stopPlaceFromQuaysCorrelationService.areClose(quay, nearbyQuay)).isFalse();
    }

    /**
     * Typical two stop points nearby, but not close enough.
     * About ~693 meters
     * https://www.google.no/maps/search/59.865190,+10.488425
     * https://www.google.no/maps/search/59.858964,+10.493528
     *
     * See also http://rechneronline.de/geo-coordinates/
     */
    @Test
    public void quaysAreCloseButNotCloseEnough() throws Exception {
        Quay quay = quayWithCentroid(10.488425, 59.865190);
        Quay nearbyQuay = quayWithCentroid(10.493528, 59.858964);
        assertThat(stopPlaceFromQuaysCorrelationService.areClose(quay, nearbyQuay)).isFalse();
    }

    /**
     * Typical two stop points on each side on the road related to one stop place.
     * https://www.google.no/maps/search/59.865190,+10.488425
     * https://www.google.no/maps/search/59.866439,+10.489552
     */
    @Test
    public void quaysAreClose() throws Exception {
        Quay quay = quayWithCentroid(10.489552, 59.866439);
        Quay nearbyQuay = quayWithCentroid(10.488425, 59.865190);
        assertThat(stopPlaceFromQuaysCorrelationService.areClose(quay, nearbyQuay)).isTrue();
    }

    private Quay quayWithCentroid(double x, double y) {
        Quay quay = new Quay();
        quay.setCentroid(new SimplePoint());
        quay.getCentroid().setLocation(geometryFactory.createPoint(new Coordinate(x, y, 0)));
        return quay;
    }

    @Test
    public void correlateFourQuaysAndExpectThreeStopPlacesSaved() {

        Quay quayWithSameName1 = quayWithCentroid(10.489552, 59.866439);
        quayWithSameName1.setId("1");
        quayWithSameName1.setName(new MultilingualString("name", "no", ""));

        Quay quayWithSameName2 = quayWithCentroid(10.489552, 59.866439);
        quayWithSameName2.setId("2");
        quayWithSameName2.setName(new MultilingualString("name", "no", ""));

        Quay quayWithSameNameButFarAway = quayWithCentroid(4.489552, 59.866439);
        quayWithSameNameButFarAway.setId("3");
        quayWithSameNameButFarAway.setName(new MultilingualString("name", "no", ""));

        Quay quayWithOtherName = quayWithCentroid(10.489552, 59.866439);
        quayWithOtherName.setId("4");
        quayWithOtherName.setName(new MultilingualString("othername", "no", ""));

        List<Quay> quays = Arrays.asList(quayWithSameName1, quayWithSameName2, quayWithSameNameButFarAway, quayWithOtherName);

        List<StopPlace> savedStopPlaces = new ArrayList<>();
        when(stopPlaceRepository.save(any(StopPlace.class))).thenAnswer(invocationOnMock -> {
            StopPlace stopPlace = (StopPlace) invocationOnMock.getArguments()[0];
            savedStopPlaces.add(stopPlace);
            return stopPlace;
        });

        when(quayRepository.findAll()).thenReturn(quays);

        stopPlaceFromQuaysCorrelationService.correlate();

        assertThat(savedStopPlaces.size()).isEqualTo(3);

    }



    @Test
    public void threeQuaysWithSameNameButDifferentLocationExpectThreeStopPlaces() {

        Quay quay1 = quayWithCentroid(4.0, 59.866439);
        quay1.setId("1");
        quay1.setName(new MultilingualString("name", "no", ""));

        Quay quay2 = quayWithCentroid(5.0, 59.866439);
        quay2.setId("2");
        quay2.setName(new MultilingualString("name", "no", ""));

        Quay quay3 = quayWithCentroid(6.0, 59.866439);
        quay3.setId("3");
        quay3.setName(new MultilingualString("name", "no", ""));

        List<Quay> quays = Arrays.asList(quay1, quay2, quay3);

        List<StopPlace> savedStopPlaces = new ArrayList<>();
        when(stopPlaceRepository.save(any(StopPlace.class))).thenAnswer(invocationOnMock -> {
            StopPlace stopPlace = (StopPlace) invocationOnMock.getArguments()[0];
            savedStopPlaces.add(stopPlace);
            return stopPlace;
        });

        when(quayRepository.findAll()).thenReturn(quays);

        stopPlaceFromQuaysCorrelationService.correlate();

        assertThat(savedStopPlaces.size()).isEqualTo(3);

    }

}