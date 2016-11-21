

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class FlexibleRoute_VersionStructure
    extends Route_VersionStructure
{

    protected FlexibleRouteTypeEnumeration flexibleRouteType;

    public FlexibleRouteTypeEnumeration getFlexibleRouteType() {
        return flexibleRouteType;
    }

    public void setFlexibleRouteType(FlexibleRouteTypeEnumeration value) {
        this.flexibleRouteType = value;
    }

}
