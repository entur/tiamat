package org.rutebanken.tiamat.repository.search;


import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.exporter.params.StopPlaceSearch;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;

@Service
public class ExportParamsAndStopPlaceSearchValidator {

    public void validateExportParams(ExportParams exportParams) {

        StopPlaceSearch stopPlaceSearch = exportParams.getStopPlaceSearch();
        Set<String> paramsExplicitySet = new HashSet<>();

        addIfTrue(ALL_VERSIONS, stopPlaceSearch.isAllVersions(), paramsExplicitySet);
        addIfNonNull(VERSION, stopPlaceSearch.getVersion(), paramsExplicitySet);
        addIfNonNull(POINT_IN_TIME, stopPlaceSearch.getPointInTime(), paramsExplicitySet);
        addIfNonNull("versionValidity", stopPlaceSearch.getVersionValidity(), paramsExplicitySet);

        if (paramsExplicitySet.size() > 1) {
            String message = "Parameters cannot be combined: " + paramsExplicitySet + ". Remove one of them";
            throw new IllegalArgumentException(message);
        }
    }

    private void addIfNonNull(String name, Object value, Set<String> paramsExplicitySet) {
        if (value != null) {
            paramsExplicitySet.add(name);
        }
    }

    private void addIfTrue(String name, boolean value, Set<String> set) {
        if (value) {
            set.add(name);
        }
    }
}
