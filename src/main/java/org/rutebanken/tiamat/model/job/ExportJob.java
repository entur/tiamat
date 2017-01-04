package org.rutebanken.tiamat.model.job;

import com.google.common.base.MoreObjects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.concurrent.Future;

@Entity
@XmlRootElement
public class ExportJob {

    @Id
    @GeneratedValue
    private Long id;

    private String jobUrl;
    private String fileName;

    private ZonedDateTime started;
    private ZonedDateTime finished;

    private  JobStatus status;

    public ExportJob() {
    }

    public ExportJob(JobStatus jobStatus) {
        status = jobStatus;
    }

    public ExportJob(String jobUrl, String fileName, JobStatus status) {
        this.jobUrl = jobUrl;
        this.fileName = fileName;
        this.status = status;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("id", id)
                .add("status", status)
                .add("jobUrl", jobUrl)
                .add("fileName", fileName)
                .add("started", started)
                .add("finished", finished)
                .toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobUrl() {
        return jobUrl;
    }

    public void setJobUrl(String jobUrl) {
        this.jobUrl = jobUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ZonedDateTime getStarted() {
        return started;
    }

    public void setStarted(ZonedDateTime started) {
        this.started = started;
    }

    public ZonedDateTime getFinished() {
        return finished;
    }

    public void setFinished(ZonedDateTime finished) {
        this.finished = finished;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }
}
