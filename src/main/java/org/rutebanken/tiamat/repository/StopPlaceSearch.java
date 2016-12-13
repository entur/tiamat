package org.rutebanken.tiamat.repository;

import com.google.common.base.MoreObjects;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class StopPlaceSearch {

    private String query;
    private List<String> municipalityIds;
    private List<String> countyIds;
    private List<StopTypeEnumeration> stopTypeEnumerations;
    private List<Long> idList;
    private Pageable pageable;

    public StopPlaceSearch() {}

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
}
