package org.rutebanken.tiamat.ext.fintraffic.importer;

import org.rutebanken.tiamat.ext.fintraffic.model.FintrafficInfoLink;
import org.rutebanken.tiamat.ext.fintraffic.model.FintrafficParking;
import org.rutebanken.tiamat.importer.KeyValueListAppender;
import org.rutebanken.tiamat.importer.finder.NearbyParkingFinder;
import org.rutebanken.tiamat.importer.finder.ParkingFromOriginalIdFinder;
import org.rutebanken.tiamat.importer.merging.MergingParkingImporter;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.PaymentMethodEnumeration;
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
 * {@link FintrafficParking#getPaymentMethods() paymentMethods} and
 * {@link FintrafficParking#getInfoLinks() infoLinks} on both import paths.
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

        boolean changed = false;

        // paymentMethods — always available via Parking.getPaymentMethods() (transient field)
        List<PaymentMethodEnumeration> incomingMethods = incomingParking.getPaymentMethods();
        List<PaymentMethodEnumeration> existingMethods = target.getPaymentMethods();
        if (!incomingMethods.equals(existingMethods)) {
            target.setPaymentMethods(new ArrayList<>(incomingMethods));
            changed = true;
        }

        // infoLinks — only available when incomingParking is also a FintrafficParking
        if (incomingParking instanceof FintrafficParking incomingFP) {
            List<FintrafficInfoLink> incomingLinks = incomingFP.getInfoLinks();
            List<FintrafficInfoLink> existingLinks = target.getInfoLinks();
            if (!incomingLinks.equals(existingLinks)) {
                target.setInfoLinks(new ArrayList<>(incomingLinks));
                changed = true;
            }
        }

        return changed;
    }
}
