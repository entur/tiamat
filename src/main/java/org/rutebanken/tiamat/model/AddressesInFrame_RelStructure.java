

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


public class AddressesInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<JAXBElement<? extends Address_VersionStructure>> address;

    public List<JAXBElement<? extends Address_VersionStructure>> getAddress() {
        if (address == null) {
            address = new ArrayList<JAXBElement<? extends Address_VersionStructure>>();
        }
        return this.address;
    }

}
