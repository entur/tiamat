package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class UserNeeds {

    protected List<UserNeed> userNeed;

    public List<UserNeed> getUserNeed() {
        if (userNeed == null) {
            userNeed = new ArrayList<UserNeed>();
        }
        return this.userNeed;
    }

}
