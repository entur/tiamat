package org.rutebanken.tiamat.model.vehicle;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import net.opengis.gml._3.MultiSurfaceType;
import net.opengis.gml._3.PolygonType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.rutebanken.netex.OmitNullsToStringStyle;

@Entity
@Getter
@Setter
public class PassengerEntrance extends DeckComponent_VersionStructure {
    @Enumerated(EnumType.STRING)
    private VehicleSideEnumeration vehicleSide;

    private BigDecimal distanceFromFront;
    private BigInteger sequenceFromFront;
    private BigDecimal heightFromGround;

    @Enumerated(EnumType.STRING)
    private DeckEntranceTypeEnumeration deckEntranceType;
    private Boolean hasDoor;
    private Boolean isAutomatic;
    private Boolean isEmergencyExit;

    //    private SensorsInEntrance_RelStructure sensorsInEntrance;
//    private TypeOfDeckEntranceUsageRefStructure typeOfDeckEntranceUsageRef;
}
