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

package org.rutebanken.tiamat.auth;

import java.util.List;

/**
 * The claims {@link DefaultAuthorizationService} reads, via either role assignment extractor.
 * <p>
 * Under the jwt extractor the roles travel in the token itself, in {@code role_assignments} and
 * falling back to {@code permissions}. Under the permission store they are looked up from the
 * identity instead, for which the organisation id is validated against the stored client for
 * machine callers. The preferred username is what the audit trail records as {@code changedBy}.
 * <p>
 * These names are duplicated rather than imported because the libraries that read them do not
 * expose them: the permission store declares its own package private, and the oauth2 helper dropped
 * its constants class after 5.x. Verified against the versions actually resolved,
 * permission-store-proxy and oauth2 7.2.0; worth rechecking on a major upgrade of either.
 */
public class DefaultAuthorizationClaims implements AuthorizationClaims {

    private static final String CLAIM_ORGANISATION_ID = "https://entur.io/organisationID";
    private static final String CLAIM_ROLE_ASSIGNMENTS = "role_assignments";
    private static final String CLAIM_PERMISSIONS = "permissions";
    private static final String CLAIM_PREFERRED_USERNAME = "preferred_username";

    @Override
    public List<String> claimNames() {
        return List.of(
                CLAIM_ROLE_ASSIGNMENTS,
                CLAIM_PERMISSIONS,
                CLAIM_ORGANISATION_ID,
                CLAIM_PREFERRED_USERNAME
        );
    }
}
