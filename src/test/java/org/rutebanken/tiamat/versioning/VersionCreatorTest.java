package org.rutebanken.tiamat.versioning;

import com.vividsolutions.jts.geom.Coordinate;
import org.junit.Ignore;
import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class VersionCreatorTest extends TiamatIntegrationTest {

    @Autowired
    private VersionCreator versionCreator;


    @Test
    public void createCopyOfStopWithGeometry() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(1L);
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(59.0, 11.1)));
        stopPlace = stopPlaceRepository.save(stopPlace);

        StopPlace newVersion = versionCreator.createCopy(stopPlace, StopPlace.class);
        assertThat(newVersion.getCentroid()).isNotNull();
    }

    @Test
    public void unsavedNewVersionShouldNotHavePrimaryKey() throws NoSuchFieldException, IllegalAccessException {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(1L);

        ValidBetween validBetween = new ValidBetween(ZonedDateTime.now());
        validBetween.getOriginalIds().add("1000");

        stopPlace.getValidBetweens().add(validBetween);

        // Save first version
        stopPlace = stopPlaceRepository.save(stopPlace);
        stopPlaceRepository.flush();

        Object firstVersionValidBetweenId = getIdValue(stopPlace.getValidBetweens().get(0));
        assertThat(firstVersionValidBetweenId).isNotNull();

        // Create new version
        StopPlace newVersion = versionCreator.createCopy(stopPlace, StopPlace.class);

        Object actualStopPlaceId = getIdValue(newVersion);
        assertThat(actualStopPlaceId).isNull();

        // Check that ID of ValidBetween has been excluded in the new version
        Object actualValidBetweenId = getIdValue(newVersion.getValidBetweens().get(0));
        assertThat(actualValidBetweenId)
                .as("The id value of valid between should not have been mapped by orika: " + actualValidBetweenId)
                .isNull();
    }

    @Ignore
    @Test
    public void deepCopiedObjectShouldHaveOriginalId() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(1L);
        stopPlace.getOriginalIds().add("original-id");
        stopPlace = stopPlaceRepository.save(stopPlace);

        ValidBetween validBetween = new ValidBetween(ZonedDateTime.now());
        validBetween.getOriginalIds().add("1000");
        stopPlace.getValidBetweens().add(validBetween);

        StopPlace newVersion = versionCreator.createCopy(stopPlace, StopPlace.class);
        assertThat(newVersion.getOriginalIds()).hasSize(1);
        assertThat(newVersion.getValidBetweens().get(0).getOriginalIds()).hasSize(1);
    }

    private Object getIdValue(IdentifiedEntity entity) throws NoSuchFieldException, IllegalAccessException {
        Field field = IdentifiedEntity.class.getDeclaredField("id");
        field.setAccessible(true);
        return field.get(entity);
    }

    @Test
    public void createNewVersionOfStopWithZonedDateTime() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(1L);
        stopPlace.setChanged(ZonedDateTime.now());
        stopPlace = stopPlaceRepository.save(stopPlace);
        StopPlace newVersion = versionCreator.createCopy(stopPlace, StopPlace.class);
        assertThat(newVersion.getChanged()).isNotNull();
    }

    @Test
    public void stopPlaceQuayShouldAlsoHaveItsVersionIncremented() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(1L);

        Quay quay = new Quay();
        quay.setVersion(1L);

        stopPlace.getQuays().add(quay);

        stopPlaceRepository.save(stopPlace);

        StopPlace newVersion = versionCreator.createCopy(stopPlace);
        newVersion = versionCreator.initiateOrIncrementVersions(newVersion);
        assertThat(newVersion.getQuays()).isNotEmpty();
        assertThat(newVersion.getQuays().iterator().next().getVersion()).isEqualTo(2L);
    }

    @Ignore // Should be testing future path link saver service
    @Test
    public void createNewVersionOfPathLink() {
        Quay fromQuay = new Quay();
        fromQuay.setVersion(1L);
        fromQuay = quayRepository.save(fromQuay);

        Quay toQuay = new Quay();
        toQuay.setVersion(1L);
        toQuay = quayRepository.save(toQuay);

        PathLinkEnd pathLinkEndFromQuay = new PathLinkEnd(new AddressablePlaceRefStructure(fromQuay.getNetexId(), String.valueOf(fromQuay.getVersion())));
        PathLinkEnd pathLinkEndToQuay = new PathLinkEnd(new AddressablePlaceRefStructure(toQuay.getNetexId(), String.valueOf(toQuay.getVersion())));

        PathLink pathLink = new PathLink(pathLinkEndFromQuay, pathLinkEndToQuay);
        pathLink.setVersion(1L);

        pathLink = pathLinkRepository.save(pathLink);

        PathLink newVersion = versionCreator.createCopy(pathLink, PathLink.class);

        assertThat(newVersion.getVersion())
                .describedAs("The version of path link should have been incremented")
                .isEqualTo(pathLink.getVersion()+1);

        newVersion = pathLinkRepository.save(newVersion);

        PathLink actualNewVersionPathLink = pathLinkRepository.findFirstByNetexIdOrderByVersionDesc(newVersion.getNetexId());

        assertThat(actualNewVersionPathLink.getVersion()).isEqualTo(2L);
        assertThat(actualNewVersionPathLink.getFrom().getPlaceRef().getRef()).isEqualTo(fromQuay.getNetexId());
        assertThat(actualNewVersionPathLink.getTo().getPlaceRef().getRef()).isEqualTo(toQuay.getNetexId());

        PathLink actualOldVersionPathLink = pathLinkRepository.findFirstByNetexIdAndVersion(newVersion.getNetexId(), 1L);
        assertThat(actualOldVersionPathLink).isNotNull();
    }

    @Test
    public void newVersionShouldHaveValidBetween() {
        StopPlace oldVersion = new StopPlace();
        oldVersion.setVersion(1L);

        Quay quay = new Quay();
        quay.setVersion(1L);

        oldVersion.getQuays().add(quay);

        oldVersion.getValidBetweens().add(new ValidBetween(ZonedDateTime.now().minusDays(2)));

        oldVersion = stopPlaceRepository.save(oldVersion);

        ZonedDateTime beforeCreated = ZonedDateTime.now();
        System.out.println(beforeCreated);

        StopPlace newVersion = versionCreator.createCopy(oldVersion);

        oldVersion = versionCreator.terminateVersion(oldVersion, ZonedDateTime.now());

        assertThat(newVersion.getValidBetweens())
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        System.out.println(oldVersion.getValidBetweens().get(0).getToDate());
        assertThat(oldVersion.getValidBetweens().get(0).getToDate()).isAfterOrEqualTo(beforeCreated);


        ValidBetween validBetween = newVersion.getValidBetweens().get(0);
        assertThat(validBetween.getFromDate()).isAfterOrEqualTo(beforeCreated);
        assertThat(validBetween.getToDate()).isNull();

    }

}