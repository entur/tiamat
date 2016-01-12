package no.rutebanken.tiamat.service;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import no.rutebanken.tiamat.nvdb.service.NvdbSearchService;
import no.rutebanken.tiamat.pelias.CountyAndMunicipalityLookupService;
import no.rutebanken.tiamat.repository.ifopt.QuayRepository;
import no.rutebanken.tiamat.repository.ifopt.StopPlaceRepository;
import org.junit.Test;
import uk.org.netex.netex.Quay;
import uk.org.netex.netex.SimplePoint;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;

public class StopPlaceFromQuaysCorrelationServiceTest {
    private GeometryFactory geometryFactory = new GeometryFactory();

    private StopPlaceFromQuaysCorrelationService stopPlaceFromQuaysCorrelationService =
            new StopPlaceFromQuaysCorrelationService(mock(QuayRepository.class),
                    mock(StopPlaceRepository.class),
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
}