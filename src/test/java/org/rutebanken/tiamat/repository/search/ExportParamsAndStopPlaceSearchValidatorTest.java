package org.rutebanken.tiamat.repository.search;

import org.junit.Test;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.exporter.params.StopPlaceSearch;

import java.time.Instant;

public class ExportParamsAndStopPlaceSearchValidatorTest {

    private ExportParamsAndStopPlaceSearchValidator validator = new ExportParamsAndStopPlaceSearchValidator();

    @Test(expected = IllegalArgumentException.class)
    public void versionAndPointInTimeCannotBeCombined() {
        ExportParams exportParams = ExportParams.newExportParamsBuilder()
                .setStopPlaceSearch(StopPlaceSearch.newStopPlaceSearchBuilder()
                        .setVersion(1L)
                        .setPointInTime(Instant.now())
                        .build())
                .build();

        validator.validateExportParams(exportParams);
    }

    @Test(expected = IllegalArgumentException.class)
    public void versionValidityAndPointInTimeCannotBeCombined() {
        ExportParams exportParams = ExportParams.newExportParamsBuilder()
                .setVersionValidity(ExportParams.VersionValidity.CURRENT_FUTURE)
                .setStopPlaceSearch(StopPlaceSearch.newStopPlaceSearchBuilder()
                        .setPointInTime(Instant.now())
                        .build())
                .build();

        validator.validateExportParams(exportParams);
    }

    @Test(expected = IllegalArgumentException.class)
    public void versionValidityAndAllversionsCannotBeCombined() {
        ExportParams exportParams = ExportParams.newExportParamsBuilder()
                .setVersionValidity(ExportParams.VersionValidity.CURRENT_FUTURE)
                .setStopPlaceSearch(StopPlaceSearch.newStopPlaceSearchBuilder()
                        .setAllVersions(true)
                        .build())
                .build();

        validator.validateExportParams(exportParams);
    }

}