package org.rutebanken.tiamat.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table //(indexes = {@Index(name = "parent_topographic_ref_index", columnList = "parent_topographic_ref")})
public class TopographicPlace
        extends Place_VersionStructure {

    protected String isoCode;

    @Enumerated(EnumType.STRING)
    protected TopographicPlaceTypeEnumeration topographicPlaceType;

    @AttributeOverrides({
            @AttributeOverride(name = "ref", column = @Column(name = "country_ref")),
            @AttributeOverride(name = "value", column = @Column(name = "country_ref_value"))
    })
    @Embedded
    protected CountryRef countryRef;

//    @AttributeOverrides({
//            @AttributeOverride(name = "ref", column = @Column(name = "parent_topographic_ref")),
//            @AttributeOverride(name = "version", column = @Column(name = "parent_topographic_ref_version"))
//    })
//    @Embedded
    @Transient
    protected TopographicPlaceRefStructure parentTopographicPlaceRef;

    @OneToOne
    @Embedded
    protected TopographicPlace parentTopographicPlace;

    public TopographicPlace(EmbeddableMultilingualString name) {
        super(name);
    }

    public TopographicPlace() {
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String value) {
        this.isoCode = value;
    }

    public TopographicPlaceTypeEnumeration getTopographicPlaceType() {
        return topographicPlaceType;
    }

    public void setTopographicPlaceType(TopographicPlaceTypeEnumeration value) {
        this.topographicPlaceType = value;
    }

    public CountryRef getCountryRef() {
        return countryRef;
    }

    public void setCountryRef(CountryRef value) {
        this.countryRef = value;
    }

    public TopographicPlaceRefStructure getParentTopographicPlaceRef() {
        return parentTopographicPlaceRef;
    }


    public void setParentTopographicPlaceRef(TopographicPlaceRefStructure value) {
        this.parentTopographicPlaceRef = value;
    }

    public TopographicPlace getParentTopographicPlace() {
        return parentTopographicPlace;
    }


    public void setParentTopographicPlace(TopographicPlace value) {
        this.parentTopographicPlace = value;
    }

}
