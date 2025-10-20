package org.rutebanken.tiamat.model.vehicle;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.rutebanken.tiamat.model.DataManagedObjectStructure;

import java.math.BigInteger;

@MappedSuperclass
@Getter
@Setter
public class PassengerCapacityStructure extends DataManagedObjectStructure {
    @Enumerated(EnumType.STRING)
    private FareClassEnumeration fareClass;
    private BigInteger totalCapacity;
    private BigInteger seatingCapacity;
    private BigInteger standingCapacity;
    private BigInteger specialPlaceCapacity;
    private BigInteger pushchairCapacity;
    private BigInteger wheelchairPlaceCapacity;
    private BigInteger pramPlaceCapacity;
    private BigInteger bicycleRackCapacity;

}
