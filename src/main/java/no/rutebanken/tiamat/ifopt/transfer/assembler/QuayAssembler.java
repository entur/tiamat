package no.rutebanken.tiamat.ifopt.transfer.assembler;


import no.rutebanken.tiamat.ifopt.transfer.dto.QuayDTO;
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

    public QuayDTO assemble(Quay quay) {
        QuayDTO quayDTO = new QuayDTO();

        quayDTO.id = quay.getId();
        if(quay.getName() != null) quayDTO.name = quay.getName().getValue();

        quayDTO.centroid = simplePointAssembler.assemble(quay.getCentroid());

        if(quay.getQuayType() != null) {
            quayDTO.quayType = quay.getQuayType().value();
        }
        if(quay.isAllAreasWheelchairAccessible() != null) {
            quayDTO.allAreasWheelchairAccessible = quay.isAllAreasWheelchairAccessible();
        }
        return quayDTO;
    }

}