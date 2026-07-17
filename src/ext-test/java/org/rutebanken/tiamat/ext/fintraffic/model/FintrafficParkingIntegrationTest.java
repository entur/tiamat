package org.rutebanken.tiamat.ext.fintraffic.model;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.ext.fintraffic.FintrafficIntegrationTest;
import org.rutebanken.tiamat.ext.fintraffic.FintrafficTiamatTestApplication;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.PaymentMethodEnumeration;
import org.rutebanken.tiamat.model.factory.ParkingEntityFactory;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.ParkingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test verifying that {@link FintrafficParking} persists {@code paymentMethods}
 * through the full NeTEx → Orika → JPA → DB → JPA → NeTEx round-trip.
 * <p>
 * The {@code fintraffic} profile activates {@link FintrafficParkingEntityFactory} (which removes
 * the Orika exclusion for {@code paymentMethods}) and the {@link FintrafficParking} entity subclass.
 * {@link org.rutebanken.tiamat.ext.fintraffic.auth.FintrafficSecurityConfig} is excluded from the
 * component scan via {@link FintrafficTiamatTestApplication}; {@link AuthorizationService} is mocked.
 * <p>
 * Persistence tests use {@link ParkingRepository} directly to avoid the {@code parentSiteRef}
 * validation in {@code ParkingVersionedSaverService}, since the round-trip under test is
 * specifically the {@code paymentMethods} field persistence, not the full import pipeline.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = FintrafficTiamatTestApplication.class
)
@ActiveProfiles({"test", "gcs-blobstore", "fintraffic"})
@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
public class FintrafficParkingIntegrationTest extends FintrafficIntegrationTest {

    @MockitoBean
    private AuthorizationService authorizationService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private NetexMapper netexMapper;

    @Autowired
    private ParkingEntityFactory parkingEntityFactory;

    @Autowired
    private ParkingRepository parkingRepository;

    @After
    public void cleanUp() {
        parkingRepository.deleteAll();
    }

    @Test
    public void parkingEntityFactory_producesFintrafficParkingInstance() {
        Parking parking = netexMapper.mapToTiamatModel(
                new org.rutebanken.netex.model.Parking().withId("FSR:Parking:1").withVersion("1"));

        assertThat(parking)
                .as("factory must produce FintrafficParking when fintraffic profile is active")
                .isInstanceOf(FintrafficParking.class);
    }

    @Test
    @Transactional
    public void paymentMethods_surviveMappingAndDatabaseRoundTrip() {
        // Map NeTEx → Tiamat model with payment methods
        org.rutebanken.netex.model.Parking netexParking = new org.rutebanken.netex.model.Parking()
                .withId("FSR:Parking:42")
                .withVersion("1")
                .withPaymentMethods(
                        org.rutebanken.netex.model.PaymentMethodEnumeration.CASH,
                        org.rutebanken.netex.model.PaymentMethodEnumeration.CREDIT_CARD);

        Parking tiamatParking = netexMapper.mapToTiamatModel(netexParking);

        assertThat(tiamatParking.getPaymentMethods())
                .as("paymentMethods must be mapped from NeTEx (not excluded) with fintraffic profile")
                .containsExactlyInAnyOrder(
                        PaymentMethodEnumeration.CASH,
                        PaymentMethodEnumeration.CREDIT_CARD);

        // Save directly via repository (no parentSiteRef validation)
        Parking saved = parkingRepository.save(tiamatParking);
        Long id = saved.getId();

        // Flush and clear to evict first-level cache; forces genuine DB read
        entityManager.flush();
        entityManager.clear();

        // Reload from DB
        Parking reloaded = parkingRepository.findById(id).orElseThrow();

        assertThat(reloaded.getPaymentMethods())
                .as("paymentMethods must survive DB round-trip")
                .containsExactlyInAnyOrder(
                        PaymentMethodEnumeration.CASH,
                        PaymentMethodEnumeration.CREDIT_CARD);
    }

    @Test
    @Transactional
    public void paymentMethods_appearsInNetexExport() {
        // Build and persist a FintrafficParking with payment methods
        FintrafficParking fp = (FintrafficParking) parkingEntityFactory.create();
        fp.setPaymentMethods(List.of(PaymentMethodEnumeration.CASH));
        Parking saved = parkingRepository.save(fp);

        // Flush and clear to evict first-level cache; forces genuine DB read
        entityManager.flush();
        entityManager.clear();

        // Reload (ensure we're reading from DB, not session cache)
        Parking reloaded = parkingRepository.findById(saved.getId()).orElseThrow();

        // Map Tiamat → NeTEx
        org.rutebanken.netex.model.Parking exported = netexMapper.mapToNetexModel(reloaded);

        assertThat(exported.getPaymentMethods())
                .as("paymentMethods must appear in NeTEx export")
                .contains(org.rutebanken.netex.model.PaymentMethodEnumeration.CASH);
    }
}
