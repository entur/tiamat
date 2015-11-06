//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.11.05 at 07:41:01 PM CET 
//


package uk.org.netex.netex;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;


/**
 * Type for a list of LOCAL SERVICEs.
 * 
 * <p>Java class for explicitEquipments_RelStructure complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="explicitEquipments_RelStructure">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.netex.org.uk/netex}containmentAggregationStructure">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element ref="{http://www.netex.org.uk/netex}InstalledEquipmentRef"/>
 *         &lt;element ref="{http://www.netex.org.uk/netex}InstalledEquipment"/>
 *         &lt;element ref="{http://www.netex.org.uk/netex}LocalServiceRef"/>
 *         &lt;element ref="{http://www.netex.org.uk/netex}LocalService"/>
 *         &lt;element ref="{http://www.netex.org.uk/netex}OtherPlaceEquipment"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "explicitEquipments_RelStructure", propOrder = {
    "installedEquipmentRefOrInstalledEquipmentOrLocalServiceRef"
})
public class ExplicitEquipments_RelStructure
    extends ContainmentAggregationStructure
{

    @XmlElementRefs({
        @XmlElementRef(name = "LocalServiceRef", namespace = "http://www.netex.org.uk/netex", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "OtherPlaceEquipment", namespace = "http://www.netex.org.uk/netex", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "InstalledEquipment", namespace = "http://www.netex.org.uk/netex", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "LocalService", namespace = "http://www.netex.org.uk/netex", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "InstalledEquipmentRef", namespace = "http://www.netex.org.uk/netex", type = JAXBElement.class, required = false)
    })
    protected List<JAXBElement<?>> installedEquipmentRefOrInstalledEquipmentOrLocalServiceRef;

    /**
     * Gets the value of the installedEquipmentRefOrInstalledEquipmentOrLocalServiceRef property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the installedEquipmentRefOrInstalledEquipmentOrLocalServiceRef property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInstalledEquipmentRefOrInstalledEquipmentOrLocalServiceRef().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link HireServiceRefStructure }{@code >}
     * {@link JAXBElement }{@code <}{@link PlaceSign }{@code >}
     * {@link JAXBElement }{@code <}{@link SanitaryEquipment }{@code >}
     * {@link JAXBElement }{@code <}{@link TicketValidatorEquipment }{@code >}
     * {@link JAXBElement }{@code <}{@link ActualVehicleEquipment_VersionStructure }{@code >}
     * {@link JAXBElement }{@code <}{@link CommunicationServiceRefStructure }{@code >}
     * {@link JAXBElement }{@code <}{@link QueueingEquipment }{@code >}
     * {@link JAXBElement }{@code <}{@link InstalledEquipmentRefStructure }{@code >}
     * {@link JAXBElement }{@code <}{@link EntranceEquipment }{@code >}
     * {@link JAXBElement }{@code <}{@link InstalledEquipment_VersionStructure }{@code >}
     * {@link JAXBElement }{@code <}{@link PassengerEquipment_VersionStructure }{@code >}
     * {@link JAXBElement }{@code <}{@link AccessEquipment_VersionStructure }{@code >}
     * {@link JAXBElement }{@code <}{@link GeneralSign }{@code >}
     * {@link JAXBElement }{@code <}{@link StaircaseEquipment }{@code >}
     * {@link JAXBElement }{@code <}{@link RoughSurface }{@code >}
     * {@link JAXBElement }{@code <}{@link CommunicationService }{@code >}
     * {@link JAXBElement }{@code <}{@link RetailServiceRefStructure }{@code >}
     * {@link JAXBElement }{@code <}{@link TicketingService }{@code >}
     * {@link JAXBElement }{@code <}{@link AccessVehicleEquipmentRefStructure }{@code >}
     * {@link JAXBElement }{@code <}{@link HireService }{@code >}
     * {@link JAXBElement }{@code <}{@link CustomerServiceRefStructure }{@code >}
     * {@link JAXBElement }{@code <}{@link SiteEquipment_VersionStructure }{@code >}
     * {@link JAXBElement }{@code <}{@link EscalatorEquipment }{@code >}
     * {@link JAXBElement }{@code <}{@link CateringServiceRefStructure }{@code >}
     * {@link JAXBElement }{@code <}{@link CateringService }{@code >}
     * {@link JAXBElement }{@code <}{@link PassengerSafetyEquipmentRefStructure }{@code >}
     * {@link JAXBElement }{@code <}{@link TrolleyStandEquipment }{@code >}
     * {@link JAXBElement }{@code <}{@link AssistanceBookingService }{@code >}
     * {@link JAXBElement }{@code <}{@link VehicleChargingEquipment }{@code >}
     * {@link JAXBElement }{@code <}{@link LocalService_VersionStructure }{@code >}
     * {@link JAXBElement }{@code <}{@link CycleStorageEquipment }{@code >}
     * {@link JAXBElement }{@code <}{@link InstalledEquipmentRefStructure }{@code >}
     * {@link JAXBElement }{@code <}{@link ShelterEquipment }{@code >}
     * {@link JAXBElement }{@code <}{@link PlaceEquipment_VersionStructure }{@code >}
     * {@link JAXBElement }{@code <}{@link HeadingSign }{@code >}
     * {@link JAXBElement }{@code <}{@link RubbishDisposalEquipment }{@code >}
     * {@link JAXBElement }{@code <}{@link RubbishDisposalEquipmentRefStructure }{@code >}
     * {@link JAXBElement }{@code <}{@link AssistanceServiceRefStructure }{@code >}
     * {@link JAXBElement }{@code <}{@link InstalledEquipmentRefStructure }{@code >}
     * {@link JAXBElement }{@code <}{@link MeetingPointServiceRefStructure }{@code >}
     * {@link JAXBElement }{@code <}{@link MoneyServiceRefStructure }{@code >}
     * {@link JAXBElement }{@code <}{@link CrossingEquipment }{@code >}
     * {@link JAXBElement }{@code <}{@link HelpPointEquipmentRefStructure }{@code >}
     * {@link JAXBElement }{@code <}{@link SanitaryEquipmentRefStructure }{@code >}
     * {@link JAXBElement }{@code <}{@link LuggageService }{@code >}
     * {@link JAXBElement }{@code <}{@link PassengerInformationEquipmentRefStructure }{@code >}
     * {@link JAXBElement }{@code <}{@link LostPropertyService }{@code >}
     * {@link JAXBElement }{@code <}{@link LuggageLockerEquipment }{@code >}
     * {@link JAXBElement }{@code <}{@link PlaceEquipment_VersionStructure }{@code >}
     * {@link JAXBElement }{@code <}{@link PassengerSafetyEquipment }{@code >}
     * {@link JAXBElement }{@code <}{@link MoneyService }{@code >}
     * {@link JAXBElement }{@code <}{@link ComplaintsServiceRefStructure }{@code >}
     * {@link JAXBElement }{@code <}{@link HelpPointEquipment }{@code >}
     * {@link JAXBElement }{@code <}{@link LuggageServiceRefStructure }{@code >}
     * {@link JAXBElement }{@code <}{@link SignEquipment_VersionStructure }{@code >}
     * {@link JAXBElement }{@code <}{@link TravelatorEquipment }{@code >}
     * {@link JAXBElement }{@code <}{@link SeatingEquipment }{@code >}
     * {@link JAXBElement }{@code <}{@link LeftLuggageServiceRefStructure }{@code >}
     * {@link JAXBElement }{@code <}{@link PlaceLighting }{@code >}
     * {@link JAXBElement }{@code <}{@link TicketingServiceRefStructure }{@code >}
     * {@link JAXBElement }{@code <}{@link WaitingEquipment }{@code >}
     * {@link JAXBElement }{@code <}{@link ComplaintsService }{@code >}
     * {@link JAXBElement }{@code <}{@link TicketingEquipment }{@code >}
     * {@link JAXBElement }{@code <}{@link InstalledEquipmentRefStructure }{@code >}
     * {@link JAXBElement }{@code <}{@link AssistanceBookingServiceRefStructure }{@code >}
     * {@link JAXBElement }{@code <}{@link WaitingRoomEquipment }{@code >}
     * {@link JAXBElement }{@code <}{@link StairEquipment }{@code >}
     * {@link JAXBElement }{@code <}{@link MeetingPointService }{@code >}
     * {@link JAXBElement }{@code <}{@link CustomerService_VersionStructure }{@code >}
     * {@link JAXBElement }{@code <}{@link RetailService }{@code >}
     * {@link JAXBElement }{@code <}{@link LocalServiceRefStructure }{@code >}
     * {@link JAXBElement }{@code <}{@link LostPropertyServiceRefStructure }{@code >}
     * {@link JAXBElement }{@code <}{@link LiftEquipment }{@code >}
     * {@link JAXBElement }{@code <}{@link AssistanceService }{@code >}
     * {@link JAXBElement }{@code <}{@link AccessVehicleEquipment }{@code >}
     * {@link JAXBElement }{@code <}{@link RampEquipment }{@code >}
     * {@link JAXBElement }{@code <}{@link PassengerInformationEquipment }{@code >}
     * {@link JAXBElement }{@code <}{@link LeftLuggageService }{@code >}
     * {@link JAXBElement }{@code <}{@link WheelchairVehicleEquipment }{@code >}
     * 
     * 
     */
    public List<JAXBElement<?>> getInstalledEquipmentRefOrInstalledEquipmentOrLocalServiceRef() {
        if (installedEquipmentRefOrInstalledEquipmentOrLocalServiceRef == null) {
            installedEquipmentRefOrInstalledEquipmentOrLocalServiceRef = new ArrayList<JAXBElement<?>>();
        }
        return this.installedEquipmentRefOrInstalledEquipmentOrLocalServiceRef;
    }

}
