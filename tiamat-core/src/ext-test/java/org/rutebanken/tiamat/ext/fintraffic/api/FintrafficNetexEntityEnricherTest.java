package org.rutebanken.tiamat.ext.fintraffic.api;

import jakarta.xml.bind.JAXBElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.netex.model.Parking;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.Quays_RelStructure;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.TopographicPlace;

import javax.xml.namespace.QName;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

class FintrafficNetexEntityEnricherTest {

    private FintrafficNetexEntityEnricher enricher;

    @BeforeEach
    void setUp() {
        enricher = new FintrafficNetexEntityEnricher();
    }

    @Test
    void enrichStopPlace_addsNumericIdWithOffset() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setId("ABC:StopPlace:123");

        enricher.enrich(stopPlace);

        assertNumericId(stopPlace.getKeyList().getKeyValue(), 1_000_123L);
    }

    @Test
    void enrichStopPlace_withExistingKeyList_appendsNumericId() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setId("ABC:StopPlace:1");
        stopPlace.withKeyList(new org.rutebanken.netex.model.KeyListStructure()
                .withKeyValue(new KeyValueStructure().withKey("existingKey").withValue("existingValue")));

        enricher.enrich(stopPlace);

        List<KeyValueStructure> keyValues = stopPlace.getKeyList().getKeyValue();
        assertThat(keyValues).hasSize(2);
        assertThat(keyValues).anyMatch(kv -> "existingKey".equals(kv.getKey()));
        assertNumericId(keyValues, 1_000_001L);
    }

    @Test
    void enrichStopPlace_withQuays_addsNumericIdToEachQuay() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setId("ABC:StopPlace:10");

        Quay quay1 = new Quay();
        quay1.setId("ABC:Quay:1");
        Quay quay2 = new Quay();
        quay2.setId("ABC:Quay:2");

        stopPlace.withQuays(new Quays_RelStructure()
                .withQuayRefOrQuay(toJaxbElement(quay1), toJaxbElement(quay2)));

        enricher.enrich(stopPlace);

        assertNumericId(stopPlace.getKeyList().getKeyValue(), 1_000_010L);
        assertNumericId(quay1.getKeyList().getKeyValue(), 5_000_001L);
        assertNumericId(quay2.getKeyList().getKeyValue(), 5_000_002L);
    }

    @Test
    void enrichStopPlace_withEmptyQuays_doesNotFail() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setId("ABC:StopPlace:10");
        stopPlace.withQuays(new Quays_RelStructure());

        assertThatNoException().isThrownBy(() -> enricher.enrich(stopPlace));
        assertNumericId(stopPlace.getKeyList().getKeyValue(), 1_000_010L);
    }

    @Test
    void enrichStopPlace_withNullQuays_doesNotFail() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setId("ABC:StopPlace:10");

        assertThatNoException().isThrownBy(() -> enricher.enrich(stopPlace));
        assertNumericId(stopPlace.getKeyList().getKeyValue(), 1_000_010L);
    }

    @Test
    void enrichParking_doesNothing() {
        Parking parking = new Parking();
        parking.setId("ABC:Parking:1");

        enricher.enrich(parking);

        assertThat(parking.getKeyList()).isNull();
    }

    @Test
    void enrichTopographicPlace_doesNothing() {
        TopographicPlace tp = new TopographicPlace();
        tp.setId("ABC:TopographicPlace:1");

        enricher.enrich(tp);

        assertThat(tp.getKeyList()).isNull();
    }

    @Test
    void enrichStopPlace_withMalformedId_doesNotThrow() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setId("ABC:StopPlace:not-a-number");

        assertThatNoException().isThrownBy(() -> enricher.enrich(stopPlace));
        assertThat(stopPlace.getKeyList()).isNull();
    }

    @Test
    void enrichStopPlace_withNullId_doesNotThrow() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setId(null);

        assertThatNoException().isThrownBy(() -> enricher.enrich(stopPlace));
        assertThat(stopPlace.getKeyList()).isNull();
    }

    @Test
    void enrichQuay_withMalformedId_doesNotThrow() {
        Quay quay = new Quay();
        quay.setId("ABC:Quay:bad");

        assertThatNoException().isThrownBy(() -> enricher.enrich(quay));
        assertThat(quay.getKeyList()).isNull();
    }

    private static void assertNumericId(List<KeyValueStructure> keyValues, long expectedValue) {
        assertThat(keyValues)
                .filteredOn(kv -> "peti_numeric_id".equals(kv.getKey()))
                .hasSize(1)
                .first()
                .extracting(KeyValueStructure::getValue)
                .isEqualTo(String.valueOf(expectedValue));
    }

    private static JAXBElement<Quay> toJaxbElement(Quay quay) {
        return new JAXBElement<>(new QName("Quay"), Quay.class, quay);
    }
}

