package org.rutebanken.tiamat.model;

public class PostalAddress_VersionStructure
        extends Address_VersionStructure {

    protected String houseNumber;
    protected MultilingualStringEntity buildingName;
    protected MultilingualStringEntity addressLine1;
    protected MultilingualStringEntity addressLine2;
    protected MultilingualStringEntity street;
    protected MultilingualStringEntity town;
    protected MultilingualStringEntity suburb;
    protected String postCode;
    protected String postCodeExtension;
    protected String postalRegion;
    protected MultilingualStringEntity province;
    protected AddressRefStructure roadAddressRef;

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String value) {
        this.houseNumber = value;
    }

    public MultilingualStringEntity getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(MultilingualStringEntity value) {
        this.buildingName = value;
    }

    public MultilingualStringEntity getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(MultilingualStringEntity value) {
        this.addressLine1 = value;
    }

    public MultilingualStringEntity getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(MultilingualStringEntity value) {
        this.addressLine2 = value;
    }

    public MultilingualStringEntity getStreet() {
        return street;
    }

    public void setStreet(MultilingualStringEntity value) {
        this.street = value;
    }

    public MultilingualStringEntity getTown() {
        return town;
    }

    public void setTown(MultilingualStringEntity value) {
        this.town = value;
    }

    public MultilingualStringEntity getSuburb() {
        return suburb;
    }

    public void setSuburb(MultilingualStringEntity value) {
        this.suburb = value;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String value) {
        this.postCode = value;
    }

    public String getPostCodeExtension() {
        return postCodeExtension;
    }

    public void setPostCodeExtension(String value) {
        this.postCodeExtension = value;
    }

    public String getPostalRegion() {
        return postalRegion;
    }

    public void setPostalRegion(String value) {
        this.postalRegion = value;
    }

    public MultilingualStringEntity getProvince() {
        return province;
    }

    public void setProvince(MultilingualStringEntity value) {
        this.province = value;
    }

    public AddressRefStructure getRoadAddressRef() {
        return roadAddressRef;
    }

    public void setRoadAddressRef(AddressRefStructure value) {
        this.roadAddressRef = value;
    }

}
