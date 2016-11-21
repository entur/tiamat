

package org.rutebanken.tiamat.model;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


@MappedSuperclass
public abstract class Assignment_VersionStructure_
    extends DataManagedObjectStructure
{

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    protected MultilingualStringEntity name;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    protected MultilingualStringEntity description;

    @Transient
    protected BigInteger order;

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public MultilingualStringEntity getDescription() {
        return description;
    }

    public void setDescription(MultilingualStringEntity value) {
        this.description = value;
    }

    public BigInteger getOrder() {
        return order;
    }

    public void setOrder(BigInteger value) {
        this.order = value;
    }

}
