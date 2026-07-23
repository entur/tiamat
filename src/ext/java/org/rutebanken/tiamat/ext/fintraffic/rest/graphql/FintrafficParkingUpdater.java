package org.rutebanken.tiamat.ext.fintraffic.rest.graphql;

import graphql.schema.DataFetchingEnvironment;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.rutebanken.tiamat.ext.fintraffic.model.FintrafficParking;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.PaymentMethodEnumeration;
import org.rutebanken.tiamat.rest.graphql.fetchers.ParkingUpdater;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.rutebanken.tiamat.ext.fintraffic.rest.graphql.FintrafficParkingGraphQLTypeContributor.PAYMENT_METHODS;

/**
 * Fintraffic extension of {@link ParkingUpdater} that handles the
 * {@code paymentMethods} input field contributed by
 * {@link FintrafficParkingGraphQLTypeContributor}.
 */
@Profile("fintraffic")
@Transactional
public class FintrafficParkingUpdater extends ParkingUpdater {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Extends the parent's {@code get()} to flush pending collection inserts and
     * refresh entities before the transaction closes.  This ensures that
     * {@code @ElementCollection} fields (e.g. {@code paymentMethods}) on newly
     * persisted {@link FintrafficParking} instances are fully loaded from the
     * database and not left in Hibernate's "pending-insert" state, which would
     * cause them to appear empty when the GraphQL response is serialised.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object get(DataFetchingEnvironment environment) {
        List<Parking> parkings = (List<Parking>) super.get(environment);
        if (parkings != null) {
            entityManager.flush();
            parkings.stream()
                    .filter(Objects::nonNull)
                    .filter(entityManager::contains)
                    .forEach(entityManager::refresh);
        }
        return parkings;
    }

    @Override
    protected boolean populateExtendedFields(Map input, Parking parking) {
        if (!(parking instanceof FintrafficParking target)) {
            return false;
        }

        @SuppressWarnings("unchecked")
        List<PaymentMethodEnumeration> incoming = (List<PaymentMethodEnumeration>) input.get(PAYMENT_METHODS);
        if (incoming == null) {
            return false;
        }

        if (incoming.equals(target.getPaymentMethods())) {
            return false;
        }

        target.setPaymentMethods(List.copyOf(incoming));
        return true;
    }

    /**
     * Copies extended fields that Orika does not transfer from the existing version into
     * the newly created version copy.  Called by the parent's update path immediately
     * after {@link org.rutebanken.tiamat.versioning.VersionCreator#createCopy}, before
     * {@link #populateExtendedFields} overwrites them with the GraphQL input values.
     * <p>
     * This ensures that fields omitted from the update mutation (e.g. the caller did not
     * include {@code paymentMethods} in the input) retain their current values rather than
     * being silently reset to the default.
     */
    @Override
    protected void preserveExtendedFields(Parking existingVersion, Parking copy) {
        if (existingVersion instanceof FintrafficParking source && copy instanceof FintrafficParking target) {
            target.setPaymentMethods(new ArrayList<>(source.getPaymentMethods()));
        }
    }
}
