package org.rutebanken.tiamat.auth;

import org.rutebanken.tiamat.model.StopPlace;
import org.springframework.stereotype.Service;


@Service
public class StopPlaceAuthorizationService extends GenericAuthorizationService<StopPlace> {

	@Override
	protected boolean isMatchForExplicitClassifier(AuthorizationTask<StopPlace> task, String classifier) {
		return classifier.equals(task.getEntity().getStopPlaceType().value());
	}

	@Override
	protected boolean matchesAdministrativeZone(AuthorizationTask<StopPlace> task) {
		if (task.getAdministrativeZone() != null) {
			return task.getAdministrativeZone().contains(task.getEntity().getCentroid());
		}
		return true;
	}

	@Override
	protected boolean matchesOrganisation(AuthorizationTask<StopPlace> task) {
		if (task.getEntity().getOrganisationRef() != null) {
			String orgRef = task.getEntity().getOrganisationRef().getValue().getRef();
			if (orgRef != null) {
				return orgRef.endsWith(":" + task.getRoleAssignment().o);
			}
		}
		return true;
	}
}
