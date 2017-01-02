package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ExportJob {

    public int jobId;
    public String jobUrl;
    public String fileName;

}
