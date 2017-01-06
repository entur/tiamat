package org.rutebanken.tiamat.model;

public class EscalatorEquipment_VersionStructure
        extends StairEquipment_VersionStructure {

    protected Boolean tactileActuators;
    protected Boolean energySaving;
    protected Boolean dogsMustBeCarried;

    public Boolean isTactileActuators() {
        return tactileActuators;
    }

    public void setTactileActuators(Boolean value) {
        this.tactileActuators = value;
    }

    public Boolean isEnergySaving() {
        return energySaving;
    }

    public void setEnergySaving(Boolean value) {
        this.energySaving = value;
    }

    public Boolean isDogsMustBeCarried() {
        return dogsMustBeCarried;
    }

    public void setDogsMustBeCarried(Boolean value) {
        this.dogsMustBeCarried = value;
    }

}
