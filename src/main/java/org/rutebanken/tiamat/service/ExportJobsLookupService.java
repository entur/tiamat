package org.rutebanken.tiamat.service;

import org.rutebanken.tiamat.model.job.ExportJob;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExportJobsLookupService {
    private List<ExportJob> exportJobList = new ArrayList<>();

    public List<ExportJob> getExportJob2List() {
        return exportJobList;
    }

    public void addExportJob(ExportJob exportJob) {
        exportJobList.add(exportJob);
    }

    public Optional<ExportJob> getExportJob(long exportJobId)  {
        return exportJobList.stream().filter(exportJob -> exportJob.getId().equals(exportJobId)).findFirst();
    }

    public void updateExportJob(ExportJob exportJob) {
        exportJobList = exportJobList.stream().map(ep -> ep.getId().equals(exportJob.getId()) ? exportJob : ep)
                .collect(Collectors.toList());

    }
}
