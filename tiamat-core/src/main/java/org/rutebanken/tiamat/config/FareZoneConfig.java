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

package org.rutebanken.tiamat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FareZoneConfig {

    @Value("${fareZone.externalVersioning:false}")
    private boolean externalVersioning;

    @Value("${fareZone.register.import.enabled:false}")
    private boolean registerImportEnabled;

    public boolean isExternalVersioning() {
        return externalVersioning;
    }

    public boolean isRegisterImportEnabled() {
        return registerImportEnabled;
    }

    /**
     * Whether fare zones are managed by an external source, so the import runs as a replica: update by
     * netexId and prune zones absent from the delivery. True for the legacy distance-api/papsukkal POST
     * (externalVersioning) and for the farezone-resolver register import - separate sources, same
     * replica semantics.
     */
    public boolean isReplicaImport() {
        return externalVersioning || registerImportEnabled;
    }
}