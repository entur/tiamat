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
import io.swagger.annotations.ApiParam;

import javax.ws.rs.QueryParam;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.VERSION_VALIDITY_ARG_DESCRIPTION;

public class TopographicPlaceSearch implements SearchObject {

    @QueryParam(value = "versionValidity")
    @ApiParam(value = VERSION_VALIDITY_ARG_DESCRIPTION)
    private ExportParams.VersionValidity versionValidity;


    private TopographicPlaceSearch(Builder builder) {
        this.versionValidity = builder.versionValidity;
    }

    /**
     * TODO: VersionValidity should be moved to ExportParams. It should not be possible to search for different version validities in one export.
     * This work was started in branch ROR-390_full_versioning_tariff_zones. But things were prioritized differently.
     *
     * @return
     */
    public ExportParams.VersionValidity getVersionValidity() {
        return versionValidity;
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("versionValidity", versionValidity)
                .toString();
    }

    public static Builder newTopographicPlaceSearchBuilder() {
        return new Builder();
    }

    public static class Builder {

        private ExportParams.VersionValidity versionValidity;

        public Builder versionValidity(ExportParams.VersionValidity versionValidity) {
            this.versionValidity = versionValidity;
            return this;
        }

        public TopographicPlaceSearch build() {
            return new TopographicPlaceSearch(this);
        }
    }
}