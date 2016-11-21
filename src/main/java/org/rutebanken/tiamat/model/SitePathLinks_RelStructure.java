

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


@Entity
public class SitePathLinks_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<PathLinkRefStructure> pathLinkRefOrSitePathLink;

    public List<PathLinkRefStructure> getPathLinkRefOrSitePathLink() {
        if (pathLinkRefOrSitePathLink == null) {
            pathLinkRefOrSitePathLink = new ArrayList<PathLinkRefStructure>();
        }
        return this.pathLinkRefOrSitePathLink;
    }

}
