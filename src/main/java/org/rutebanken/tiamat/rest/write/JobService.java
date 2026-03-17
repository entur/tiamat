package org.rutebanken.tiamat.rest.write;

import org.rutebanken.tiamat.model.job.AsyncStopPlaceJob;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJobStatus;
import org.rutebanken.tiamat.model.job.StopPlaceIdMapping;
import org.rutebanken.tiamat.repository.AsyncStopPlaceJobRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.RejectedExecutionException;

@Service
public class JobService {

    private final AsyncStopPlaceJobRepository repo;

    public JobService(AsyncStopPlaceJobRepository repo) {
        this.repo = repo;
    }

    public AsyncStopPlaceJob createJob() {
        var job = new AsyncStopPlaceJob();
        job.setStatus(AsyncStopPlaceJobStatus.PROCESSING);
        job.setCreatedIds(Collections.emptyList());
        return repo.save(job);
    }

    public Optional<AsyncStopPlaceJob> getJob(Long jobId) {
        return repo.findById(jobId);
    }

    public void succeed(Long id, List<StopPlaceIdMapping> createdStopPlaceIds) {
        var job = repo.findById(id).orElseThrow();
        job.setStatus(AsyncStopPlaceJobStatus.FINISHED);
        job.setCreatedIds(createdStopPlaceIds);
        repo.save(job);
    }

    public AsyncStopPlaceJob fail(Long id, Exception exception) {
        var job = repo.findById(id).orElseThrow();
        job.setStatus(AsyncStopPlaceJobStatus.FAILED);
        job.setReason(formatException(exception));
        return repo.save(job);
    }

    private String formatException(Exception e) {
        if (e instanceof IllegalArgumentException) {
            return e.getMessage();
        } else if (e instanceof RejectedExecutionException) {
            return "The job queue is full. Please try again later.";
        }
        return "An unexpected error occurred.";
    }
}
