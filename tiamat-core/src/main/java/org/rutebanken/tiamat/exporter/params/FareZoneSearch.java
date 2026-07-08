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

package org.rutebanken.tiamat.exporter.params;

import com.google.common.base.MoreObjects;
import jakarta.ws.rs.QueryParam;
import org.rutebanken.tiamat.model.ScopingMethodEnumeration;
import org.rutebanken.tiamat.model.ZoneTopologyEnumeration;

public class FareZoneSearch implements SearchObject {

    @QueryParam(value = "fareZoneQuery")
    private final String query;
    private final String authorityRef;
    private final ScopingMethodEnumeration scopingMethodEnumeration;
    private final ZoneTopologyEnumeration zoneTopologyEnumeration;


    private FareZoneSearch(Builder builder) {
        this.query = builder.query;
        this.authorityRef = builder.authorityRef;
        this.scopingMethodEnumeration =builder.scopingMethodEnumeration;
        this.zoneTopologyEnumeration = builder.zoneTopologyEnumeration;
    }

    public String getQuery() {
        return query;
    }

    public String getAuthorityRef() {
        return authorityRef;
    }

    public ScopingMethodEnumeration getScopingMethodEnumeration() {
        return scopingMethodEnumeration;
    }

    public ZoneTopologyEnumeration getZoneTopologyEnumeration() {
        return zoneTopologyEnumeration;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("query", query)
                .toString();
    }

    public static Builder newFareZoneSearchBuilder() {
        return new Builder();
    }

    public static class Builder {

        private String query;
        private String authorityRef;
        private ScopingMethodEnumeration scopingMethodEnumeration;
        private ZoneTopologyEnumeration zoneTopologyEnumeration;

        public Builder query(String query) {
            this.query = query;
            return this;
        }

        public Builder authorityRef(String authorityRef) {
            this.authorityRef = authorityRef;
            return this;
        }

        public Builder scopingMethod(ScopingMethodEnumeration scopingMethodEnumeration) {
            this.scopingMethodEnumeration = scopingMethodEnumeration;
            return this;
        }

        public Builder zoneTopology(ZoneTopologyEnumeration zoneTopologyEnumeration){
            this.zoneTopologyEnumeration = zoneTopologyEnumeration;
            return this;
        }

        public FareZoneSearch build() {
            return new FareZoneSearch(this);
        }
    }
}