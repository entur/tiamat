package org.rutebanken.tiamat.ext.fintraffic.api.background;

import jakarta.annotation.PostConstruct;
import org.rutebanken.tiamat.ext.fintraffic.api.repository.NetexRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Profile("fintraffic-read-api")
@Service
public class ReadApiBackgroundJobs {
    private static final Logger logger = LoggerFactory.getLogger(ReadApiBackgroundJobs.class);

    private static final AtomicLong threadNumber = new AtomicLong();

    private final ScheduledExecutorService backgroundJobExecutor =
            Executors.newScheduledThreadPool(3, (runnable) -> new Thread(runnable, "read-api-background-job-"+threadNumber.incrementAndGet()));

    private final NetexRepository netexRepository;
    private final boolean backgroundJobsEnabled;

    @Autowired
    public ReadApiBackgroundJobs(
            NetexRepository netexRepository,
            @Value("${tiamat.ext.fintraffic.read-api-background-jobs.enabled:false}") boolean backgroundJobsEnabled
    ) {
        this.netexRepository = netexRepository;
        this.backgroundJobsEnabled = backgroundJobsEnabled;
    }

    @PostConstruct
    public void scheduleBackgroundJobs() {
        if (!backgroundJobsEnabled) {
            logger.info("Read API background jobs are disabled by configuration");
            return;
        }
        logger.info("Scheduling background job for netexRepository::checkDatabaseConsistency");
        backgroundJobExecutor.scheduleAtFixedRate(netexRepository::checkDatabaseConsistency, 2, 15, TimeUnit.MINUTES);
    }
}
