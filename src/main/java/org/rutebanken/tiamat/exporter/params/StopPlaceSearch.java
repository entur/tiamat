package org.rutebanken.tiamat.exporter.params;

import com.google.common.base.MoreObjects;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import java.time.Instant;
import java.util.List;

public class StopPlaceSearch {

    /**
     * zero-based page index
     */
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_PAGE_SIZE = 20;

    @DefaultValue(value = "0") @QueryParam(value = "page")
    private int page = DEFAULT_PAGE;

    @DefaultValue(value = "20") @QueryParam(value = "size")
    private int size = DEFAULT_PAGE_SIZE;

    @QueryParam(value = "q")
    private String query;

    @QueryParam(value = "stopPlaceType")
    private List<StopTypeEnumeration> stopTypeEnumerations;

    @QueryParam(value = "idList")
    private List<String> netexIdList;

    @QueryParam(value = "allVersions")
    private boolean allVersions;

    @DefaultValue(value = "ALL")
    @QueryParam(value = "versionValidity")
    private ExportParams.VersionValidity versionValidity;

    @QueryParam(value = "withoutLocationOnly")
    private boolean withoutLocationOnly;

    @QueryParam(value = "version")
    private Long version;

    @QueryParam(value = "tag")
    private List<String> tags;

    private Instant pointInTime;

    public StopPlaceSearch() {}

    private StopPlaceSearch(String query,
                            List<StopTypeEnumeration> stopTypeEnumerations,
                            List<String> netexIdList,
                            boolean allVersions,
                            boolean withoutLocationOnly,
                            Instant pointInTime,
                            Long version,
                            ExportParams.VersionValidity versionValidity,
                            List<String> tags,
                            int page, int size) {
        this.query = query;
        this.stopTypeEnumerations = stopTypeEnumerations;
        this.netexIdList = netexIdList;
        this.allVersions = allVersions;
        this.withoutLocationOnly = withoutLocationOnly;
        this.pointInTime = pointInTime;
        this.version = version;
        this.versionValidity = versionValidity;
        this.tags = tags;
        this.page = page;
        this.size = size;
    }

    public String getQuery() {
        return query;
    }

    public List<StopTypeEnumeration> getStopTypeEnumerations() {
        return stopTypeEnumerations;
    }

    public Pageable getPageable() {
        return new PageRequest(page, size);
    }

    public List<String> getNetexIdList() {
        return netexIdList;
    }

    public Long getVersion() {
        return version;
    }

    public boolean isAllVersions() {
        return allVersions;
    }

    public boolean isWithoutLocationOnly() {
        return withoutLocationOnly;
    }

    public Instant getPointInTime() {
        return pointInTime;
    }

    public ExportParams.VersionValidity getVersionValidity() {
        return versionValidity;
    }

    public List<String> getTags() {
        return tags;
    }

    // TODO: Remove or update
    public boolean isEmpty() {
        return !((query != null && !query.isEmpty())
                || (tags != null && !tags.isEmpty())
                || (stopTypeEnumerations != null && !stopTypeEnumerations.isEmpty())
                || (netexIdList != null && !netexIdList.isEmpty()));
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("q", getQuery())
                .add("stopPlaceType", getStopTypeEnumerations())
                .add("netexIdList", getNetexIdList())
                .add("allVersions", isAllVersions())
                .add("versionValidity", getVersionValidity())
                .add("tags", tags)
                .add("page", page)
                .add("size", size)
                .toString();
    }

    public static Builder newStopPlaceSearchBuilder() {
        return new Builder();
    }

    public static class Builder {

        private String query;
        private List<StopTypeEnumeration> stopTypeEnumerations;
        private List<String> idList;
        private boolean allVersions;
        private boolean withoutLocationOnly;
        private Long version;
        private Instant pointInTime;
        private ExportParams.VersionValidity versionValidity;
        private List<String> tags;
        private int page = DEFAULT_PAGE;
        private int size = DEFAULT_PAGE_SIZE;

        private Builder() {
        }

        public Builder setQuery(String query) {
            this.query = query;
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

        public Builder setPage(int page) {
            this.page = page;
            return this;
        }

        public Builder setSize(int size) {
            this.size = size;
            return this;
        }

        public Builder setAllVersions(boolean allVersions) {
            this.allVersions = allVersions;
            return this;
        }

        public Builder setWithoutLocationOnly(boolean withoutLocationOnly) {
            this.withoutLocationOnly = withoutLocationOnly;
            return this;
        }

        public Builder setVersion(Long version) {
            this.version = version;
            return this;
        }

        public Builder setPointInTime(Instant pointInTime) {
            this.pointInTime = pointInTime;
            return this;
        }

        public Builder setVersionValidity(ExportParams.VersionValidity versionValidity) {
            this.versionValidity = versionValidity;
            return this;
        }

        public Builder setTags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public StopPlaceSearch build() {
            return new StopPlaceSearch(query, stopTypeEnumerations, idList, allVersions, withoutLocationOnly, pointInTime, version, versionValidity, tags, page, size);
        }

    }
}
