package org.rutebanken.tiamat.auth;

import org.rutebanken.helper.organisation.EntityResolver;
import org.rutebanken.tiamat.auth.check.ParkingAuthorizationCheck;
import org.rutebanken.tiamat.auth.check.PlaceAuthorizationCheck;
import org.rutebanken.tiamat.auth.check.StopPlaceAuthorizationCheck;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.Place;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TiamatEntityResolver implements EntityResolver {

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Override
    public Object resolveCorrectEntity(Object entity) {

        if(entity == null) {
            return null;
        }

        if(entity instanceof Quay) {
            StopPlace stopPlace = stopPlaceRepository.findByQuay((Quay) entity);
            if(stopPlace == null) {
                throw new IllegalArgumentException("Cannot resolve stop place from quay: " + entity);
            }
            return stopPlace;
        }

        if(entity instanceof Parking) {
            Parking parking = (Parking) entity;
            if (parking.getParentSiteRef() != null && parking.getParentSiteRef().getRef() != null) {
                StopPlace stopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(parking.getParentSiteRef().getRef());

                if(stopPlace == null) {
                    throw new IllegalArgumentException("Cannot resolve stop place from parking: " + entity);
                }
                return stopPlace;
            }
        }


        return entity;

    }
}
