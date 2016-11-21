package org.rutebanken.tiamat.model;

public class PassengerAccessibilityNeedsStructure {

    protected Boolean accompaniedByCarer;
    protected UserNeeds userNeeds;
    protected Suitabilities suitabilities;

    public Boolean isAccompaniedByCarer() {
        return accompaniedByCarer;
    }

    public void setAccompaniedByCarer(Boolean value) {
        this.accompaniedByCarer = value;
    }

    public UserNeeds getUserNeeds() {
        return userNeeds;
    }

    public void setUserNeeds(UserNeeds value) {
        this.userNeeds = value;
    }

    public Suitabilities getSuitabilities() {
        return suitabilities;
    }

    public void setSuitabilities(Suitabilities value) {
        this.suitabilities = value;
    }

}
