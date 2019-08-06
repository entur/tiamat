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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;

import javax.ws.rs.BeanParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import java.util.Arrays;
import java.util.List;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.COUNTY_REF_ARG_DESCRIPTION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.COUNTRY_REF_ARG_DESCRIPTION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.MUNICIPALITY_REF_ARG_DESCRIPTION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SEARCH_WITH_CODE_SPACE_ARG_DESCRIPTION;

/**
 * Export parameters.
 * Parameters specific for search related to a certain type like StopPlace does not necessary belong here.
 */
@ApiModel(description = "Export parameters")
public class ExportParams {

    public enum ExportMode {NONE, RELEVANT, ALL}

    public enum VersionValidity {ALL, CURRENT, CURRENT_FUTURE, MAX_VERSION}

    public static final ExportMode DEFAULT_TARIFF_ZONE_EXPORT_MODE = ExportMode.RELEVANT;

    public static final ExportMode DEFAULT_TOPOGRAPHIC_PLACE_EXPORT_MODE = ExportMode.RELEVANT;

    public static final ExportMode DEFAULT_GROUP_OF_STOP_PLACES_EXPORT_MODE = ExportMode.RELEVANT;

    @QueryParam(value = "topographicPlaceExportMode")
    @DefaultValue(value = "RELEVANT")
    @ApiParam(value = "Controls exported topographic places. If set to relevant, only topographic places relevant to exported stop places are exported.")
    @ApiModelProperty("Topographic place export mode")
    private ExportMode topographicPlaceExportMode = DEFAULT_TOPOGRAPHIC_PLACE_EXPORT_MODE;

    @QueryParam(value = "tariffZoneExportMode")
    @DefaultValue(value = "RELEVANT")
    @ApiParam(value = "Controls exported tariff zones. If set to relevant, only tariff zones relevant to exported stop places are exported.")
    @ApiModelProperty("Tariff zone export mode")
    private ExportMode tariffZoneExportMode = DEFAULT_TARIFF_ZONE_EXPORT_MODE;

    @QueryParam(value = "groupOfStopPlacesExportMode")
    @DefaultValue(value = "RELEVANT")
    @ApiParam(value = "Controls exported group of stop places. If set to relevant, only group of stop places relevant to exported stop places are exported.")
    @ApiModelProperty("Group of stop places export mode")
    private ExportMode groupOfStopPlacesExportMode = DEFAULT_GROUP_OF_STOP_PLACES_EXPORT_MODE;

    @QueryParam(value = "municipalityReference")
    @ApiParam(value = MUNICIPALITY_REF_ARG_DESCRIPTION)
    @ApiModelProperty("municipalityReference")
    private List<String> municipalityReferences;

    @QueryParam(value = "countyReference")
    @ApiParam(value = COUNTY_REF_ARG_DESCRIPTION)
    @ApiModelProperty("countyReference")
    private List<String> countyReferences;

    @QueryParam(value = "countryReference")
    @ApiParam(value = COUNTRY_REF_ARG_DESCRIPTION)
    @ApiModelProperty("countryReference")
    private List<String> countryReferences;

    @QueryParam(value = "codeSpace")
    @ApiParam(value = SEARCH_WITH_CODE_SPACE_ARG_DESCRIPTION)
    @ApiModelProperty("codeSpace")
    private String codeSpace;

    @BeanParam
    @ApiModelProperty(hidden = true)
    private StopPlaceSearch stopPlaceSearch;

    private ExportParams(ExportMode topographicPlaceExportMode, ExportMode tariffZoneExportMode, ExportMode groupOfStopPlacesExportMode, List<String> municipalityReferences, List<String> countyReferences, List<String> countryReferences, StopPlaceSearch stopPlaceSearch, String codeSpace) {
        this.topographicPlaceExportMode = topographicPlaceExportMode;
        this.tariffZoneExportMode = tariffZoneExportMode;
        this.groupOfStopPlacesExportMode = groupOfStopPlacesExportMode;
        this.municipalityReferences = municipalityReferences;
        this.countyReferences = countyReferences;
        this.countryReferences = countryReferences;
        this.stopPlaceSearch = stopPlaceSearch;
        this.codeSpace = codeSpace;
    }

    public ExportParams(StopPlaceSearch stopPlaceSearch) {
        this.stopPlaceSearch = stopPlaceSearch;
    }

    public ExportParams() {
    }

    public ExportMode getTopographicPlaceExportMode() {
        return topographicPlaceExportMode;
    }

    public ExportMode getTariffZoneExportMode() {
        return tariffZoneExportMode;
    }

    public ExportMode getGroupOfStopPlacesExportMode() {
        return groupOfStopPlacesExportMode;
    }

    public List<String> getMunicipalityReferences() {
        return municipalityReferences;
    }

    public List<String> getCountyReferences() {
        return countyReferences;
    }
    public List<String> getCountryReferences() {
        return countryReferences;
    }
    public StopPlaceSearch getStopPlaceSearch() {
        return stopPlaceSearch;
    }

    public String getCodeSpace() {
        return codeSpace;
    }

    public static ExportParams.Builder newExportParamsBuilder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("topographicPlaceExportMode", topographicPlaceExportMode)
                .add("municipalityReferences", municipalityReferences)
                .add("countyReferences", countyReferences)
                .add("countryReferences", countryReferences)
                .add("stopPlaceSearch", stopPlaceSearch)
                .add("tariffZoneExportMode", tariffZoneExportMode)
                .add("codeSpace", codeSpace)
                .toString();
    }

    public static class Builder {
        private ExportMode tariffZoneExportMode = DEFAULT_TARIFF_ZONE_EXPORT_MODE;
        private ExportMode topographicPlaceExportMode = DEFAULT_TOPOGRAPHIC_PLACE_EXPORT_MODE;
        private ExportMode groupOfStopPlacesExportMode = DEFAULT_GROUP_OF_STOP_PLACES_EXPORT_MODE;
        private List<String> municipalityReferences;
        private List<String> countyReferences;
        private List<String> countryReferences;
        private StopPlaceSearch stopPlaceSearch;
        private String codeSpace;

        private Builder() {
        }

        public Builder setTopographicPlaceExportMode(ExportMode topographicPlaceExportMode) {
            this.topographicPlaceExportMode = topographicPlaceExportMode;
            return this;
        }

        public Builder setMunicipalityReferences(List<String> municipalityReferences) {
            this.municipalityReferences = municipalityReferences;
            return this;
        }

        public Builder setMunicipalityReference(String... municipalityReference) {
            this.municipalityReferences = Arrays.asList(municipalityReference);
            return this;
        }

        public Builder setCountyReferences(List<String> countyReferences) {
            this.countyReferences = countyReferences;
            return this;
        }

        public Builder setCountyReference(String... countyReference) {
            this.countyReferences = Arrays.asList(countyReference);
            return this;
        }

        public Builder setCountryReferences(List<String> countryReferences) {
            this.countryReferences = countryReferences;
            return this;
        }

        public Builder setCountryReference(String... countryReference) {
            this.countryReferences = Arrays.asList(countryReference);
            return this;
        }

        public Builder setStopPlaceSearch(StopPlaceSearch stopPlaceSearch) {
            this.stopPlaceSearch = stopPlaceSearch;
            return this;
        }

        public Builder setTariffZoneExportMode(ExportMode tariffZoneExportMode) {
            this.tariffZoneExportMode = tariffZoneExportMode;
            return this;
        }

        public Builder setGroupOfStopPlacesExportMode(ExportMode groupOfStopPlacesExportMode) {
            this.groupOfStopPlacesExportMode = groupOfStopPlacesExportMode;
            return this;
        }

        public Builder setCodeSpace(String codeSpace) {
            this.codeSpace = codeSpace;
            return this;
        }

        public ExportParams build() {
            return new ExportParams(topographicPlaceExportMode, tariffZoneExportMode, groupOfStopPlacesExportMode, municipalityReferences, countyReferences, countryReferences, stopPlaceSearch, codeSpace);
        }
    }
}
