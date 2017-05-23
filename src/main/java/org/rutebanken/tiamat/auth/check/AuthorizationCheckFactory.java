package org.rutebanken.tiamat.auth.check;

import com.vividsolutions.jts.geom.Polygon;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationCheckFactory {

	@Autowired
	private StopPlaceRepository stopPlaceRepository;

	public <T extends EntityStructure> AuthorizationCheck buildCheck(T entity, RoleAssignment roleAssignment, Polygon administrativeZone) {
		if (entity instanceof StopPlace) {
			return new StopPlaceAuthorizationCheck((StopPlace) entity, roleAssignment, administrativeZone);
		}
		if (entity instanceof Quay) {
			StopPlace stopPlace = stopPlaceRepository.findByQuay((Quay) entity);
			if (stopPlace != null) {
				return new StopPlaceAuthorizationCheck(stopPlace, roleAssignment, administrativeZone);
			}
		}
		if (entity instanceof Parking) {
			Parking parking = (Parking) entity;
			if (parking.getParentSiteRef() != null && parking.getParentSiteRef().getRef() != null) {
					StopPlace stopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(parking.getParentSiteRef().getRef());
					return new StopPlaceAuthorizationCheck(stopPlace, roleAssignment, administrativeZone);
			}
			return new ParkingAuthorizationCheck(parking, roleAssignment, administrativeZone);
		}
		if (entity instanceof Place) {
			return new PlaceAuthorizationCheck<>((Place) entity, roleAssignment, administrativeZone);
		}
		return new AuthorizationCheck<>(entity, roleAssignment, administrativeZone);
	}

}
