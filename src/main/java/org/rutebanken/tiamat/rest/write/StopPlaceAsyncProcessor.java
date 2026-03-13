package org.rutebanken.tiamat.rest.write;

import org.rutebanken.tiamat.rest.write.dto.StopPlacesDto;

public interface StopPlaceAsyncProcessor {
    void processCreateStopPlace(Long jobId, StopPlacesDto dto);
    void processUpdateStopPlace(Long jobId, StopPlacesDto dto);
    void processDeleteStopPlace(Long jobId, String stopPlaceId);
}
