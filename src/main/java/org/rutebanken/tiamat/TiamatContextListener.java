package org.rutebanken.tiamat;

import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.rutebanken.tiamat.service.TagRemover;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.samtrafiken.aws.metrics.ReporterManager;

/**
 * This class adds some metrics reporting to Tiamat in AWS.
 *
 * @author Dick Zetterberg (dick@transitor.se)
 * @version 2020-12-14
 */
@WebListener
public class TiamatContextListener implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(TagRemover.class);

    private static final String IS_RUNNING_IN_AWS_ENV = "IS_RUNNING_IN_AWS";

    private ReporterManager reporterManager;
    private boolean isRunningInAws;

    public TiamatContextListener() {
        this.isRunningInAws = Objects.equals(System.getenv(IS_RUNNING_IN_AWS_ENV), "true");
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        log.info("TiamatContextListener.contextInitialized");
        try {
            logSystemInfo();
            if (isRunningInAws) {
                // Create a metrics reporter and health check reporter that will report metrics and health to cloud watch
                reporterManager = ReporterManager.createMetricsAndHealthCheckReporters("nsr-tiamat", 20, 60, TimeUnit.SECONDS);
            }
        } catch (Throwable t) {
            log.error("Caught throwable during initialization", t);
            throw t;
        }
    }

    /**
     * This method is called when the application is destroyed, for example
     * when it is reloaded or redeployed.
     * Here we go through some trouble to clean up things as much as possible
     * because otherwise memory usage grows and old threads continue to run
     */
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        log.error("TiamatContextListener.contextDestroyed");
        try {
            if (isRunningInAws) {
                reporterManager.shutdown();
            }
            log.error("TiamatContextListener application shutdown completed");
        } catch (Throwable throwable) {
            log.error("Caught throwable when shutting down", throwable);
        }
    }

    private void logSystemInfo() {
        log.error("*******************************************************************");
        log.error("****                                                           ****");
        log.error("****                   Tiamat initializing                     ****");
        log.error("****                                                           ****");
        log.error("*******************************************************************");
        // Log the java version
        log.error("Java version: " + System.getProperty("java.version"));
        log.error("Java home: " + System.getProperty("java.home"));
        // Log the default system file encoding.
        log.error("System default file encoding: " + System.getProperty("file.encoding"));
        log.error("System user.language: " + System.getProperty("user.language"));
        log.error("System user.region: " + System.getProperty("user.region"));
        // Default locale is composed by user.language and user.region originally
        log.error("Default locale: " + Locale.getDefault());
        log.error("System user.timezone: " + System.getProperty("user.timezone"));
        // Default timezone is composed by user.timezone OR user.region and java.home
        log.error("Default timezone: " + TimeZone.getDefault());
    }

}
