/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.auth.check;

import com.vividsolutions.jts.geom.*;
import org.junit.Assert;
import org.junit.Test;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;

import java.util.Arrays;
import java.util.HashMap;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ENTITY_CLASSIFIER_ALL_TYPES;

public class StopPlaceAuthorizationCheckTest {

	private static final String ENTITY_TYPE_STOP_PLACE = "StopPlace";

	@Test
	public void matchesOrgAndExactEntityClassification() {
		StopPlace stopPlace = stopPlace(StopTypeEnumeration.COACH_STATION);
		RoleAssignment roleAssignment = new RoleAssignment();
		roleAssignment.e = new HashMap<>();
		roleAssignment.e.put(ENTITY_TYPE_STOP_PLACE, Arrays.asList("other", StopTypeEnumeration.COACH_STATION.value()));
		StopPlaceAuthorizationCheck check = new StopPlaceAuthorizationCheck(stopPlace, roleAssignment, null);

		Assert.assertTrue(check.isAllowed());
	}

	@Test
	public void matchesOrgAndWildcardEntityClassification() {
		StopPlace stopPlace = stopPlace(StopTypeEnumeration.COACH_STATION);
		RoleAssignment roleAssignment = new RoleAssignment();
		roleAssignment.e = new HashMap<>();
		roleAssignment.e.put(ENTITY_TYPE_STOP_PLACE, Arrays.asList("other", ENTITY_CLASSIFIER_ALL_TYPES));
		StopPlaceAuthorizationCheck check = new StopPlaceAuthorizationCheck(stopPlace, roleAssignment, null);

		Assert.assertTrue(check.isAllowed());
	}

	@Test
	public void noMatchingClassifiers() {
		StopPlace stopPlace = stopPlace(StopTypeEnumeration.COACH_STATION);
		RoleAssignment roleAssignment = new RoleAssignment();
		roleAssignment.e = new HashMap<>();
		roleAssignment.e.put("OtherType", Arrays.asList(ENTITY_CLASSIFIER_ALL_TYPES, StopTypeEnumeration.COACH_STATION.value()));
		roleAssignment.e.put(ENTITY_TYPE_STOP_PLACE, Arrays.asList("wrongStopType"));
		StopPlaceAuthorizationCheck check = new StopPlaceAuthorizationCheck(stopPlace, roleAssignment, null);

		Assert.assertFalse(check.isAllowed());
	}

	@Test
	public void insideAdministrativeZone() {
		Polygon administrativeZone = polygon();

		StopPlace stopPlace = stopPlace(StopTypeEnumeration.COACH_STATION);
		stopPlace.setCentroid(pointInsidePolygon());

		RoleAssignment roleAssignment = new RoleAssignment();
		roleAssignment.e = new HashMap<>();
		roleAssignment.e.put(ENTITY_TYPE_STOP_PLACE, Arrays.asList(ENTITY_CLASSIFIER_ALL_TYPES));

		StopPlaceAuthorizationCheck check = new StopPlaceAuthorizationCheck(stopPlace, roleAssignment, administrativeZone);

		Assert.assertTrue(check.isAllowed());
	}

	@Test
	public void outsideAdministrativeZone() {
		Polygon administrativeZone = polygon();

		StopPlace stopPlace = stopPlace(StopTypeEnumeration.COACH_STATION);
		stopPlace.setCentroid(pointOutsidePolygon());

		RoleAssignment roleAssignment = new RoleAssignment();
		roleAssignment.e = new HashMap<>();
		roleAssignment.e.put(ENTITY_TYPE_STOP_PLACE, Arrays.asList("other", ENTITY_CLASSIFIER_ALL_TYPES));
		StopPlaceAuthorizationCheck check = new StopPlaceAuthorizationCheck(stopPlace, roleAssignment, administrativeZone);

		Assert.assertFalse(check.isAllowed());
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
