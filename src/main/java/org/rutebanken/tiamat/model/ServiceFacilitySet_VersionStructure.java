package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ServiceFacilitySet_VersionStructure
        extends FacilitySet_VersionStructure {

    protected List<AccommodationAccessEnumeration> accommodationAccessList;
    protected List<AccommodationFacilityEnumeration> accommodationFacilityList;
    protected BoardingPermissionEnumeration boardingPermisssion;
    protected List<BookingProcessEnumeration> bookingProcessFacilityList;
    protected List<CouchetteFacilityEnumeration> couchetteFacilityList;
    protected GroupBookingEnumeration groupBookingFacility;
    protected List<LuggageCarriageEnumeration> luggageCarriageFacilityList;
    protected List<ReservationEnumeration> serviceReservationFacilityList;
    protected List<UicProductCharacteristicEnumeration> uicProductCharacteristicList;
    protected UicRateTypeEnumeration uicTrainRate;
    protected Accommodations_RelStructure accommodations;
    protected OnboardStays_RelStructure onboardStays;

    public List<AccommodationAccessEnumeration> getAccommodationAccessList() {
        if (accommodationAccessList == null) {
            accommodationAccessList = new ArrayList<AccommodationAccessEnumeration>();
        }
        return this.accommodationAccessList;
    }

    public List<AccommodationFacilityEnumeration> getAccommodationFacilityList() {
        if (accommodationFacilityList == null) {
            accommodationFacilityList = new ArrayList<AccommodationFacilityEnumeration>();
        }
        return this.accommodationFacilityList;
    }

    public BoardingPermissionEnumeration getBoardingPermisssion() {
        return boardingPermisssion;
    }

    public void setBoardingPermisssion(BoardingPermissionEnumeration value) {
        this.boardingPermisssion = value;
    }

    public List<BookingProcessEnumeration> getBookingProcessFacilityList() {
        if (bookingProcessFacilityList == null) {
            bookingProcessFacilityList = new ArrayList<BookingProcessEnumeration>();
        }
        return this.bookingProcessFacilityList;
    }

    public List<CouchetteFacilityEnumeration> getCouchetteFacilityList() {
        if (couchetteFacilityList == null) {
            couchetteFacilityList = new ArrayList<CouchetteFacilityEnumeration>();
        }
        return this.couchetteFacilityList;
    }

    public GroupBookingEnumeration getGroupBookingFacility() {
        return groupBookingFacility;
    }

    public void setGroupBookingFacility(GroupBookingEnumeration value) {
        this.groupBookingFacility = value;
    }

    public List<LuggageCarriageEnumeration> getLuggageCarriageFacilityList() {
        if (luggageCarriageFacilityList == null) {
            luggageCarriageFacilityList = new ArrayList<LuggageCarriageEnumeration>();
        }
        return this.luggageCarriageFacilityList;
    }

    public List<ReservationEnumeration> getServiceReservationFacilityList() {
        if (serviceReservationFacilityList == null) {
            serviceReservationFacilityList = new ArrayList<ReservationEnumeration>();
        }
        return this.serviceReservationFacilityList;
    }

    public List<UicProductCharacteristicEnumeration> getUicProductCharacteristicList() {
        if (uicProductCharacteristicList == null) {
            uicProductCharacteristicList = new ArrayList<UicProductCharacteristicEnumeration>();
        }
        return this.uicProductCharacteristicList;
    }

    public UicRateTypeEnumeration getUicTrainRate() {
        return uicTrainRate;
    }

    public void setUicTrainRate(UicRateTypeEnumeration value) {
        this.uicTrainRate = value;
    }

    public Accommodations_RelStructure getAccommodations() {
        return accommodations;
    }

    public void setAccommodations(Accommodations_RelStructure value) {
        this.accommodations = value;
    }

    public OnboardStays_RelStructure getOnboardStays() {
        return onboardStays;
    }

    public void setOnboardStays(OnboardStays_RelStructure value) {
        this.onboardStays = value;
    }

}
