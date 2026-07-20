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
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.PaymentMethodEnumeration;

import java.util.ArrayList;
import java.util.List;

/**
 * Fintraffic extension of the core {@link Parking} entity.
 * <p>
 * Persists fields that are {@code @Transient} in the core model, using a
 * separate collection table so Entur's core DDL remains unmodified.
 * Activated when the {@code fintraffic} Spring profile is active via
 * {@link FintrafficParkingEntityFactory}.
 */
@Entity
@DiscriminatorValue("FintrafficParking")
public class FintrafficParking extends Parking {

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
}

