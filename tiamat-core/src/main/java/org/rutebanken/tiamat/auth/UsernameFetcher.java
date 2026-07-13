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


import org.rutebanken.helper.organisation.user.UserInfoExtractor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;

@Service
public class UsernameFetcher {

    private final UserInfoExtractor userInfoExtractor;

    public UsernameFetcher(UserInfoExtractor userInfoExtractor) {
        this.userInfoExtractor = userInfoExtractor;
    }

    /**
     * Return the preferred username or null if the user is not authenticated.
     */
    @Nullable
    public String getUserNameForAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null
                || authentication.getPrincipal() == null
                || !(authentication.getPrincipal() instanceof Jwt)) {
            return null;
        }
        return userInfoExtractor.getPreferredName();
    }
}
