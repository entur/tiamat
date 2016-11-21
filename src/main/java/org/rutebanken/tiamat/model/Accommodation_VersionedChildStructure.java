

package org.rutebanken.tiamat.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "name",
    "fareClass",
    "classOfUseRef",
    "accommodationFacility",
    "couchetteFacility",
    "maximumNumberOfBerths",
    "berthFacility",
    "showerFacility",
    "toiletFacility",
    "genderLimitation",
    "nuisanceFacilityList",
public class Accommodation_VersionedChildStructure
    extends VersionedChildStructure
{

    protected MultilingualStringEntity name;

    protected ClassOfUseRef classOfUseRef;
    protected AccommodationFacilityEnumeration accommodationFacility;
    protected CouchetteFacilityEnumeration couchetteFacility;
    protected BigInteger maximumNumberOfBerths;
    protected BerthFacilityEnumeration berthFacility;
    protected SanitaryFacilityEnumeration showerFacility;
    protected SanitaryFacilityEnumeration toiletFacility;
    protected GenderLimitationEnumeration genderLimitation;
    protected List<NuisanceFacilityEnumeration> nuisanceFacilityList;
    protected List<PassengerCommsFacilityEnumeration> passengerCommsFacilityList;

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public ClassOfUseRef getClassOfUseRef() {
        return classOfUseRef;
    }

    public void setClassOfUseRef(ClassOfUseRef value) {
    }

    public AccommodationFacilityEnumeration getAccommodationFacility() {
        return accommodationFacility;
    }

    public void setAccommodationFacility(AccommodationFacilityEnumeration value) {
        this.accommodationFacility = value;
    }

    public CouchetteFacilityEnumeration getCouchetteFacility() {
        return couchetteFacility;
    }

    public void setCouchetteFacility(CouchetteFacilityEnumeration value) {
        this.couchetteFacility = value;
    }

    public BigInteger getMaximumNumberOfBerths() {
        return maximumNumberOfBerths;
    }

    public void setMaximumNumberOfBerths(BigInteger value) {
        this.maximumNumberOfBerths = value;
    }

    public BerthFacilityEnumeration getBerthFacility() {
        return berthFacility;
    }

    public void setBerthFacility(BerthFacilityEnumeration value) {
        this.berthFacility = value;
    }

    public SanitaryFacilityEnumeration getShowerFacility() {
        return showerFacility;
    }

    public void setShowerFacility(SanitaryFacilityEnumeration value) {
        this.showerFacility = value;
    }

    public SanitaryFacilityEnumeration getToiletFacility() {
        return toiletFacility;
    }

    public void setToiletFacility(SanitaryFacilityEnumeration value) {
        this.toiletFacility = value;
    }

    public GenderLimitationEnumeration getGenderLimitation() {
        return genderLimitation;
    }

    public void setGenderLimitation(GenderLimitationEnumeration value) {
        this.genderLimitation = value;
    }

    public List<NuisanceFacilityEnumeration> getNuisanceFacilityList() {
        if (nuisanceFacilityList == null) {
            nuisanceFacilityList = new ArrayList<NuisanceFacilityEnumeration>();
        }
        return this.nuisanceFacilityList;
    }

    public List<PassengerCommsFacilityEnumeration> getPassengerCommsFacilityList() {
        if (passengerCommsFacilityList == null) {
            passengerCommsFacilityList = new ArrayList<PassengerCommsFacilityEnumeration>();
        }
        return this.passengerCommsFacilityList;
    }

}
