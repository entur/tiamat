package org.rutebanken.tiamat.ext.fintraffic.importer;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.ext.fintraffic.FintrafficTiamatTestApplication;
import org.rutebanken.tiamat.ext.fintraffic.model.FintrafficParking;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

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
public class FintrafficMergingParkingImporterTest {

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
}
