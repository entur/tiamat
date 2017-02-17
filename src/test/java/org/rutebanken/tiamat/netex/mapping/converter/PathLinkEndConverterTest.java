package org.rutebanken.tiamat.netex.mapping.converter;

import org.junit.Test;
import org.rutebanken.netex.model.PathLinkEndStructure;
import org.rutebanken.netex.model.PlaceRef;
import org.rutebanken.tiamat.CommonSpringBootTest;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.getNetexId;

@Transactional
public class PathLinkEndConverterTest extends CommonSpringBootTest {

    @Autowired
    private NetexMapper netexMapper;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private QuayRepository quayRepository;

    @Test
    public void mapPathLinkWithStopsFromNetexToTiamat() {

        String fromId = "fromId";
        StopPlace fromTiamatStop = createAndPersistTiamatStopWithOriginalId(fromId);

        org.rutebanken.netex.model.StopPlace fromPlace = new org.rutebanken.netex.model.StopPlace();
        fromPlace.withId(fromId);

        String toId = "toId";

        org.rutebanken.netex.model.StopPlace toPlace = new org.rutebanken.netex.model.StopPlace();
        toPlace.withId(toId);
        StopPlace toTiamatStop = createAndPersistTiamatStopWithOriginalId(toId);

        org.rutebanken.netex.model.PathLink netexPathLink = new org.rutebanken.netex.model.PathLink()
                .withFrom(
                        new PathLinkEndStructure()
                                .withPlaceRef(
                                        new PlaceRef()
                                                .withRef(fromPlace.getId())
                                                .withNameOfMemberClass(fromPlace.getClass().getSimpleName())))
                .withTo(
                        new PathLinkEndStructure()
                                .withPlaceRef(
                                        new PlaceRef()
                                                .withRef(toPlace.getId())
                                                .withNameOfMemberClass(fromPlace.getClass().getSimpleName())));

        List<PathLink> actual = netexMapper.mapPathLinksToTiamatModel(Arrays.asList(netexPathLink));
        assertThat(actual).isNotEmpty();

        PathLink actualPathLink = actual.get(0);
        assertPathLinkEnds(actualPathLink, fromTiamatStop, fromId, toTiamatStop, toId);
    }

    @Test
    public void mapPathLinkWithQuaysFromNetexToTiamat() {

        String fromId = "fromId";
        Quay fromTiamatQuay = createAndPersistTiamatQuayWithOriginalId(fromId);

        org.rutebanken.netex.model.Quay fromQuay = new org.rutebanken.netex.model.Quay();
        fromQuay.withId(fromId);

        String toId = "toId";

        org.rutebanken.netex.model.Quay toQuay = new org.rutebanken.netex.model.Quay();
        toQuay.withId(toId);

        Quay toTiamatQuay = createAndPersistTiamatQuayWithOriginalId(toId);

        org.rutebanken.netex.model.PathLink netexPathLink = new org.rutebanken.netex.model.PathLink()
                .withFrom(
                        new PathLinkEndStructure()
                                .withPlaceRef(
                                        new PlaceRef()
                                                .withRef(fromQuay.getId())
                                                .withNameOfMemberClass(fromQuay.getClass().getSimpleName())))
                .withTo(
                        new PathLinkEndStructure()
                                .withPlaceRef(
                                        new PlaceRef()
                                                .withRef(toQuay.getId())
                                                .withNameOfMemberClass(toQuay.getClass().getSimpleName())));

        List<PathLink> actual = netexMapper.mapPathLinksToTiamatModel(Arrays.asList(netexPathLink));
        assertThat(actual).hasSize(1);

        PathLink actualPathLink = actual.get(0);
        assertPathLinkEnds(actualPathLink, fromTiamatQuay, fromId, toTiamatQuay, toId);
    }

    @Test
    public void mapPathLinkWithInternalIdsFromNetexToTiamat() {
        StopPlace fromTiamatStop = new StopPlace();
        stopPlaceRepository.save(fromTiamatStop);
        String fromStopPlaceId = NetexIdMapper.getNetexId(fromTiamatStop);

        org.rutebanken.netex.model.StopPlace fromStopPlace = new org.rutebanken.netex.model.StopPlace();
        fromStopPlace.withId(fromStopPlaceId);

        Quay toTiamatQuay = new Quay();
        quayRepository.save(toTiamatQuay);
        String toId = NetexIdMapper.getNetexId(toTiamatQuay);
        org.rutebanken.netex.model.Quay toQuay = new org.rutebanken.netex.model.Quay();

        toQuay.withId(toId);

        org.rutebanken.netex.model.PathLink netexPathLink = new org.rutebanken.netex.model.PathLink()
                .withFrom(
                        new PathLinkEndStructure()
                                .withPlaceRef(
                                        new PlaceRef()
                                                .withRef(fromStopPlace.getId())
                                                .withNameOfMemberClass(fromStopPlace.getClass().getSimpleName())))
                .withTo(
                        new PathLinkEndStructure()
                                .withPlaceRef(
                                        new PlaceRef()
                                                .withRef(toQuay.getId())
                                                .withNameOfMemberClass(toQuay.getClass().getSimpleName())));

        List<PathLink> actual = netexMapper.mapPathLinksToTiamatModel(Arrays.asList(netexPathLink));
        assertThat(actual).hasSize(1);

        PathLink actualPathLink = actual.get(0);
        assertPathLinkEnds(actualPathLink, fromTiamatStop, null, toTiamatQuay, null);
    }


    @Test
    public void mapPathLinkToNetex() {
        Quay quay = new Quay();
        quay.setId(10L);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setId(11L);

        PathLink pathLink = new PathLink(new PathLinkEnd(quay), new PathLinkEnd(stopPlace));
        pathLink.setId(123L);

        org.rutebanken.netex.model.PathLink netexPathLink = netexMapper.mapToNetexModel(pathLink);

        assertThat(netexPathLink).describedAs("Mapped path link shall not be null").isNotNull();
        assertThat(netexPathLink.getId()).isEqualTo(getNetexId(pathLink, pathLink.getId()));
        verifyPathLinkEnd(netexPathLink.getFrom(), quay.getId(), quay, "PathlinkEnd from");
        verifyPathLinkEnd(netexPathLink.getTo(), stopPlace.getId(), stopPlace, "PathLinkEnd to");

    }

    private void verifyPathLinkEnd(PathLinkEndStructure pathLinkEndStructure, long entityId, EntityStructure entityStructure, String describedAs) {
        assertThat(pathLinkEndStructure).describedAs(describedAs).isNotNull();
        assertThat(pathLinkEndStructure.getPlaceRef()).describedAs(describedAs).isNotNull();
        assertThat(pathLinkEndStructure.getPlaceRef().getRef()).isEqualTo(NetexIdMapper.getNetexId(entityStructure, entityId));
    }

    private void assertPathLinkEnds(PathLink actualPathLinkEnd, SiteElement fromPlace, String expectedOriginalFromId, SiteElement toPlace, String expectedOriginalToId) {
        assertPathLinkEndEqualTo(actualPathLinkEnd.getFrom(), fromPlace, expectedOriginalFromId);
        assertPathLinkEndEqualTo(actualPathLinkEnd.getTo(), toPlace, expectedOriginalToId);
    }

    private void assertPathLinkEndEqualTo(PathLinkEnd actualPathLinkEnd, SiteElement expectedPlace, String expectedOriginalId) {
        assertThat(actualPathLinkEnd).isNotNull();
        AddressablePlace place = getPlaceFromPathLinkEnd(actualPathLinkEnd);
        assertThat(place.getId()).isEqualTo(expectedPlace.getId());
        if(expectedOriginalId != null) {
            assertThat(place.getOriginalIds().iterator().next()).isEqualTo(expectedOriginalId);
        }
    }

    private SiteElement getPlaceFromPathLinkEnd(PathLinkEnd pathLinkEnd) {
        Optional<SiteElement> found = Arrays.asList(pathLinkEnd.getQuay(), pathLinkEnd.getStopPlace(), pathLinkEnd.getEntrance())
                .stream()
                .filter(Objects::nonNull)
                .findFirst();
        assertThat(found).describedAs("Expected pathLinkEnd to contain place").isPresent();
        return found.get();
    }

    private StopPlace createAndPersistTiamatStopWithOriginalId(String originalId) {
        StopPlace stopPlace = new StopPlace();
        stopPlace.getOriginalIds().add(originalId);
        stopPlaceRepository.save(stopPlace);
        return stopPlace;
    }

    private Quay createAndPersistTiamatQuayWithOriginalId(String originalId) {
        Quay quay = new Quay();
        quay.getOriginalIds().add(originalId);
        quayRepository.save(quay);
        return quay;
    }

}