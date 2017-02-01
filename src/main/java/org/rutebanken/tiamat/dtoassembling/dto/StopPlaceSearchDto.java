package org.rutebanken.tiamat.dtoassembling.dto;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Data transfer object for stop place search params.
 */
public class StopPlaceSearchDto {

    public static final int DEFAULT_PAGE = 1;
    public static final int DEFAULT_PAGE_SIZE = 20;

    @DefaultValue(value = "0") @QueryParam(value = "page") public int page;
    @DefaultValue(value = "20") @QueryParam(value = "size") public int size;
    @QueryParam(value = "q") public String query;

    @QueryParam(value = "municipalityReference") public List<String> municipalityReferences;
    @QueryParam(value = "countyReference") public List<String> countyReferences;
    @QueryParam(value = "stopPlaceType") public List<String> stopPlaceTypes;
    @QueryParam(value = "idList") public List<String> idList;

    public StopPlaceSearchDto() {}

    public StopPlaceSearchDto(int page, int size, String query, List<String> municipalityReferences,
                              List<String> countyReferences, List<String> stopPlaceTypes, List<String> idList) {
        this.page = page;
        this.size = size;
        this.query = query;
        this.municipalityReferences = municipalityReferences;
        this.countyReferences = countyReferences;
        this.stopPlaceTypes = stopPlaceTypes;
        this.idList = idList;
    }

    public static class Builder {
        private int page = DEFAULT_PAGE;
        private int size = DEFAULT_PAGE_SIZE;

        private String query;
        private List<String> municipalityReferences;
        private List<String> countyReferences;
        private List<String> stopPlaceTypes;
        private List<String> idList;

        public Builder setPage(int page) {
            this.page = page;
            return this;
        }

        public Builder setSize(int size) {
            this.size = size;
            return this;
        }

        public Builder setQuery(String query) {
            this.query = query;
            return this;
        }

        public Builder setMunicipalityReferences(List<String> municipalityReferences) {
            this.municipalityReferences = municipalityReferences;
            return this;
        }

        public Builder setCountyReferences(List<String> countyReferences) {
            this.countyReferences = countyReferences;
            return this;
        }

        public Builder setStopPlaceTypes(List<String> stopPlaceTypes) {
            this.stopPlaceTypes = stopPlaceTypes;
            return this;
        }

        public Builder setIdList(List<String> idList) {
            this.idList = idList;
            return this;
        }

        public StopPlaceSearchDto build() {
            return new StopPlaceSearchDto(page, size, query, municipalityReferences, countyReferences, stopPlaceTypes, idList);
        }
    }
}
