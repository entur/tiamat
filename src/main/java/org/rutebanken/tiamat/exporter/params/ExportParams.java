package org.rutebanken.tiamat.exporter.params;

import com.google.common.base.MoreObjects;

import javax.ws.rs.BeanParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.valueOf;
import static org.rutebanken.tiamat.exporter.params.ExportParams.ExportMode.RELEVANT;

/**
 * Export parameters.
 * Parameters specific for search related to a certain type like StopPlace does not necessary belong here.
 */
public class ExportParams {

    public enum ExportMode {NONE, RELEVANT, ALL}

    public enum VersionValidity {ALL, CURRENT, CURRENT_FUTURE}

    @DefaultValue(value = "RELEVANT")
    @QueryParam(value = "topopgraphicPlaceExportMode")
    private ExportMode topopgraphicPlaceExportMode = ExportMode.RELEVANT;

    @DefaultValue(value = "RELEVANT")
    @QueryParam(value = "tariffZoneExportMode")
    private ExportMode tariffZoneExportMode = ExportMode.RELEVANT;


    @QueryParam(value = "municipalityReference")
    private List<String> municipalityReferences;

    @QueryParam(value = "countyReference")
    private List<String> countyReferences;

    @BeanParam
    private StopPlaceSearch stopPlaceSearch;

    private ExportParams(ExportMode topopgraphicPlaceExportMode, ExportMode tariffZoneExportMode, List<String> municipalityReferences, List<String> countyReferences, StopPlaceSearch stopPlaceSearch) {
        this.topopgraphicPlaceExportMode = topopgraphicPlaceExportMode;
        this.tariffZoneExportMode = tariffZoneExportMode;
        this.municipalityReferences = municipalityReferences;
        this.countyReferences = countyReferences;
        this.stopPlaceSearch = stopPlaceSearch;
    }

    public ExportParams(StopPlaceSearch stopPlaceSearch) {
        this.stopPlaceSearch = stopPlaceSearch;
    }

    public ExportParams() {}

    public ExportMode getTopopgraphicPlaceExportMode() {
        return topopgraphicPlaceExportMode;
    }

    public ExportMode getTariffZoneExportMode() {
        return tariffZoneExportMode;
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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("topopgraphicPlaceExportMode", topopgraphicPlaceExportMode)
                .add("municipalityReferences", municipalityReferences)
                .add("countyReferences", countyReferences)
                .add("stopPlaceSearch", stopPlaceSearch)
                .add("tariffZoneExportMode", tariffZoneExportMode)
                .toString();
    }

    public static class Builder {
        private ExportMode tariffZoneExportMode = ExportMode.RELEVANT;
        private ExportMode topographicPlaceExportMode = ExportMode.RELEVANT;
        private List<String> municipalityReferences;
        private List<String> countyReferences;
        private StopPlaceSearch stopPlaceSearch;

        private Builder() { }

        public Builder setTopographicPlaceExportMode(ExportMode topographicPlaceExportMode) {
            this.topographicPlaceExportMode = topographicPlaceExportMode;
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

        public Builder setTariffZoneExportMode(ExportMode tariffZoneExportMode) {
            this.tariffZoneExportMode = tariffZoneExportMode;
            return  this;
        }

        public ExportParams build() {
            return new ExportParams(topographicPlaceExportMode, tariffZoneExportMode, municipalityReferences, countyReferences, stopPlaceSearch);
        }
    }
}
