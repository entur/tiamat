package org.rutebanken.tiamat.model;

public class RoutePoint_VersionStructure
        extends Point_VersionStructure {

    protected Boolean viaFlag;
    protected Boolean borderCrossing;

    public Boolean isViaFlag() {
        return viaFlag;
    }

    public void setViaFlag(Boolean value) {
        this.viaFlag = value;
    }

    public Boolean isBorderCrossing() {
        return borderCrossing;
    }

    public void setBorderCrossing(Boolean value) {
        this.borderCrossing = value;
    }

}
