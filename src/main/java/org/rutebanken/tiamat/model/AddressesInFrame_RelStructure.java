package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class AddressesInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<JAXBElement<? extends Address_VersionStructure>> address;

    public List<JAXBElement<? extends Address_VersionStructure>> getAddress() {
        if (address == null) {
            address = new ArrayList<JAXBElement<? extends Address_VersionStructure>>();
        }
        return this.address;
    }

}
