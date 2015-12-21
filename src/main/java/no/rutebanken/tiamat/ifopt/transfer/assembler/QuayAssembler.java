package no.rutebanken.tiamat.ifopt.transfer.assembler;


import no.rutebanken.tiamat.ifopt.transfer.dto.QuayDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.org.netex.netex.Quay;

@Component
public class QuayAssembler {

    @Autowired
    private SimplePointAssembler simplePointAssembler;

    public QuayDTO assemble(Quay quay) {
        QuayDTO quayDTO = new QuayDTO();

        quayDTO.id = quay.getId();
        if(quay.getName() != null) quayDTO.name = quay.getName().getValue();

        quayDTO.centroid = simplePointAssembler.assemble(quay.getCentroid());
        return quayDTO;
    }

}