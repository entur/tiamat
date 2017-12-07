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
import org.rutebanken.tiamat.model.GroupOfStopPlaces;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.Set;

public class GroupOfStopPlacesSearch implements SearchObject {

    @QueryParam(value = "groupQuery")
    private String query;

    @QueryParam(value = "groupIds")
    private List<String> idList;

    @QueryParam(value = "stopPlaceId")
    private String stopPlaceId;


    private GroupOfStopPlacesSearch(Builder builder) {
        this.query = builder.query;
        this.idList = builder.idList;
        this.stopPlaceId = builder.stopPlaceId;
    }

    public String getQuery() {
        return query;
    }

    public List<String> getIdList() {
        return idList;
    }

    public String getStopPlaceId() {
        return stopPlaceId;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("query", query)
                .add("idList", idList)
                .add("stopPlaceId", stopPlaceId)
                .toString();
    }

    public static Builder newGroupOfStopPlacesSearchBuilder() { return new Builder(); }

    public static class Builder {

        private String query;
        private List<String> idList;
        private String stopPlaceId;

        public Builder query(String query) {
            this.query = query;
            return this;
        }

        public Builder idList(List<String> idList) {
            this.idList = idList;
            return this;
        }

        public Builder stopPlaceId(String stopPlaceId) {
            this.stopPlaceId = stopPlaceId;
            return this;
        }

        public GroupOfStopPlacesSearch build() {
            return new GroupOfStopPlacesSearch(this);
        }
    }
}