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

import javax.xml.bind.JAXBElement;


public class AssistanceBookingService_VersionStructure
        extends LocalService_VersionStructure {

    protected AssistanceAvailabilityEnumeration assistanceAvailability;
    protected Boolean wheelchairBookingRequired;
    protected ContactStructure bookingContact;
    protected BookingArrangementsStructure bookingArrangements;
    protected AllModesEnumeration vehicleMode;
    protected JAXBElement<? extends OrganisationRefStructure> transportOrganisationRef;
    protected VersionOfObjectRefStructure bookedObjectRef;
    protected NoticeAssignments_RelStructure noticeAssignmenrts;

    public AssistanceAvailabilityEnumeration getAssistanceAvailability() {
        return assistanceAvailability;
    }

    public void setAssistanceAvailability(AssistanceAvailabilityEnumeration value) {
        this.assistanceAvailability = value;
    }

    public Boolean isWheelchairBookingRequired() {
        return wheelchairBookingRequired;
    }

    public void setWheelchairBookingRequired(Boolean value) {
        this.wheelchairBookingRequired = value;
    }

    public ContactStructure getBookingContact() {
        return bookingContact;
    }

    public void setBookingContact(ContactStructure value) {
        this.bookingContact = value;
    }

    public BookingArrangementsStructure getBookingArrangements() {
        return bookingArrangements;
    }

    public void setBookingArrangements(BookingArrangementsStructure value) {
        this.bookingArrangements = value;
    }

    public AllModesEnumeration getVehicleMode() {
        return vehicleMode;
    }

    public void setVehicleMode(AllModesEnumeration value) {
        this.vehicleMode = value;
    }

    public JAXBElement<? extends OrganisationRefStructure> getTransportOrganisationRef() {
        return transportOrganisationRef;
    }

    public void setTransportOrganisationRef(JAXBElement<? extends OrganisationRefStructure> value) {
        this.transportOrganisationRef = value;
    }

    public VersionOfObjectRefStructure getBookedObjectRef() {
        return bookedObjectRef;
    }

    public void setBookedObjectRef(VersionOfObjectRefStructure value) {
        this.bookedObjectRef = value;
    }

    public NoticeAssignments_RelStructure getNoticeAssignmenrts() {
        return noticeAssignmenrts;
    }

    public void setNoticeAssignmenrts(NoticeAssignments_RelStructure value) {
        this.noticeAssignmenrts = value;
    }

}
