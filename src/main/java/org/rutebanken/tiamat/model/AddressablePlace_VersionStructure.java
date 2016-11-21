

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "url",
    "image",
    "postalAddress",
@MappedSuperclass
public class AddressablePlace_VersionStructure
    extends Place_VersionStructure
{

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

    public AddressablePlace_VersionStructure() {}

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
