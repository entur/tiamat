package org.rutebanken.tiamat.model;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;


@MappedSuperclass
public class AddressablePlace_VersionStructure
        extends Place_VersionStructure {

    @Transient
    protected String url;

    @Transient
    protected String image;

    @Transient
    protected PostalAddress postalAddress;


    @Transient
    protected RoadAddress roadAddress;

    public AddressablePlace_VersionStructure(EmbeddableMultilingualString name) {
        super(name);
    }

    public AddressablePlace_VersionStructure() {
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
