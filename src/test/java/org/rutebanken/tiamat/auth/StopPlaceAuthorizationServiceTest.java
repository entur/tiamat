package org.rutebanken.tiamat.auth;

import com.vividsolutions.jts.geom.*;
import org.junit.Assert;
import org.junit.Test;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;

import java.util.Arrays;
import java.util.HashMap;

import static org.rutebanken.tiamat.auth.GenericAuthorizationService.ENTITY_CLASSIFIER_ALL_TYPES;

public class StopPlaceAuthorizationServiceTest {

	private StopPlaceAuthorizationService stopPlaceAuthorizationService = new StopPlaceAuthorizationService();

	@Test
	public void matchesOrgAndExactEntityClassification() {
		StopPlace stopPlace = stopPlace(StopTypeEnumeration.COACH_STATION);
		RoleAssignment roleAssignment = new RoleAssignment();
		roleAssignment.e = new HashMap<>();
		roleAssignment.e.put("StopPlace", Arrays.asList("other", StopTypeEnumeration.COACH_STATION.value()));
		AuthorizationTask<StopPlace> task = new AuthorizationTask(stopPlace, roleAssignment, null);

		Assert.assertTrue(stopPlaceAuthorizationService.isAllowed(task));
	}

	@Test
	public void matchesOrgAndWildcardEntityClassification() {
		StopPlace stopPlace = stopPlace(StopTypeEnumeration.COACH_STATION);
		RoleAssignment roleAssignment = new RoleAssignment();
		roleAssignment.e = new HashMap<>();
		roleAssignment.e.put("StopPlace", Arrays.asList("other", ENTITY_CLASSIFIER_ALL_TYPES));
		AuthorizationTask<StopPlace> task = new AuthorizationTask(stopPlace, roleAssignment, null);

		Assert.assertTrue(stopPlaceAuthorizationService.isAllowed(task));
	}

	@Test
	public void noMatchingClassifiers() {
		StopPlace stopPlace = stopPlace(StopTypeEnumeration.COACH_STATION);
		RoleAssignment roleAssignment = new RoleAssignment();
		roleAssignment.e = new HashMap<>();
		roleAssignment.e.put("OtherType", Arrays.asList(ENTITY_CLASSIFIER_ALL_TYPES, StopTypeEnumeration.COACH_STATION.value()));
		roleAssignment.e.put("StopPlace", Arrays.asList("wrongStopType"));
		AuthorizationTask<StopPlace> task = new AuthorizationTask(stopPlace, roleAssignment, null);

		Assert.assertFalse(stopPlaceAuthorizationService.isAllowed(task));
	}

	@Test
	public void insideAdministrativeZone() {
		Polygon administrativeZone = polygon();

		StopPlace stopPlace = stopPlace(StopTypeEnumeration.COACH_STATION);
		stopPlace.setCentroid(pointInsidePolygon());

		RoleAssignment roleAssignment = new RoleAssignment();
		roleAssignment.e = new HashMap<>();
		roleAssignment.e.put("StopPlace", Arrays.asList(ENTITY_CLASSIFIER_ALL_TYPES));

		AuthorizationTask<StopPlace> task = new AuthorizationTask(stopPlace, roleAssignment, administrativeZone);

		Assert.assertTrue(stopPlaceAuthorizationService.isAllowed(task));
	}

	@Test
	public void outsideAdministrativeZone() {
		Polygon administrativeZone = polygon();

		StopPlace stopPlace = stopPlace(StopTypeEnumeration.COACH_STATION);
		stopPlace.setCentroid(pointOutsidePolygon());

		RoleAssignment roleAssignment = new RoleAssignment();
		roleAssignment.e = new HashMap<>();
		roleAssignment.e.put("StopPlace", Arrays.asList("other", ENTITY_CLASSIFIER_ALL_TYPES));
		AuthorizationTask<StopPlace> task = new AuthorizationTask(stopPlace, roleAssignment, administrativeZone);

		Assert.assertFalse(stopPlaceAuthorizationService.isAllowed(task));
	}

	private Polygon polygon() {
		GeometryFactory fact = new GeometryFactory();
		LinearRing linear = new GeometryFactory().createLinearRing(new Coordinate[]{new Coordinate(0, 0), new Coordinate(1, 0), new Coordinate(1, 1), new Coordinate(0, 0)});
		return new Polygon(linear, null, fact);
	}

	private Point pointInsidePolygon() {
		return polygon().getCentroid();
	}

	private Point pointOutsidePolygon() {
		GeometryFactory fact = new GeometryFactory();
		return fact.createPoint(new Coordinate(100, 100));
	}

	private StopPlace stopPlace(StopTypeEnumeration type) {
		StopPlace stopPlace = new StopPlace();
		stopPlace.setStopPlaceType(type);
		return stopPlace;
	}


}
