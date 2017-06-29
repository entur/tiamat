package org.rutebanken.tiamat.exporter.params;

import javax.ws.rs.BeanParam;
import javax.ws.rs.QueryParam;
import java.util.Arrays;
import java.util.List;

/**
 * Export parameters.
 * Parameters specific for search related to a certain type like StopPlace does not necessary belong here.
 */
public class ExportParams {

    @QueryParam(value = "includeTopographicPlaces")
    private boolean includeTopographicPlaces = false;

    @QueryParam(value = "municipalityReference")
    private List<String> municipalityReferences;

    @QueryParam(value = "countyReference")
    private List<String> countyReferences;

    @BeanParam
    private StopPlaceSearch stopPlaceSearch;

    private ExportParams(boolean includeTopographicPlaces, List<String> municipalityReferences, List<String> countyReferences, StopPlaceSearch stopPlaceSearch) {
        this.includeTopographicPlaces = includeTopographicPlaces;
        this.municipalityReferences = municipalityReferences;
        this.countyReferences = countyReferences;
        this.stopPlaceSearch = stopPlaceSearch;
    }

    public ExportParams(StopPlaceSearch stopPlaceSearch) {
        this.stopPlaceSearch = stopPlaceSearch;
    }

    public ExportParams() {}

    public boolean isIncludeTopographicPlaces() {
        return includeTopographicPlaces;
    }

    public List<String> getMunicipalityReferences() {
        return municipalityReferences;
    }

    public List<String> getCountyReferences() {
        return countyReferences;
    }

    public StopPlaceSearch getStopPlaceSearch() {
        return stopPlaceSearch;
    }

    public static ExportParams.Builder newExportParamsBuilder() {
       return new Builder();
    }

    public static class Builder {

        private boolean includeTopographicPlaces;
        private List<String> municipalityReferences;
        private List<String> countyReferences;
        private StopPlaceSearch stopPlaceSearch;

        private Builder() { }

        public Builder setIncludeTopographicPlaces(boolean includeTopographicPlaces) {
            this.includeTopographicPlaces = includeTopographicPlaces;
            return this;
        }

        public Builder setMunicipalityReferences(List<String> municipalityReferences) {
            this.municipalityReferences = municipalityReferences;
            return this;
        }

        public Builder setMunicipalityReference(String ... municipalityReference) {
            this.municipalityReferences = Arrays.asList(municipalityReference);
            return this;
        }

        public Builder setCountyReferences(List<String> countyReferences) {
            this.countyReferences = countyReferences;
            return this;
        }

        public Builder setCountyReference(String ... countyReference) {
            this.countyReferences = Arrays.asList(countyReference);
            return this;
        }

        public Builder setStopPlaceSearch(StopPlaceSearch stopPlaceSearch) {
            this.stopPlaceSearch = stopPlaceSearch;
            return this;
        }

        public ExportParams build() {
            return new ExportParams(includeTopographicPlaces, municipalityReferences, countyReferences, stopPlaceSearch);
        }
    }
}
