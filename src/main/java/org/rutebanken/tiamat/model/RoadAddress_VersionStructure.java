

package org.rutebanken.tiamat.model;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


    "gisFeatureRef",
    "roadNumber",
    "roadName",
    "bearingCompass",
    "bearingDegrees",
    "oddNumberRange",
@MappedSuperclass
public class RoadAddress_VersionStructure
    extends Address_VersionStructure
{

    protected String gisFeatureRef;

    protected String roadNumber;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    protected MultilingualStringEntity roadName;

    protected String bearingCompass;

    protected BigInteger bearingDegrees;

    @Transient
    protected RoadNumberRangeStructure oddNumberRange;

    @Transient
    protected RoadNumberRangeStructure evenNumberRange;

    public String getGisFeatureRef() {
        return gisFeatureRef;
    }

    public void setGisFeatureRef(String value) {
        this.gisFeatureRef = value;
    }

    public String getRoadNumber() {
        return roadNumber;
    }

    public void setRoadNumber(String value) {
        this.roadNumber = value;
    }

    public MultilingualStringEntity getRoadName() {
        return roadName;
    }

    public void setRoadName(MultilingualStringEntity value) {
        this.roadName = value;
    }

    public String getBearingCompass() {
        return bearingCompass;
    }

    public void setBearingCompass(String value) {
        this.bearingCompass = value;
    }

    public BigInteger getBearingDegrees() {
        return bearingDegrees;
    }

    public void setBearingDegrees(BigInteger value) {
        this.bearingDegrees = value;
    }

    public RoadNumberRangeStructure getOddNumberRange() {
        return oddNumberRange;
    }

    public void setOddNumberRange(RoadNumberRangeStructure value) {
        this.oddNumberRange = value;
    }

    public RoadNumberRangeStructure getEvenNumberRange() {
        return evenNumberRange;
    }

    public void setEvenNumberRange(RoadNumberRangeStructure value) {
        this.evenNumberRange = value;
    }

}
