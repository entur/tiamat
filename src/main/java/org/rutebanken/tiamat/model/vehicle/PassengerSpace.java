package org.rutebanken.tiamat.model.vehicle;

import jakarta.persistence.Entity;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
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
public class PassengerSpace extends DeckSpace_VersionStructure {
    private PassengerSpaceTypeEnumeration passengerSpaceType;
    private Boolean standingAllowed;

    //TODO - TBD
//    protected PassengerSpots_RelStructure passengerSpots;
//    protected LuggageSpots_RelStructure luggageSpots;
//    protected PassengerVehicleSpots_RelStructure passengerVehicleSpots;
//    protected SpotAffinities_RelStructure spotAffinities;

}
