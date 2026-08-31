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

package org.rutebanken.tiamat.rest.graphql.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.helper.organisation.user.UserInfoExtractor;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.model.authorization.UserPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class UserPermissionsFetcher implements DataFetcher {

    private static final Logger logger = LoggerFactory.getLogger(UserPermissionsFetcher.class);


    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private UserInfoExtractor userInfoExtractor;

    @Override
    public Object get(DataFetchingEnvironment dataFetchingEnvironment) {
        final boolean isGuest = authorizationService.isGuest();
        final boolean allowNewStopEverywhere = authorizationService.canEditAllEntities();
        final String preferredName = isGuest ? null : userInfoExtractor.getPreferredName();

        logger.debug("isGuest: {}, allowNewStopEverywhere: {}, preferredName: {}" , allowNewStopEverywhere, isGuest, preferredName);

        return new UserPermissions(isGuest, allowNewStopEverywhere, preferredName);
    }
}
