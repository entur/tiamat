package org.rutebanken.tiamat.importer.log;

import java.util.Timer;
import java.util.TimerTask;

public class ImportLogger extends Timer {

    private TimerTask importLoggerTask;

    public ImportLogger(ImportLoggerTask importLoggerTask) {
        super(ImportLogger.class.getSimpleName()+"-logger");
        this.importLoggerTask = importLoggerTask;
        scheduleAtFixedRate(this.importLoggerTask, 10000, 10000);
    }
}
