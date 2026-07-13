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

public enum CheckProcessTypeEnumeration {

    NONE("none"),
    UNKNOWN("unknown"),
    BOARDING("boarding"),
    ALIGHTING("alighting"),
    TICKET_PURCHASE("ticketPurchase"),
    TICKET_COLLECTION("ticketCollection"),
    TICKET_VALIDATION("ticketValidation"),
    BAGGAGE_CHECK_IN("baggageCheckIn"),
    CHECKOUT("checkout"),
    OVERSIZE_BAGGAGE_CHECK_IN("oversizeBaggageCheckIn"),
    OVERSIZE_BAGGAGE_RECLAIM("oversizeBaggageReclaim"),
    BAGGAGE_RECLAIM("baggageReclaim"),
    LEFT_LUGGAGE_DEPOSIT("leftLuggageDeposit"),
    LEFT_LUGGAGE_RECLAIM("leftLuggageReclaim"),
    FIRSTCLASS_CHECKIN("firstclassCheckin"),
    SPECIAL_NEEDS_CHECKIN("specialNeedsCheckin"),
    BAGGAGE_SECURITY_CHECK("baggageSecurityCheck"),
    SECURITY_CHECK("securityCheck"),
    OUTGOING_PASSPORT_CONTROL("outgoingPassportControl"),
    INCOMING_PASSPORT_CONTROL("incomingPassportControl"),
    FASTTRACK_DEPARTURES("fasttrackDepartures"),
    FASTTRACK_ARRIVALS("fasttrackArrivals"),
    INCOMING_DUTY_FREE("incomingDutyFree"),
    OUTGOING_DUTY_FREE("outgoingDutyFree"),
    TAX_REFUNDS("taxRefunds"),
    OUTGOING_CUSTOMS("outgoingCustoms"),
    INCOMING_CUSTOMS("incomingCustoms"),
    WAIT_FOR_LIFT("waitForLift"),
    INGRESS("ingress"),
    EGRESS("egress"),
    QUEUE("queue"),
    VEHICLE_LOADING("vehicleLoading"),
    VEHICLE_UNLOADING("vehicleUnloading"),
    OTHER("other");
    private final String value;

    CheckProcessTypeEnumeration(String v) {
        value = v;
    }

    public static CheckProcessTypeEnumeration fromValue(String v) {
        for (CheckProcessTypeEnumeration c : CheckProcessTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    public String value() {
        return value;
    }

}
