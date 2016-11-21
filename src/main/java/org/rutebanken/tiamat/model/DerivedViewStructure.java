

package org.rutebanken.tiamat.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


@MappedSuperclass
public abstract class DerivedViewStructure {

    protected BrandingRefStructure brandingRef;

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    protected Long id;

    public BrandingRefStructure getBrandingRef() {
        return brandingRef;
    }

    public void setBrandingRef(BrandingRefStructure value) {
        this.brandingRef = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

}
