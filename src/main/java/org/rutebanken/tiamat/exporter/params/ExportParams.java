package org.rutebanken.tiamat.exporter.params;

import com.google.common.base.MoreObjects;

import javax.ws.rs.BeanParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.valueOf;

/**
 * Export parameters.
 * Parameters specific for search related to a certain type like StopPlace does not necessary belong here.
 */
public class ExportParams {

    public enum ExportMode {NONE, RELEVANT, ALL}

    public enum VersionValidity {ALL, CURRENT, CURRENT_FUTURE}

    public static final ExportMode DEFAULT_TARIFF_ZONE_EXPORT_MODE = ExportMode.RELEVANT;

    public static final ExportMode DEFAULT_TOPOGRAPHIC_PLACE_EXPORT_MODE = ExportMode.RELEVANT;

    @DefaultValue(value = "RELEVANT")
    @QueryParam(value = "topographicPlaceExportMode")
    private ExportMode topographicPlaceExportMode = DEFAULT_TOPOGRAPHIC_PLACE_EXPORT_MODE;

    @DefaultValue(value = "RELEVANT")
    @QueryParam(value = "tariffZoneExportMode")
    private ExportMode tariffZoneExportMode = DEFAULT_TARIFF_ZONE_EXPORT_MODE;

    @QueryParam(value = "municipalityReference")
    private List<String> municipalityReferences;

    @QueryParam(value = "countyReference")
    private List<String> countyReferences;

    @BeanParam
    private StopPlaceSearch stopPlaceSearch;

    private ExportParams(ExportMode topographicPlaceExportMode, ExportMode tariffZoneExportMode, List<String> municipalityReferences, List<String> countyReferences, StopPlaceSearch stopPlaceSearch) {
        this.topographicPlaceExportMode = topographicPlaceExportMode;
        this.tariffZoneExportMode = tariffZoneExportMode;
        this.municipalityReferences = municipalityReferences;
        this.countyReferences = countyReferences;
        this.stopPlaceSearch = stopPlaceSearch;
    }

    public ExportParams(StopPlaceSearch stopPlaceSearch) {
        this.stopPlaceSearch = stopPlaceSearch;
    }

    public ExportParams() {}

    public ExportMode getTopographicPlaceExportMode() {
        return topographicPlaceExportMode;
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
                .add("topographicPlaceExportMode", topographicPlaceExportMode)
                .add("municipalityReferences", municipalityReferences)
                .add("countyReferences", countyReferences)
                .add("stopPlaceSearch", stopPlaceSearch)
                .add("tariffZoneExportMode", tariffZoneExportMode)
                .toString();
    }

    public static class Builder {
        private ExportMode tariffZoneExportMode = DEFAULT_TARIFF_ZONE_EXPORT_MODE;
        private ExportMode topographicPlaceExportMode = DEFAULT_TOPOGRAPHIC_PLACE_EXPORT_MODE;
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
