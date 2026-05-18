package org.rutebanken.tiamat.model.job;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Mapping of submitted ID to created StopPlace ID")
public record StopPlaceIdMapping(
        @Schema(description = "The submitted/client-provided ID") String submittedId,
        @Schema(description = "The created StopPlace ID in the system") String createdId
) {}
