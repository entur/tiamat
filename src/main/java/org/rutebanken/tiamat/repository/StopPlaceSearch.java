package org.rutebanken.tiamat.repository;

import com.google.common.base.MoreObjects;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.BufferedReader;
import java.util.List;

public class StopPlaceSearch {

    private String query;
    private List<String> municipalityIds;
    private List<String> countyIds;
    private List<StopTypeEnumeration> stopTypeEnumerations;
    private List<Long> idList;
    private Pageable pageable;

    public StopPlaceSearch() {}

    private StopPlaceSearch(String query, List<String> municipalityIds, List<String> countyIds, List<StopTypeEnumeration> stopTypeEnumerations, List<Long> idList, Pageable pageable) {
        this.query = query;
        this.municipalityIds = municipalityIds;
        this.countyIds = countyIds;
        this.stopTypeEnumerations = stopTypeEnumerations;
        this.idList = idList;
        this.pageable = pageable;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<String> getMunicipalityIds() {
        return municipalityIds;
    }

    public void setMunicipalityIds(List<String> municipalityIds) {
        this.municipalityIds = municipalityIds;
    }

    public List<String> getCountyIds() {
        return countyIds;
    }

    public void setCountyIds(List<String> countyIds) {
        this.countyIds = countyIds;
    }

    public List<StopTypeEnumeration> getStopTypeEnumerations() {
        return stopTypeEnumerations;
    }

    public void setStopTypeEnumerations(List<StopTypeEnumeration> stopTypeEnumerations) {
        this.stopTypeEnumerations = stopTypeEnumerations;
    }

    public Pageable getPageable() {
        return pageable;
    }

    public void setPageable(Pageable pageable) {
        this.pageable = pageable;
    }


    public List<Long> getIdList() {
        return idList;
    }

    public void setIdList(List<Long> idList) {
        this.idList = idList;
    }

    public boolean isEmpty() {
        return !((query != null && !query.isEmpty())
                || countyIds != null || municipalityIds != null
                || (stopTypeEnumerations != null && !stopTypeEnumerations.isEmpty()));
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("q", getQuery())
                .add("municipalityReferences", getMunicipalityIds())
                .add("countyReference", getCountyIds())
                .add("stopPlaceType", getStopTypeEnumerations())
                .add("idList", getIdList())
                .add("page", getPageable().getPageNumber())
                .add("size", getPageable().getPageSize())
                .toString();
    }

    public static class Builder {

        private String query;
        private List<String> municipalityIds;
        private List<String> countyIds;
        private List<StopTypeEnumeration> stopTypeEnumerations;
        private List<Long> idList;
        private Pageable pageable = new PageRequest(0, 20);

        public Builder setQuery(String query) {
            this.query = query;
            return this;
        }

        public Builder setMunicipalityIds(List<String> municipalityIds) {
            this.municipalityIds = municipalityIds;
            return this;
        }

        public Builder setCountyIds(List<String> countyIds) {
            this.countyIds = countyIds;
            return this;
        }

        public Builder setStopTypeEnumerations(List<StopTypeEnumeration> stopTypeEnumerations) {
            this.stopTypeEnumerations = stopTypeEnumerations;
            return this;
        }

        public Builder setIdList(List<Long> idList) {
            this.idList = idList;
            return this;
        }

        public Builder setPageable(Pageable pageable) {
            this.pageable = pageable;
            return this;
        }

        public StopPlaceSearch build() {
            return new StopPlaceSearch(query, municipalityIds, countyIds, stopTypeEnumerations, idList, pageable);
        }

    }
}
