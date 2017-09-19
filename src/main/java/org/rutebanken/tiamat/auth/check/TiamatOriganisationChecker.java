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


import org.rutebanken.helper.organisation.OrganisationChecker;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.tiamat.model.Site_VersionStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TiamatOriganisationChecker implements OrganisationChecker {

    private static final Logger logger = LoggerFactory.getLogger(TiamatOriganisationChecker.class);

    @Override
    public boolean entityMatchesOrganisationRef(RoleAssignment roleAssignment, Object entity) {

        if (entity instanceof Site_VersionStructure) {

            Site_VersionStructure site = (Site_VersionStructure) entity;

            if (site.getOrganisationRef() != null) {
                String orgRef = site.getOrganisationRef().getValue().getRef();
                if (orgRef != null) {
                    logger.debug("Found org ref {} for entity. Returning true if it matches role assignment organisation :{}", orgRef, roleAssignment.getOrganisation());
                    return orgRef.endsWith(":" + roleAssignment.getOrganisation());
                }
            }
            logger.debug("Org ref is null for entity: {}", entity);
            return true;
        } else {
            logger.warn("Cannot check for organisation for entity {}", entity);
            return true;
        }
    }
}
