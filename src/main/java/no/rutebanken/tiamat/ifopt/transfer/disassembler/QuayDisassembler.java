package no.rutebanken.tiamat.ifopt.transfer.disassembler;

import no.rutebanken.tiamat.ifopt.transfer.dto.QuayDTO;
import no.rutebanken.tiamat.repository.ifopt.QuayRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.org.netex.netex.MultilingualString;
import uk.org.netex.netex.Quay;

@Component
public class QuayDisassembler {

    private static final Logger logger = LoggerFactory.getLogger(QuayDisassembler.class);

    @Autowired
    private QuayRepository quayRepository;

    @Autowired
    private SimplePointDisassembler simplePointDisassembler;

    public Quay disassemble(QuayDTO quayDTO) {

        Quay quay;

        if(quayDTO == null) {
            logger.warn("The incoming quay is null. Returning null");
            return null;
        }

        if(quayDTO.id != null || quayDTO.id.isEmpty()) {
            logger.trace("The quay to disassemble has no Id, which means it's new.");
            quay = new Quay();
        } else {
             quay = quayRepository.findOne(quayDTO.id);
            if (quay == null) {
                logger.warn("There is no existing quay with id {}, returning null", quayDTO.id);
                return null;
            }
        }


        quay.setName(new MultilingualString(quayDTO.shortName, "no", ""));
        quay.setCentroid(simplePointDisassembler.disassemble(quayDTO.centroid));

        return quay;
    }

}
