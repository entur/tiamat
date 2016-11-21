package org.rutebanken.tiamat.model;

public class FlexibleRoute_VersionStructure
        extends Route_VersionStructure {

    protected FlexibleRouteTypeEnumeration flexibleRouteType;

    public FlexibleRouteTypeEnumeration getFlexibleRouteType() {
        return flexibleRouteType;
    }

    public void setFlexibleRouteType(FlexibleRouteTypeEnumeration value) {
        this.flexibleRouteType = value;
    }

}
