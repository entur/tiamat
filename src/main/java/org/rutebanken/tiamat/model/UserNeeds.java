

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class UserNeeds {

    protected List<UserNeed> userNeed;

    public List<UserNeed> getUserNeed() {
        if (userNeed == null) {
            userNeed = new ArrayList<UserNeed>();
        }
        return this.userNeed;
    }

}
