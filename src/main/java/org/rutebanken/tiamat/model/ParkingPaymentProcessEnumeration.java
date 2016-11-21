

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum ParkingPaymentProcessEnumeration {

    FREE("free"),
    PAY_AT_BAY("payAtBay"),
    PAY_AND_DISPLAY("payAndDisplay"),
    PAY_AT_EXIT_BOOTH_MANUAL_COLLECTION("payAtExitBoothManualCollection"),
    PAY_AT_MACHINE_ON_FOOT_PRIOR_TO_EXIT("payAtMachineOnFootPriorToExit"),
    PAY_BY_PREPAID_TOKEN("payByPrepaidToken"),
    PAY_BY_MOBILE_DEVICE("payByMobileDevice"),
    UNDEFINED("undefined"),
    OTHER("other");
    private final String value;

    ParkingPaymentProcessEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ParkingPaymentProcessEnumeration fromValue(String v) {
        for (ParkingPaymentProcessEnumeration c: ParkingPaymentProcessEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
