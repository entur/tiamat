

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


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
