/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


public class Accommodation_VersionedChildStructure
        extends VersionedChildStructure {

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
