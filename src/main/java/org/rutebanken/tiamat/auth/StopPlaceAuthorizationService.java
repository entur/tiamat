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

import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.tiamat.model.StopPlace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * This authorization service is implemented for handling multi modal stops.
 */
@Service
public class StopPlaceAuthorizationService {

    private final ReflectionAuthorizationService authorizationService;

    @Autowired
    public StopPlaceAuthorizationService(ReflectionAuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    public void assertAuthorized(String requiredRole, StopPlace stopPlace) {
        authorizationService.assertAuthorized(requiredRole, Arrays.asList(stopPlace));
    }
}
