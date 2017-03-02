package org.rutebanken.tiamat.repository;

import com.google.common.base.MoreObjects;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class StopPlaceSearch {

    private String query;
    private List<String> municipalityIds;
    private List<String> countyIds;
    private List<StopTypeEnumeration> stopTypeEnumerations;
    private List<String> netexIdList;
    private Pageable pageable;

    public StopPlaceSearch() {}

    private StopPlaceSearch(String query, List<String> municipalityIds, List<String> countyIds, List<StopTypeEnumeration> stopTypeEnumerations, List<String> netexIdList, Pageable pageable) {
        this.query = query;
        this.municipalityIds = municipalityIds;
        this.countyIds = countyIds;
        this.stopTypeEnumerations = stopTypeEnumerations;
        this.netexIdList = netexIdList;
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


    public List<String> getNetexIdList() {
        return netexIdList;
    }

    public void setNetexIdList(List<String> netexIdList) {
        this.netexIdList = netexIdList;
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
                .add("netexIdList", getNetexIdList())
                .add("page", getPageable().getPageNumber())
                .add("size", getPageable().getPageSize())
                .toString();
    }

    public static class Builder {

        private String query;
        private List<String> municipalityIds;
        private List<String> countyIds;
        private List<StopTypeEnumeration> stopTypeEnumerations;
        private List<String> idList;
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

        public Builder setNetexIdList(List<String> netexIdList) {
            this.idList = netexIdList;
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
