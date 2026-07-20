package org.rutebanken.tiamat.ext.fintraffic.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import org.rutebanken.tiamat.model.LightingEnumeration;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.PaymentMethodEnumeration;

import java.util.ArrayList;
import java.util.List;

/**
 * Fintraffic extension of the core {@link Parking} entity.
 * <p>
 * Persists fields that are {@code @Transient} in the core model. Scalar fields
 * (e.g. {@code lighting}) are added as columns on the shared {@code parking} table
 * (populated only for {@code dtype = 'FintrafficParking'} rows).  Collection fields
 * use separate collection tables so Entur's core DDL remains unmodified.
 * Activated when the {@code fintraffic} Spring profile is active via
 * {@link FintrafficParkingEntityFactory}.
 */
@Entity
@DiscriminatorValue("FintrafficParking")
public class FintrafficParking extends Parking {

    @Column(name = "lighting")
    @Enumerated(EnumType.STRING)
    private LightingEnumeration lighting;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "parking_payment_methods",
            joinColumns = @JoinColumn(name = "parking_id")
    )
    @Column(name = "payment_method")
    private List<PaymentMethodEnumeration> paymentMethods;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "parking_info_links",
            joinColumns = @JoinColumn(name = "parking_id")
    )
    private List<FintrafficInfoLink> infoLinks;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "parking_vehicle_entrances",
            joinColumns = @JoinColumn(name = "parking_id")
    )
    private List<FintrafficParkingEntranceForVehicles> fintrafficVehicleEntrances;

    @Override
    public LightingEnumeration getLighting() {
        return lighting;
    }

    @Override
    public void setLighting(LightingEnumeration value) {
        this.lighting = value;
    }

    @Override
    public List<PaymentMethodEnumeration> getPaymentMethods() {
        if (paymentMethods == null) {
            paymentMethods = new ArrayList<>();
        }
        return paymentMethods;
    }

    public void setPaymentMethods(List<PaymentMethodEnumeration> value) {
        this.paymentMethods = value;
    }

    public List<FintrafficInfoLink> getInfoLinks() {
        if (infoLinks == null) {
            infoLinks = new ArrayList<>();
        }
        return infoLinks;
    }

    public void setInfoLinks(List<FintrafficInfoLink> infoLinks) {
        this.infoLinks = infoLinks;
    }

    public List<FintrafficParkingEntranceForVehicles> getFintrafficVehicleEntrances() {
        if (fintrafficVehicleEntrances == null) {
            fintrafficVehicleEntrances = new ArrayList<>();
        }
        return fintrafficVehicleEntrances;
    }

    public void setFintrafficVehicleEntrances(List<FintrafficParkingEntranceForVehicles> vehicleEntrances) {
        this.fintrafficVehicleEntrances = vehicleEntrances;
    }
}

