package org.rutebanken.tiamat.rest.write;

import org.rutebanken.tiamat.model.job.AsyncStopPlaceJob;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJobStatus;
import org.rutebanken.tiamat.repository.AsyncStopPlaceJobRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    public AsyncStopPlaceJob succeed(Long id, List<String> stopPlaceIds) {
        var job = repo.findById(id).orElseThrow();
        job.setStatus(AsyncStopPlaceJobStatus.FINISHED);
        job.setCreatedIds(stopPlaceIds);
        return repo.save(job);
    }

    public AsyncStopPlaceJob fail(Long id, String reason) {
        var job = repo.findById(id).orElseThrow();
        job.setStatus(AsyncStopPlaceJobStatus.FAILED);
        job.setReason(reason);
        return repo.save(job);
    }
}
