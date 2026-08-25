package org.rutebanken.tiamat.ext.fintraffic.importer;

import org.rutebanken.tiamat.ext.fintraffic.model.FintrafficParking;
import org.rutebanken.tiamat.importer.KeyValueListAppender;
import org.rutebanken.tiamat.importer.finder.NearbyParkingFinder;
import org.rutebanken.tiamat.importer.finder.ParkingFromOriginalIdFinder;
import org.rutebanken.tiamat.importer.merging.MergingParkingImporter;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.factory.ParkingEntityFactory;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.reference.ReferenceResolver;
import org.rutebanken.tiamat.versioning.VersionCreator;
import org.rutebanken.tiamat.versioning.save.ParkingVersionedSaverService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Fintraffic extension of {@link MergingParkingImporter} that preserves
 * {@link FintrafficParking#getPaymentMethods() paymentMethods} on both import paths:
 * <ul>
 *   <li>New parking — {@code handleCompletelyNewParking} creates a typed
 *       {@link FintrafficParking} copy and calls {@link #mergeExtendedFields} to copy
 *       the transient {@code paymentMethods} from the NeTEx-derived source into the
 *       persisted field of the copy.</li>
 *   <li>Existing parking — {@code handleAlreadyExistingParking} does the same via
 *       its own {@link #mergeExtendedFields} call.</li>
 * </ul>
 * In both cases {@code incomingParking} may be a plain {@link org.rutebanken.tiamat.model.Parking}
 * (as produced by the NeTEx mapper) or a {@link FintrafficParking}; only the copy passed
 * as the second argument is required to be a {@link FintrafficParking}.
 */
@Profile("fintraffic")
@Primary
@Component
@Qualifier("mergingParkingImporter")
public class FintrafficMergingParkingImporter extends MergingParkingImporter {

    public FintrafficMergingParkingImporter(ParkingFromOriginalIdFinder parkingFromOriginalIdFinder,
                                            NearbyParkingFinder nearbyParkingFinder,
                                            ReferenceResolver referenceResolver,
                                            KeyValueListAppender keyValueListAppender,
                                            NetexMapper netexMapper,
                                            ParkingVersionedSaverService parkingVersionedSaverService,
                                            VersionCreator versionCreator,
                                            ParkingEntityFactory parkingEntityFactory) {
        super(parkingFromOriginalIdFinder, nearbyParkingFinder, referenceResolver,
                keyValueListAppender, netexMapper, parkingVersionedSaverService,
                versionCreator, parkingEntityFactory);
    }

    @Override
    protected boolean mergeExtendedFields(Parking incomingParking, Parking copy) {
        if (!(copy instanceof FintrafficParking target)) {
            return false;
        }

        // incomingParking may be a plain Parking (from NeTEx mapper) or FintrafficParking (from DB).
        // Either way, Parking.getPaymentMethods() returns the in-memory list the NeTEx mapper set.
        List<org.rutebanken.tiamat.model.PaymentMethodEnumeration> incomingMethods = incomingParking.getPaymentMethods();
        List<org.rutebanken.tiamat.model.PaymentMethodEnumeration> existingMethods = target.getPaymentMethods();

        if (incomingMethods.equals(existingMethods)) {
            return false;
        }

        target.setPaymentMethods(new ArrayList<>(incomingMethods));
        return true;
    }
}
