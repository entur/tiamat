package org.rutebanken.tiamat.model;


import javax.persistence.*;

@MappedSuperclass
public abstract class AddressablePlace extends Place {

    @Transient
    protected String url;

    @Transient
    protected String image;

    @Transient
    protected PostalAddress postalAddress;

    @Transient
    protected RoadAddress roadAddress;

    public AddressablePlace(EmbeddableMultilingualString name) {
        super(name);
    }

    public AddressablePlace() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String value) {
        this.url = value;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String value) {
        this.image = value;
    }

    public PostalAddress getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(PostalAddress value) {
        this.postalAddress = value;
    }

    public RoadAddress getRoadAddress() {
        return roadAddress;
    }

    public void setRoadAddress(RoadAddress value) {
        this.roadAddress = value;
    }

}
