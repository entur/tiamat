package org.rutebanken.tiamat.model;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Parking
        extends Site_VersionStructure {

    @Transient
    protected Accesses_RelStructure accesses;
    @Transient
    protected String publicCode;
    @Transient
    protected MultilingualStringEntity label;
    @Transient
    protected List<ParkingPaymentProcessEnumeration> parkingPaymentProcess;
    @Transient
    protected List<PaymentMethodEnumeration> paymentMethods;
    @Transient
    protected String defaultCurrency;
    @Transient
    protected List<String> currenciesAccepted;
    @Transient
    protected List<String> cardsAccepted;
    @Transient
    protected PaymentByMobileStructure paymentByMobile;
    @Transient
    protected ParkingEntrancesForVehicles_RelStructure vehicleEntrances;

    @Transient
    protected SitePathLinks_RelStructure pathLinks;
    @Transient
    protected PathJunctions_RelStructure pathJunctions;
    @Transient
    protected NavigationPaths_RelStructure navigationPaths;

    @Enumerated(EnumType.STRING)
    protected ParkingTypeEnumeration parkingType;

    @ElementCollection(targetClass = ParkingVehicleEnumeration.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
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
    protected ParkingReservationEnumeration parkingReservation;
    protected String bookingUrl;
    protected Boolean freeParkingOutOfHours;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<ParkingProperties> parkingProperties;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<ParkingArea> parkingAreas;

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

    public List<ParkingProperties> getParkingProperties() {
        return parkingProperties;
    }

    public void setParkingProperties(List<ParkingProperties> value) {
        this.parkingProperties = value;
    }

    public List<ParkingArea> getParkingAreas() {
        return parkingAreas;
    }

    public void setParkingAreas(List<ParkingArea> value) {
        this.parkingAreas = value;
    }

    public ParkingEntrancesForVehicles_RelStructure getVehicleEntrances() {
        return vehicleEntrances;
    }

    public void setVehicleEntrances(ParkingEntrancesForVehicles_RelStructure value) {
        this.vehicleEntrances = value;
    }

}
