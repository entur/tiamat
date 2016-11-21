package org.rutebanken.tiamat.model;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@MappedSuperclass
public class TopographicPlace_VersionStructure
        extends Place_VersionStructure {

    protected String isoCode;

    @Transient
    protected TopographicPlaceDescriptor_VersionedChildStructure descriptor;

    @Transient
    protected AlternativeDescriptors alternativeDescriptors;


    @Enumerated(EnumType.STRING)
    protected TopographicPlaceTypeEnumeration topographicPlaceType;

    protected Boolean placeCentre;

    protected String postCode;

    @Embedded
    protected CountryRef countryRef;

    @Transient
    protected CountryRefs_RelStructure otherCountries;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    protected TopographicPlaceRefStructure parentTopographicPlaceRef;

    @Transient
    protected TopographicPlaceRefs_RelStructure adjacentPlaces;

    @Transient
    protected TopographicPlaceRefs_RelStructure containedIn;

    @Transient
    protected Accesses_RelStructure accesses;

    public TopographicPlace_VersionStructure(EmbeddableMultilingualString name) {
        super(name);
    }

    public TopographicPlace_VersionStructure() {
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String value) {
        this.isoCode = value;
    }

    public TopographicPlaceDescriptor_VersionedChildStructure getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(TopographicPlaceDescriptor_VersionedChildStructure value) {
        this.descriptor = value;
    }

    public AlternativeDescriptors getAlternativeDescriptors() {
        return alternativeDescriptors;
    }

    public void setAlternativeDescriptors(AlternativeDescriptors value) {
        this.alternativeDescriptors = value;
    }


    public TopographicPlaceTypeEnumeration getTopographicPlaceType() {
        return topographicPlaceType;
    }


    public void setTopographicPlaceType(TopographicPlaceTypeEnumeration value) {
        this.topographicPlaceType = value;
    }


    public Boolean isPlaceCentre() {
        return placeCentre;
    }


    public void setPlaceCentre(Boolean value) {
        this.placeCentre = value;
    }


    public String getPostCode() {
        return postCode;
    }


    public void setPostCode(String value) {
        this.postCode = value;
    }


    public CountryRef getCountryRef() {
        return countryRef;
    }


    public void setCountryRef(CountryRef value) {
        this.countryRef = value;
    }


    public CountryRefs_RelStructure getOtherCountries() {
        return otherCountries;
    }


    public void setOtherCountries(CountryRefs_RelStructure value) {
        this.otherCountries = value;
    }


    public TopographicPlaceRefStructure getParentTopographicPlaceRef() {
        return parentTopographicPlaceRef;
    }


    public void setParentTopographicPlaceRef(TopographicPlaceRefStructure value) {
        this.parentTopographicPlaceRef = value;
    }


    public TopographicPlaceRefs_RelStructure getAdjacentPlaces() {
        return adjacentPlaces;
    }


    public void setAdjacentPlaces(TopographicPlaceRefs_RelStructure value) {
        this.adjacentPlaces = value;
    }


    public TopographicPlaceRefs_RelStructure getContainedIn() {
        return containedIn;
    }


    public void setContainedIn(TopographicPlaceRefs_RelStructure value) {
        this.containedIn = value;
    }


    public Accesses_RelStructure getAccesses() {
        return accesses;
    }


    public void setAccesses(Accesses_RelStructure value) {
        this.accesses = value;
    }

}
