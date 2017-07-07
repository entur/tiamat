package org.rutebanken.tiamat.exporter.params;

import com.google.common.base.MoreObjects;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import java.time.Instant;
import java.util.List;
import java.util.Set;

public class ParkingSearch {

    /**
     * zero-based page index
     */
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_PAGE_SIZE = 20;

    @DefaultValue(value = "0") @QueryParam(value = "page")
    private int page = DEFAULT_PAGE;

    @DefaultValue(value = "20") @QueryParam(value = "size")
    private int size = DEFAULT_PAGE_SIZE;

    private Set<String> parentSiteRefs;

    @QueryParam(value = "allVersions")
    private boolean allVersions;

    // Use builder
    private ParkingSearch() {}

    private ParkingSearch(boolean allVersions, int page, int size, Set<String> parentSiteRefs) {
        this.allVersions = allVersions;
        this.page = page;
        this.size = size;
        this.parentSiteRefs = parentSiteRefs;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public Set<String> getParentSiteRefs() {
        return parentSiteRefs;
    }

    public boolean isAllVersions() {
        return allVersions;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("page", page)
                .add("size", size)
                .add("parentSiteRefs", parentSiteRefs)
                .add("allVersions", allVersions)
                .toString();
    }

    public static Builder newParkingSearchBuilder() {
        return new Builder();
    }

    public static class Builder {

        private boolean allVersions;
        private int page = DEFAULT_PAGE;
        private int size = DEFAULT_PAGE_SIZE;
        private Set<String> parentSiteRefs;

        private Builder() {
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

        public Builder setParentSiteRefs(Set<String> parentSiteRefs) {
            this.parentSiteRefs = parentSiteRefs;
            return this;
        }

        public ParkingSearch build() {
            return new ParkingSearch(allVersions, page, size, parentSiteRefs);
        }
    }
}
