package org.rutebanken.tiamat.importer.initial;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.versioning.save.StopPlaceVersionedSaverService;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StopPlaceParentChildProcessorTest {

    private final StopPlaceVersionedSaverService stopPlaceVersionedSaverService = mock(StopPlaceVersionedSaverService.class);

    private final StopPlaceParentCreator parentStopPlaceCreator = mock(StopPlaceParentCreator.class);

    private StopPlaceParentChildProcessor processor;
    private AtomicInteger stopPlacesCreated;

    @BeforeEach
    void setUp() {
        stopPlacesCreated = new AtomicInteger(0);
        processor = new StopPlaceParentChildProcessor(
                stopPlaceVersionedSaverService,
                parentStopPlaceCreator,
                stopPlacesCreated);
    }

    @Test
    void processStopPlaceShouldDeferProcessingForParentStopPlace() {
        StopPlace parentStop = new StopPlace();
        parentStop.setNetexId("NSR:StopPlace:1");
        parentStop.setName(new EmbeddableMultilingualString("Parent Stop", "no"));

        Stream<StopPlace> result = processor.processStopPlace(parentStop);

        assertThat(result).isEmpty();
        verify(stopPlaceVersionedSaverService, never()).saveNewVersion(any());
        assertThat(stopPlacesCreated.get()).isEqualTo(0);
    }

    @Test
    void processStopPlaceShouldSaveChildStopPlaceImmediately() {
        StopPlace childStop = new StopPlace();
        childStop.setNetexId("NSR:StopPlace:2");
        childStop.setName(new EmbeddableMultilingualString("Child Stop", "no"));

        Quay quay = new Quay();
        quay.setNetexId("NSR:Quay:1");
        childStop.getQuays().add(quay);

        SiteRefStructure parentRef = new SiteRefStructure();
        parentRef.setRef("NSR:StopPlace:1");
        childStop.setParentSiteRef(parentRef);

        StopPlace savedChild = new StopPlace();
        savedChild.setNetexId("NSR:StopPlace:2");
        when(stopPlaceVersionedSaverService.saveNewVersion(childStop)).thenReturn(savedChild);

        Stream<StopPlace> result = processor.processStopPlace(childStop);

        assertThat(result).containsExactly(savedChild);
        assertThat(stopPlacesCreated.get()).isEqualTo(1);
        assertThat(childStop.getParentSiteRef()).isNull();
    }

    @Test
    void processStopPlaceShouldSaveStandaloneStopPlaceImmediately() {
        StopPlace standaloneStop = new StopPlace();
        standaloneStop.setNetexId("NSR:StopPlace:3");
        standaloneStop.setName(new EmbeddableMultilingualString("Standalone", "no"));

        Quay quay = new Quay();
        quay.setNetexId("NSR:Quay:1");
        standaloneStop.getQuays().add(quay);

        StopPlace savedStandalone = new StopPlace();
        savedStandalone.setNetexId("NSR:StopPlace:3");
        when(stopPlaceVersionedSaverService.saveNewVersion(standaloneStop)).thenReturn(savedStandalone);

        Stream<StopPlace> result = processor.processStopPlace(standaloneStop);

        assertThat(result).containsExactly(savedStandalone);
        assertThat(stopPlacesCreated.get()).isEqualTo(1);
    }

    @Test
    void createAndSaveParentStopPlacesShouldCreateParentStopPlaceWithChildren() {
        StopPlace childA = createChildStopPlace("NSR:StopPlace:101", "Child A");
        StopPlace childB = createChildStopPlace("NSR:StopPlace:102", "Child B");

        SiteRefStructure parentRef = new SiteRefStructure();
        parentRef.setRef("ORIGINAL:Parent:1");
        childA.setParentSiteRef(parentRef);
        childB.setParentSiteRef(parentRef);

        StopPlace savedChildA = new StopPlace();
        savedChildA.setNetexId("NSR:StopPlace:101");
        StopPlace savedChildB = new StopPlace();
        savedChildB.setNetexId("NSR:StopPlace:102");

        when(stopPlaceVersionedSaverService.saveNewVersion(childA)).thenReturn(savedChildA);
        when(stopPlaceVersionedSaverService.saveNewVersion(childB)).thenReturn(savedChildB);

        processor.processStopPlace(childA);
        processor.processStopPlace(childB);

        StopPlace parentStop = new StopPlace();
        parentStop.setNetexId(null);
        parentStop.getOriginalIds().add("ORIGINAL:Parent:1");
        parentStop.setName(new EmbeddableMultilingualString("Parent Stop", "no"));

        processor.processStopPlace(parentStop);

        StopPlace savedParent = new StopPlace();
        savedParent.setNetexId("NSR:StopPlace:100");
        savedParent.setParentStopPlace(true);
        when(parentStopPlaceCreator.createParentStopWithChildren(eq(parentStop), any()))
                .thenReturn(savedParent);

        List<StopPlace> parents = processor.createAndSaveParentStopPlaces();

        assertThat(parents).hasSize(1);
        assertThat(parents.getFirst()).isEqualTo(savedParent);
        assertThat(stopPlacesCreated.get()).isEqualTo(3);
    }

    @Test
    void createAndSaveParentStopPlacesShouldThrowExceptionForParentWithoutChildren() {
        StopPlace parentStop = new StopPlace();
        parentStop.setNetexId("NSR:StopPlace:999");
        parentStop.getOriginalIds().add("ORIGINAL:Parent:999");
        parentStop.setName(new EmbeddableMultilingualString("Orphan parent", "no"));

        processor.processStopPlace(parentStop);

        assertThatThrownBy(() -> processor.createAndSaveParentStopPlaces())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Invalid stop place without quays or children")
                .hasMessageContaining("NSR:StopPlace:999");
    }

    private StopPlace createChildStopPlace(String netexId, String name) {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId(netexId);
        stopPlace.setName(new EmbeddableMultilingualString(name, "no"));

        Quay quay = new Quay();
        quay.setNetexId(netexId + ":Quay:1");
        stopPlace.getQuays().add(quay);

        return stopPlace;
    }
}
