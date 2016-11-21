

package org.rutebanken.tiamat.model;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class OrderedVersionOfObjectRefStructure
    extends VersionOfObjectRefStructure
{

    protected BigInteger order;

    public BigInteger getOrder() {
        return order;
    }

    public void setOrder(BigInteger value) {
        this.order = value;
    }

}
