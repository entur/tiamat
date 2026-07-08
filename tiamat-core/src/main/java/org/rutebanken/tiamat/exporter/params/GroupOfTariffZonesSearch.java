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

import java.util.List;

public class GroupOfTariffZonesSearch implements SearchObject {

    @QueryParam(value = "groupQuery")
    private String query;

    @QueryParam(value = "groupIds")
    private List<String> idList;

    @QueryParam(value = "tariffZoneId")
    private String tariffZoneId;


    private GroupOfTariffZonesSearch(Builder builder) {
        this.query = builder.query;
        this.idList = builder.idList;
        this.tariffZoneId = builder.tariffZoneId;
    }

    public String getQuery() {
        return query;
    }

    public List<String> getIdList() {
        return idList;
    }

    public String getTariffZoneId() {
        return tariffZoneId;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("query", query)
                .add("idList", idList)
                .add("tariffZoneId", tariffZoneId)
                .toString();
    }

    public static Builder newGroupOfTariffZonesSearchBuilder() { return new Builder(); }

    public static class Builder {

        private String query;
        private List<String> idList;
        private String tariffZoneId;

        public Builder query(String query) {
            this.query = query;
            return this;
        }

        public Builder idList(List<String> idList) {
            this.idList = idList;
            return this;
        }

        public Builder tariffZoneId(String tariffZoneId) {
            this.tariffZoneId = tariffZoneId;
            return this;
        }

        public GroupOfTariffZonesSearch build() {
            return new GroupOfTariffZonesSearch(this);
        }
    }
}