package org.rutebanken.tiamat.model.vehicle;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class PassengerSpot extends LocatableSpot_VersionStructure {
    
    @Enumerated(EnumType.STRING)
    private TableTypeEnumeration tableType;
    private Boolean hasArmrest;
    private BigDecimal legSpace;
    private Boolean hasPower;
    private Boolean isByWindow;
    private Boolean isByAisle;
    private Boolean isBetweenSeats;
    private Boolean isInFrontRow;
    private Boolean isInEndRow;
    private Boolean isFacingWindow;
    private Boolean isFacingAisle;

}
