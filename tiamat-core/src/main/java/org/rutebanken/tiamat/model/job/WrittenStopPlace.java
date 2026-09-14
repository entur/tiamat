package org.rutebanken.tiamat.model.job;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A stop place that a write job wrote, and the version that the write produced.
 * <p>
 * The same record covers all three operations. A create fills in {@code submittedId}, because the
 * caller chose an id that the system replaced. An update and a delete leave it null, because the
 * caller already knew the id.
 */
@Schema(description = "A stop place that the job wrote, and the version that the write produced.")
public record WrittenStopPlace(

        @Schema(description = "The ID that you submitted. Null for an update and for a delete.",
                example = "NSR:StopPlace:1")
        String submittedId,

        @Schema(description = "The ID of the stop place in the system.", example = "NSR:StopPlace:64062")
        String netexId,

        @Schema(description = "The version that this write produced. Send this version with the next update.",
                example = "2")
        Long version
) {

    public static WrittenStopPlace created(String submittedId, String netexId, Long version) {
        return new WrittenStopPlace(submittedId, netexId, version);
    }

    public static WrittenStopPlace changed(String netexId, Long version) {
        return new WrittenStopPlace(null, netexId, version);
    }
}
