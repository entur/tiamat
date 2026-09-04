package org.rutebanken.tiamat.model.job;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
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

    @Schema(description = "The stop places that the job wrote, with the version that each write produced")
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<WrittenStopPlace> writtenStopPlaces;

    @Schema(description = "Reason for failure if the job has failed")
    private String reason;

    @Schema(description = "The kind of failure, if the job has failed")
    @Enumerated(EnumType.STRING)
    private JobFailureReason reasonCode;

    @Schema(description = "The version the stop place is at now, if the job failed because the version moved on")
    private Long currentVersion;

    @Schema(description = "Username of the principal that submitted the job")
    private String createdBy;

    @Schema(description = "When the job was submitted")
    private Instant createdAt;

    @Schema(description = "When a worker claimed the job, refreshed while it is being processed")
    private Instant claimedAt;

    @Schema(description = "Claims identifying the caller, so the write can be authorized and attributed where it is performed")
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> principalClaims;

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

    public List<WrittenStopPlace> getWrittenStopPlaces() {
        return writtenStopPlaces;
    }

    public void setWrittenStopPlaces(List<WrittenStopPlace> writtenStopPlaces) {
        this.writtenStopPlaces = writtenStopPlaces;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String errorMessage) {
        this.reason = errorMessage;
    }

    public JobFailureReason getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(JobFailureReason reasonCode) {
        this.reasonCode = reasonCode;
    }

    public Long getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(Long currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getClaimedAt() {
        return claimedAt;
    }

    public void setClaimedAt(Instant claimedAt) {
        this.claimedAt = claimedAt;
    }

    public Map<String, Object> getPrincipalClaims() {
        return principalClaims;
    }

    public void setPrincipalClaims(Map<String, Object> principalClaims) {
        this.principalClaims = principalClaims;
    }
}
