

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class VehicleEntrance_VersionStructure
    extends SiteEntrance_VersionStructure
{

    protected Boolean _public;

    public Boolean isPublic() {
        return _public;
    }

    public void setPublic(Boolean value) {
        this._public = value;
    }

}
