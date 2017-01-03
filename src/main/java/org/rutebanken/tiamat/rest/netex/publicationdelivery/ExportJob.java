package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.concurrent.Future;

@XmlRootElement
public class ExportJob {

    public int jobId;
    public String jobUrl;
    public String fileName;
    public Future future;
    public Status status;

    public ExportJob(int jobId, String jobUrl, String fileName, Status status) {
        this.jobId = jobId;
        this.jobUrl = jobUrl;
        this.fileName = fileName;
        this.status = status;
    }

    static enum Status {
        PROCESSING,
        FINISHED
    }

}
