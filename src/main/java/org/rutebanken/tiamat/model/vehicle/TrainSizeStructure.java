package org.rutebanken.tiamat.model.vehicle;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class TrainSizeStructure {
    private BigInteger numberOfCars;
    private TrainSizeEnumeration trainSizeType;
}
