package org.rutebanken.tiamat.dtoassembling.assembler;


import org.rutebanken.tiamat.dtoassembling.dto.QuayDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.rutebanken.tiamat.model.Quay;

@Component
public class QuayAssembler {

    private final PointAssembler pointAssembler;

    @Autowired
    public QuayAssembler(PointAssembler pointAssembler) {
        this.pointAssembler = pointAssembler;
    }

    public QuayDto assemble(Quay quay) {
        QuayDto quayDto = new QuayDto();

        quayDto.id = String.valueOf(quay.getId());
        if(quay.getName() != null) quayDto.name = quay.getName().getValue();

        quayDto.centroid = pointAssembler.assemble(quay.getCentroid());

        if(quay.isAllAreasWheelchairAccessible() != null) {
            quayDto.allAreasWheelchairAccessible = quay.isAllAreasWheelchairAccessible();
        }

        if (quay.getDescription() != null) {
            quayDto.description = quay.getDescription().getValue();
        }

        return quayDto;
    }

}

