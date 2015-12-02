package no.rutebanken.tiamat.ifopt.transfer.assembler;


import no.rutebanken.tiamat.ifopt.transfer.dto.QuayDTO;
import org.springframework.stereotype.Component;
import uk.org.netex.netex.Quay;

@Component
public class QuayAssembler {

    public QuayDTO assemble(Quay quay) {
        QuayDTO simpleQuayDTO = new QuayDTO();

        simpleQuayDTO.id = quay.getId();
        if(quay.getName() != null) simpleQuayDTO.name = quay.getName().getValue();
        return simpleQuayDTO;
    }

}