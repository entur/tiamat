package org.rutebanken.tiamat.model.job;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;

@Entity
@Schema(description = "Asynchronous StopPlace mutation job model")
public class AsyncStopPlaceJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique id for the entity")
    private Long id;

    @Schema(description = "Job status")
    private AsyncStopPlaceJobStatus status;

    @Schema(description = "List of submitted to created StopPlace ID mappings")
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<StopPlaceIdMapping> createdIds;

    @Schema(description = "Reason for failure if the job has failed")
    private String reason;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AsyncStopPlaceJobStatus getStatus() {
        return status;
    }

    public void setStatus(AsyncStopPlaceJobStatus status) {
        this.status = status;
    }

    public List<StopPlaceIdMapping> getCreatedIds() {
        return createdIds;
    }

    public void setCreatedIds(List<StopPlaceIdMapping> createdIds) {
        this.createdIds = createdIds;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String errorMessage) {
        this.reason = errorMessage;
    }
}
