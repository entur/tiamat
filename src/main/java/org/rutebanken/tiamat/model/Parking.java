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

import com.google.common.base.MoreObjects;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Parking
        extends Site_VersionStructure {

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
            parkingVehicleTypes = new ArrayList<>();
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
            parkingPaymentProcess = new ArrayList<>();
        }
        return this.parkingPaymentProcess;
    }

    public List<PaymentMethodEnumeration> getPaymentMethods() {
        if (paymentMethods == null) {
            paymentMethods = new ArrayList<>();
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
            currenciesAccepted = new ArrayList<>();
        }
        return this.currenciesAccepted;
    }

    public List<String> getCardsAccepted() {
        if (cardsAccepted == null) {
            cardsAccepted = new ArrayList<>();
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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("netexId", netexId)
                .add("version", version)
                .add("created", created)
                .add("changed", changed)
                .add("centroid", centroid)
                .add("parentSiteRef", parentSiteRef)
                .add("publicCode", publicCode)
                .add("label", label)
                .add("parkingPaymentProcess", parkingPaymentProcess)
                .add("paymentMethods", paymentMethods)
                .add("defaultCurrency", defaultCurrency)
                .add("currenciesAccepted", currenciesAccepted)
                .add("cardsAccepted", cardsAccepted)
                .add("paymentByMobile", paymentByMobile)
                .add("vehicleEntrances", vehicleEntrances)
                .add("pathLinks", pathLinks)
                .add("pathJunctions", pathJunctions)
                .add("navigationPaths", navigationPaths)
                .add("parkingType", parkingType)
                .add("parkingVehicleTypes", parkingVehicleTypes)
                .add("parkingLayout", parkingLayout)
                .add("numberOfParkingLevels", numberOfParkingLevels)
                .add("principalCapacity", principalCapacity)
                .add("totalCapacity", totalCapacity)
                .add("overnightParkingPermitted", overnightParkingPermitted)
                .add("prohibitedForHazardousMaterials", prohibitedForHazardousMaterials)
                .add("rechargingAvailable", rechargingAvailable)
                .add("secure", secure)
                .add("realTimeOccupancyAvailable", realTimeOccupancyAvailable)
                .add("parkingReservation", parkingReservation)
                .add("bookingUrl", bookingUrl)
                .add("freeParkingOutOfHours", freeParkingOutOfHours)
                .add("parkingProperties", parkingProperties)
                .add("parkingAreas", parkingAreas)
                .toString();
    }
}
