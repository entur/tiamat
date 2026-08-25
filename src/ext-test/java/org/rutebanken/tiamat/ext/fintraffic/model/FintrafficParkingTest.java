package org.rutebanken.tiamat.ext.fintraffic.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import org.junit.jupiter.api.Test;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.PaymentMethodEnumeration;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FintrafficParkingTest {

    @Test
    void isSubclassOfParking() {
        assertThat(Parking.class).isAssignableFrom(FintrafficParking.class);
    }

    @Test
    void paymentMethodsField_hasElementCollectionAnnotation() throws NoSuchFieldException {
        Field field = FintrafficParking.class.getDeclaredField("paymentMethods");

        assertThat(field.isAnnotationPresent(ElementCollection.class))
                .as("paymentMethods field must be @ElementCollection to be persisted")
                .isTrue();
    }

    @Test
    void paymentMethodsField_hasCollectionTableAnnotation() throws NoSuchFieldException {
        Field field = FintrafficParking.class.getDeclaredField("paymentMethods");

        CollectionTable table = field.getAnnotation(CollectionTable.class);
        assertThat(table).isNotNull();
        assertThat(table.name()).isEqualTo("parking_payment_methods");
    }

    @Test
    void setAndGetPaymentMethods_usesOwnField_notParentTransientField() {
        FintrafficParking parking = new FintrafficParking();

        parking.setPaymentMethods(List.of(PaymentMethodEnumeration.CASH, PaymentMethodEnumeration.CREDIT_CARD));

        // getPaymentMethods() must return from the @ElementCollection field, not the parent @Transient field
        assertThat(parking.getPaymentMethods())
                .containsExactly(PaymentMethodEnumeration.CASH, PaymentMethodEnumeration.CREDIT_CARD);
    }

    @Test
    void parentTransientField_isNotAffectedBySubclassSetter() throws Exception {
        FintrafficParking parking = new FintrafficParking();
        parking.setPaymentMethods(List.of(PaymentMethodEnumeration.CASH));

        // The parent @Transient protected field should remain null (we never write to it)
        Field parentField = Parking.class.getDeclaredField("paymentMethods");
        parentField.setAccessible(true);
        Object parentFieldValue = parentField.get(parking);

        assertThat(parentFieldValue)
                .as("parent @Transient paymentMethods must remain null; only the shadowed field is written")
                .isNull();
    }

    @Test
    void getPaymentMethods_returnsEmptyList_whenNothingSet() {
        FintrafficParking parking = new FintrafficParking();

        assertThat(parking.getPaymentMethods()).isEmpty();
    }
}
