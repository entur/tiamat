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
 * Names the JWT claims this deployment's {@link AuthorizationService} derives authority from.
 * <p>
 * Authorization normally reads the token held by the current request. Work that outlives its
 * request has no such token and must reconstruct the principal from claims kept earlier, which
 * means knowing which claims to keep. Naming them here rather than at each such call site keeps the
 * answer with the service the answer belongs to.
 * <p>
 * Substituted the same way as {@link AuthorizationService}: declare a bean of this type and the
 * default in {@code AuthorizationServiceConfig} backs off.
 */
public interface AuthorizationClaims {

    /**
     * Standard identity claims — {@code sub}, {@code iss}, {@code exp} — confer no authority and are
     * a caller's own concern, so they do not belong here.
     *
     * @return the claim names, which may be empty. Empty is a real answer, not an omission: an
     *         authorization service that resolves roles from the subject alone, by looking them up
     *         rather than reading them out of the token, needs no claim of its own kept.
     */
    List<String> claimNames();
}
