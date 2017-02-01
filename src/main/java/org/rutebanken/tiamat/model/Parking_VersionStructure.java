package org.rutebanken.tiamat.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


public class Parking_VersionStructure
        extends Site_VersionStructure {

    protected SitePathLinks_RelStructure pathLinks;
    protected PathJunctions_RelStructure pathJunctions;
    protected Accesses_RelStructure accesses;
    protected NavigationPaths_RelStructure navigationPaths;
    protected String publicCode;
    protected MultilingualStringEntity label;
    protected ParkingTypeEnumeration parkingType;
    protected List<ParkingVehicleEnumeration> parkingVehicleTypes;
    protected ParkingLayoutEnumeration parkingLayout;
    protected BigInteger numberOfParkingLevels;
    protected BigInteger principalCapacity;
    protected BigInteger totalCapacity;
    protected Boolean overnightParkingPermitted;
    protected Boolean prohibitedForHazardousMaterials;
    protected Boolean rechargingAvailable;
    protected Boolean secure;
    protected Boolean realTimeOccupancyAvailable;
    protected List<ParkingPaymentProcessEnumeration> parkingPaymentProcess;
    protected List<PaymentMethodEnumeration> paymentMethods;
    protected String defaultCurrency;
    protected List<String> currenciesAccepted;
    protected List<String> cardsAccepted;
    protected ParkingReservationEnumeration parkingReservation;
    protected String bookingUrl;
    protected PaymentByMobileStructure paymentByMobile;
    protected Boolean freeParkingOutOfHours;
    protected ParkingProperties_RelStructure parkingProperties;
    protected ParkingAreas_RelStructure parkingAreas;
    protected ParkingEntrancesForVehicles_RelStructure vehicleEntrances;

    public SitePathLinks_RelStructure getPathLinks() {
        return pathLinks;
    }

    public void setPathLinks(SitePathLinks_RelStructure value) {
        this.pathLinks = value;
    }

    public PathJunctions_RelStructure getPathJunctions() {
        return pathJunctions;
    }

    public void setPathJunctions(PathJunctions_RelStructure value) {
        this.pathJunctions = value;
    }

    public Accesses_RelStructure getAccesses() {
        return accesses;
    }

    public void setAccesses(Accesses_RelStructure value) {
        this.accesses = value;
    }

    public NavigationPaths_RelStructure getNavigationPaths() {
        return navigationPaths;
    }

    public void setNavigationPaths(NavigationPaths_RelStructure value) {
        this.navigationPaths = value;
    }

    public String getPublicCode() {
        return publicCode;
    }

    public void setPublicCode(String value) {
        this.publicCode = value;
    }

    public MultilingualStringEntity getLabel() {
        return label;
    }

    public void setLabel(MultilingualStringEntity value) {
        this.label = value;
    }

    public ParkingTypeEnumeration getParkingType() {
        return parkingType;
    }

    public void setParkingType(ParkingTypeEnumeration value) {
        this.parkingType = value;
    }

    public List<ParkingVehicleEnumeration> getParkingVehicleTypes() {
        if (parkingVehicleTypes == null) {
            parkingVehicleTypes = new ArrayList<ParkingVehicleEnumeration>();
        }
        return this.parkingVehicleTypes;
    }

    public ParkingLayoutEnumeration getParkingLayout() {
        return parkingLayout;
    }

    public void setParkingLayout(ParkingLayoutEnumeration value) {
        this.parkingLayout = value;
    }

    public BigInteger getNumberOfParkingLevels() {
        return numberOfParkingLevels;
    }

    public void setNumberOfParkingLevels(BigInteger value) {
        this.numberOfParkingLevels = value;
    }

    public BigInteger getPrincipalCapacity() {
        return principalCapacity;
    }

    public void setPrincipalCapacity(BigInteger value) {
        this.principalCapacity = value;
    }

    public BigInteger getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(BigInteger value) {
        this.totalCapacity = value;
    }

    public Boolean isOvernightParkingPermitted() {
        return overnightParkingPermitted;
    }

    public void setOvernightParkingPermitted(Boolean value) {
        this.overnightParkingPermitted = value;
    }

    public Boolean isProhibitedForHazardousMaterials() {
        return prohibitedForHazardousMaterials;
    }

    public void setProhibitedForHazardousMaterials(Boolean value) {
        this.prohibitedForHazardousMaterials = value;
    }

    public Boolean isRechargingAvailable() {
        return rechargingAvailable;
    }

    public void setRechargingAvailable(Boolean value) {
        this.rechargingAvailable = value;
    }

    public Boolean isSecure() {
        return secure;
    }

    public void setSecure(Boolean value) {
        this.secure = value;
    }

    public Boolean isRealTimeOccupancyAvailable() {
        return realTimeOccupancyAvailable;
    }

    public void setRealTimeOccupancyAvailable(Boolean value) {
        this.realTimeOccupancyAvailable = value;
    }

    public List<ParkingPaymentProcessEnumeration> getParkingPaymentProcess() {
        if (parkingPaymentProcess == null) {
            parkingPaymentProcess = new ArrayList<ParkingPaymentProcessEnumeration>();
        }
        return this.parkingPaymentProcess;
    }

    public List<PaymentMethodEnumeration> getPaymentMethods() {
        if (paymentMethods == null) {
            paymentMethods = new ArrayList<PaymentMethodEnumeration>();
        }
        return this.paymentMethods;
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(String value) {
        this.defaultCurrency = value;
    }

    public List<String> getCurrenciesAccepted() {
        if (currenciesAccepted == null) {
            currenciesAccepted = new ArrayList<String>();
        }
        return this.currenciesAccepted;
    }

    public List<String> getCardsAccepted() {
        if (cardsAccepted == null) {
            cardsAccepted = new ArrayList<String>();
        }
        return this.cardsAccepted;
    }

    public ParkingReservationEnumeration getParkingReservation() {
        return parkingReservation;
    }

    public void setParkingReservation(ParkingReservationEnumeration value) {
        this.parkingReservation = value;
    }

    public String getBookingUrl() {
        return bookingUrl;
    }

    public void setBookingUrl(String value) {
        this.bookingUrl = value;
    }

    public PaymentByMobileStructure getPaymentByMobile() {
        return paymentByMobile;
    }

    public void setPaymentByMobile(PaymentByMobileStructure value) {
        this.paymentByMobile = value;
    }

    public Boolean isFreeParkingOutOfHours() {
        return freeParkingOutOfHours;
    }

    public void setFreeParkingOutOfHours(Boolean value) {
        this.freeParkingOutOfHours = value;
    }

    public ParkingProperties_RelStructure getParkingProperties() {
        return parkingProperties;
    }

    public void setParkingProperties(ParkingProperties_RelStructure value) {
        this.parkingProperties = value;
    }

    public ParkingAreas_RelStructure getParkingAreas() {
        return parkingAreas;
    }

    public void setParkingAreas(ParkingAreas_RelStructure value) {
        this.parkingAreas = value;
    }

    public ParkingEntrancesForVehicles_RelStructure getVehicleEntrances() {
        return vehicleEntrances;
    }

    public void setVehicleEntrances(ParkingEntrancesForVehicles_RelStructure value) {
        this.vehicleEntrances = value;
    }

}
