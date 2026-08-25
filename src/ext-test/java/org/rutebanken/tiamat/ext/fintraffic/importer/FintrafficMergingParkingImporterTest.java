package org.rutebanken.tiamat.ext.fintraffic.importer;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.ext.fintraffic.FintrafficIntegrationTest;
import org.rutebanken.tiamat.ext.fintraffic.FintrafficTiamatTestApplication;
import org.rutebanken.tiamat.ext.fintraffic.model.FintrafficInfoLink;
import org.rutebanken.tiamat.ext.fintraffic.model.FintrafficParking;
import org.rutebanken.tiamat.ext.fintraffic.model.FintrafficParkingAvailabilityCondition;
import org.rutebanken.tiamat.ext.fintraffic.model.FintrafficParkingEntranceForVehicles;
import org.rutebanken.tiamat.importer.merging.MergingParkingImporter;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.PaymentMethodEnumeration;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.ParkingRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.versioning.save.ParkingVersionedSaverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link FintrafficMergingParkingImporter}, verifying that
 * {@link FintrafficParking#getPaymentMethods() paymentMethods} are preserved on both
 * import paths:
 * <ul>
 *   <li>New parking — {@code handleCompletelyNewParking} saves the incoming entity as-is</li>
 *   <li>Existing parking — {@code handleAlreadyExistingParking} merges {@code paymentMethods}
 *       from the incoming parking into the version copy via {@link MergingParkingImporter#mergeExtendedFields}</li>
 * </ul>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = FintrafficTiamatTestApplication.class
)
@ActiveProfiles({"test", "gcs-blobstore", "fintraffic"})
@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
public class FintrafficMergingParkingImporterTest extends FintrafficIntegrationTest {

    @MockitoBean
    private AuthorizationService authorizationService;

    @Autowired
    private MergingParkingImporter mergingParkingImporter;

    @Autowired
    private ParkingVersionedSaverService parkingVersionedSaverService;

    @Autowired
    private ParkingRepository parkingRepository;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @After
    public void cleanUp() {
        parkingRepository.deleteAll();
        stopPlaceRepository.deleteAll();
    }

    @Test
    public void importerIsFintrafficSubtype() {
        assertThat(mergingParkingImporter)
                .as("fintraffic profile must activate FintrafficMergingParkingImporter via @Primary")
                .isInstanceOf(FintrafficMergingParkingImporter.class);
    }

    @Test
    @Transactional
    public void handleCompletelyNewParking_preservesPaymentMethods() throws Exception {
        StopPlace stopPlace = new StopPlace();
        stopPlaceRepository.save(stopPlace);

        FintrafficParking incoming = new FintrafficParking();
        incoming.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));
        incoming.setPaymentMethods(List.of(PaymentMethodEnumeration.CASH, PaymentMethodEnumeration.CREDIT_CARD));

        Parking saved = mergingParkingImporter.handleCompletelyNewParking(incoming);

        assertThat(saved).isInstanceOf(FintrafficParking.class);
        assertThat(((FintrafficParking) saved).getPaymentMethods())
                .as("paymentMethods must be preserved when importing a completely new parking")
                .containsExactlyInAnyOrder(PaymentMethodEnumeration.CASH, PaymentMethodEnumeration.CREDIT_CARD);
    }

    @Test
    @Transactional
    public void handleAlreadyExistingParking_mergesPaymentMethods() {
        StopPlace stopPlace = new StopPlace();
        stopPlaceRepository.save(stopPlace);

        // Existing parking with one payment method
        FintrafficParking existing = new FintrafficParking();
        existing.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));
        existing.setPaymentMethods(List.of(PaymentMethodEnumeration.CASH));
        existing = (FintrafficParking) parkingVersionedSaverService.saveNewVersion(existing);

        // Incoming parking with updated payment methods
        FintrafficParking incoming = new FintrafficParking();
        incoming.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));
        incoming.setPaymentMethods(List.of(PaymentMethodEnumeration.CREDIT_CARD, PaymentMethodEnumeration.DEBIT_CARD));

        Parking result = mergingParkingImporter.handleAlreadyExistingParking(existing, incoming);

        assertThat(result).isInstanceOf(FintrafficParking.class);
        assertThat(((FintrafficParking) result).getPaymentMethods())
                .as("paymentMethods from incoming parking must replace those on the version copy")
                .containsExactlyInAnyOrder(PaymentMethodEnumeration.CREDIT_CARD, PaymentMethodEnumeration.DEBIT_CARD);
    }

    @Test
    @Transactional
    public void handleAlreadyExistingParking_unchangedPaymentMethods_doesNotCreateNewVersion() {
        StopPlace stopPlace = new StopPlace();
        stopPlaceRepository.save(stopPlace);

        FintrafficParking existing = new FintrafficParking();
        existing.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));
        existing.setPaymentMethods(List.of(PaymentMethodEnumeration.CASH));
        existing = (FintrafficParking) parkingVersionedSaverService.saveNewVersion(existing);
        long existingVersion = existing.getVersion();

        FintrafficParking incoming = new FintrafficParking();
        incoming.setPaymentMethods(List.of(PaymentMethodEnumeration.CASH));

        Parking result = mergingParkingImporter.handleAlreadyExistingParking(existing, incoming);

        assertThat(result.getVersion())
                .as("no new version must be created when paymentMethods are unchanged")
                .isEqualTo(existingVersion);
    }

    @Test
    @Transactional
    public void handleCompletelyNewParking_withPlainParking_preservesPaymentMethods() throws Exception {
        StopPlace stopPlace = new StopPlace();
        stopPlaceRepository.save(stopPlace);

        // Simulate the NeTEx mapper: produces a plain Parking with the @Transient
        // paymentMethods field populated from the NeTEx document.
        Parking incoming = new Parking();
        incoming.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));
        incoming.getPaymentMethods().add(PaymentMethodEnumeration.CASH);

        Parking saved = mergingParkingImporter.handleCompletelyNewParking(incoming);

        assertThat(saved)
                .as("NeTEx-imported parking must be promoted to FintrafficParking")
                .isInstanceOf(FintrafficParking.class);
        assertThat(((FintrafficParking) saved).getPaymentMethods())
                .as("paymentMethods from plain NeTEx-derived Parking must be preserved")
                .containsExactlyInAnyOrder(PaymentMethodEnumeration.CASH);
    }

    @Test
    @Transactional
    public void handleAlreadyExistingParking_withPlainParking_mergesPaymentMethods() {
        StopPlace stopPlace = new StopPlace();
        stopPlaceRepository.save(stopPlace);

        FintrafficParking existing = new FintrafficParking();
        existing.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));
        existing.setPaymentMethods(List.of(PaymentMethodEnumeration.CASH));
        existing = (FintrafficParking) parkingVersionedSaverService.saveNewVersion(existing);

        // Simulate the NeTEx mapper: plain Parking with updated @Transient paymentMethods
        Parking incoming = new Parking();
        incoming.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));
        incoming.getPaymentMethods().add(PaymentMethodEnumeration.CREDIT_CARD);

        Parking result = mergingParkingImporter.handleAlreadyExistingParking(existing, incoming);

        assertThat(result).isInstanceOf(FintrafficParking.class);
        assertThat(((FintrafficParking) result).getPaymentMethods())
                .as("paymentMethods from plain NeTEx-derived Parking must overwrite existing")
                .containsExactlyInAnyOrder(PaymentMethodEnumeration.CREDIT_CARD);
    }

    @Test
    @Transactional
    public void handleCompletelyNewParking_preservesInfoLinks() throws Exception {
        StopPlace stopPlace = new StopPlace();
        stopPlaceRepository.save(stopPlace);

        FintrafficParking incoming = new FintrafficParking();
        incoming.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));
        incoming.setInfoLinks(List.of(
                new FintrafficInfoLink("https://example.com/parking", "resource")));

        Parking saved = mergingParkingImporter.handleCompletelyNewParking(incoming);

        assertThat(saved).isInstanceOf(FintrafficParking.class);
        assertThat(((FintrafficParking) saved).getInfoLinks())
                .as("infoLinks must be preserved when importing a completely new parking")
                .containsExactly(new FintrafficInfoLink("https://example.com/parking", "resource"));
    }

    @Test
    @Transactional
    public void handleAlreadyExistingParking_mergesInfoLinks() {
        StopPlace stopPlace = new StopPlace();
        stopPlaceRepository.save(stopPlace);

        FintrafficParking existing = new FintrafficParking();
        existing.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));
        existing.setInfoLinks(List.of(new FintrafficInfoLink("https://old.example.com", "info")));
        existing = (FintrafficParking) parkingVersionedSaverService.saveNewVersion(existing);

        FintrafficParking incoming = new FintrafficParking();
        incoming.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));
        incoming.setInfoLinks(List.of(new FintrafficInfoLink("https://new.example.com", "resource")));

        Parking result = mergingParkingImporter.handleAlreadyExistingParking(existing, incoming);

        assertThat(result).isInstanceOf(FintrafficParking.class);
        assertThat(((FintrafficParking) result).getInfoLinks())
                .as("infoLinks from incoming parking must replace those on the version copy")
                .containsExactly(new FintrafficInfoLink("https://new.example.com", "resource"));
    }

    @Test
    @Transactional
    public void handleAlreadyExistingParking_unchangedInfoLinks_doesNotCreateNewVersion() {
        StopPlace stopPlace = new StopPlace();
        stopPlaceRepository.save(stopPlace);

        FintrafficParking existing = new FintrafficParking();
        existing.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));
        existing.setInfoLinks(List.of(new FintrafficInfoLink("https://example.com", "resource")));
        existing = (FintrafficParking) parkingVersionedSaverService.saveNewVersion(existing);
        long existingVersion = existing.getVersion();

        FintrafficParking incoming = new FintrafficParking();
        incoming.setInfoLinks(List.of(new FintrafficInfoLink("https://example.com", "resource")));

        Parking result = mergingParkingImporter.handleAlreadyExistingParking(existing, incoming);

        assertThat(result.getVersion())
                .as("no new version must be created when infoLinks are unchanged")
                .isEqualTo(existingVersion);
    }

    @Test
    @Transactional
    public void handleCompletelyNewParking_preservesVehicleEntrances() throws Exception {
        StopPlace stopPlace = new StopPlace();
        stopPlaceRepository.save(stopPlace);

        FintrafficParkingEntranceForVehicles entrance = new FintrafficParkingEntranceForVehicles(
                "Main", "door", null, null, true, false, "A1");
        FintrafficParking incoming = new FintrafficParking();
        incoming.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));
        incoming.setFintrafficVehicleEntrances(List.of(entrance));

        Parking saved = mergingParkingImporter.handleCompletelyNewParking(incoming);

        assertThat(saved).isInstanceOf(FintrafficParking.class);
        assertThat(((FintrafficParking) saved).getFintrafficVehicleEntrances())
                .as("vehicleEntrances must be preserved when importing a completely new parking")
                .containsExactly(entrance);
    }

    @Test
    @Transactional
    public void handleAlreadyExistingParking_mergesVehicleEntrances() {
        StopPlace stopPlace = new StopPlace();
        stopPlaceRepository.save(stopPlace);

        FintrafficParking existing = new FintrafficParking();
        existing.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));
        existing.setFintrafficVehicleEntrances(List.of(
                new FintrafficParkingEntranceForVehicles("Old", "gate", null, null, true, true, null)));
        existing = (FintrafficParking) parkingVersionedSaverService.saveNewVersion(existing);

        FintrafficParkingEntranceForVehicles newEntrance =
                new FintrafficParkingEntranceForVehicles("New", "door", null, null, true, false, "B2");
        FintrafficParking incoming = new FintrafficParking();
        incoming.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));
        incoming.setFintrafficVehicleEntrances(List.of(newEntrance));

        Parking result = mergingParkingImporter.handleAlreadyExistingParking(existing, incoming);

        assertThat(result).isInstanceOf(FintrafficParking.class);
        assertThat(((FintrafficParking) result).getFintrafficVehicleEntrances())
                .as("vehicleEntrances from incoming parking must replace those on the version copy")
                .containsExactly(newEntrance);
    }

    @Test
    @Transactional
    public void handleAlreadyExistingParking_unchangedVehicleEntrances_doesNotCreateNewVersion() {
        StopPlace stopPlace = new StopPlace();
        stopPlaceRepository.save(stopPlace);

        FintrafficParkingEntranceForVehicles entrance =
                new FintrafficParkingEntranceForVehicles("Main", "door", null, null, true, false, "A1");
        FintrafficParking existing = new FintrafficParking();
        existing.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));
        existing.setFintrafficVehicleEntrances(List.of(entrance));
        existing = (FintrafficParking) parkingVersionedSaverService.saveNewVersion(existing);
        long existingVersion = existing.getVersion();

        FintrafficParking incoming = new FintrafficParking();
        incoming.setFintrafficVehicleEntrances(List.of(entrance));

        Parking result = mergingParkingImporter.handleAlreadyExistingParking(existing, incoming);

        assertThat(result.getVersion())
                .as("no new version must be created when vehicleEntrances are unchanged")
                .isEqualTo(existingVersion);
    }

    @Test
    @Transactional
    public void handleAlreadyExistingParking_mergesAvailabilityConditions() {
        StopPlace stopPlace = new StopPlace();
        stopPlaceRepository.save(stopPlace);

        FintrafficParking existing = new FintrafficParking();
        existing.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));
        existing.setAvailabilityConditions(List.of(
                new FintrafficParkingAvailabilityCondition("FSR:DayType:BusinessDay", true, LocalTime.of(8, 0), LocalTime.of(18, 0))));
        existing = (FintrafficParking) parkingVersionedSaverService.saveNewVersion(existing);

        FintrafficParking incoming = new FintrafficParking();
        incoming.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));
        incoming.setAvailabilityConditions(List.of(
                new FintrafficParkingAvailabilityCondition("FSR:DayType:Sunday", false, null, null)));

        Parking result = mergingParkingImporter.handleAlreadyExistingParking(existing, incoming);

        assertThat(result).isInstanceOf(FintrafficParking.class);
        assertThat(((FintrafficParking) result).getAvailabilityConditions())
                .as("availabilityConditions from incoming parking must replace those on the version copy")
                .containsExactly(new FintrafficParkingAvailabilityCondition("FSR:DayType:Sunday", false, null, null));
    }

    @Test
    @Transactional
    public void handleAlreadyExistingParking_unchangedAvailabilityConditions_doesNotCreateNewVersion() {
        StopPlace stopPlace = new StopPlace();
        stopPlaceRepository.save(stopPlace);

        FintrafficParkingAvailabilityCondition condition =
                new FintrafficParkingAvailabilityCondition("FSR:DayType:BusinessDay", true, LocalTime.of(6, 0), LocalTime.of(22, 0));
        FintrafficParking existing = new FintrafficParking();
        existing.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));
        existing.setAvailabilityConditions(List.of(condition));
        existing = (FintrafficParking) parkingVersionedSaverService.saveNewVersion(existing);
        long existingVersion = existing.getVersion();

        FintrafficParking incoming = new FintrafficParking();
        incoming.setAvailabilityConditions(List.of(condition));

        Parking result = mergingParkingImporter.handleAlreadyExistingParking(existing, incoming);

        assertThat(result.getVersion())
                .as("no new version must be created when availabilityConditions are unchanged")
                .isEqualTo(existingVersion);
    }
}
