package no.rutebanken.tiamat.dtoassembling.assembler;


import no.rutebanken.tiamat.dtoassembling.dto.QuayDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import no.rutebanken.tiamat.model.Quay;

@Component
public class QuayAssembler {

    private final SimplePointAssembler simplePointAssembler;

    @Autowired
    public QuayAssembler(SimplePointAssembler simplePointAssembler) {
        this.simplePointAssembler = simplePointAssembler;
    }

    public QuayDto assemble(Quay quay) {
        QuayDto quayDto = new QuayDto();

        quayDto.id = quay.getId();
        if(quay.getName() != null) quayDto.name = quay.getName().getValue();

        quayDto.centroid = simplePointAssembler.assemble(quay.getCentroid());

        if(quay.getQuayType() != null) {
            quayDto.quayType = quay.getQuayType().value();
        }
        if(quay.isAllAreasWheelchairAccessible() != null) {
            quayDto.allAreasWheelchairAccessible = quay.isAllAreasWheelchairAccessible();
        }

        if (quay.getDescription() != null) {
            quayDto.description = quay.getDescription().getValue();
        }

        return quayDto;
    }

}

