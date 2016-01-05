package no.rutebanken.tiamat.ifopt.transfer.disassembler;

import no.rutebanken.tiamat.ifopt.transfer.dto.QuayDTO;
import no.rutebanken.tiamat.repository.ifopt.QuayRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.org.netex.netex.MultilingualString;
import uk.org.netex.netex.Quay;
import uk.org.netex.netex.QuayTypeEnumeration;

@Component
public class QuayDisassembler {

    private static final Logger logger = LoggerFactory.getLogger(QuayDisassembler.class);

    private QuayRepository quayRepository;

    private SimplePointDisassembler simplePointDisassembler;

    @Autowired
    public QuayDisassembler(QuayRepository quayRepository, SimplePointDisassembler simplePointDisassembler) {
        this.quayRepository = quayRepository;
        this.simplePointDisassembler = simplePointDisassembler;
    }

    public Quay disassemble(QuayDTO quayDTO) {

        Quay quay;

        if(quayDTO == null) {
            logger.warn("The incoming quay is null. Returning null");
            return null;
        }

        if(quayDTO.id == null || quayDTO.id.isEmpty()) {
            logger.trace("The quay to disassemble has no Id, which means it's new.");
            quay = new Quay();
        } else {
             quay = quayRepository.findOne(quayDTO.id);
            if (quay == null) {
                logger.warn("There is no existing quay with id {}, returning null", quayDTO.id);
                return null;
            }
        }


        quay.setName(new MultilingualString(quayDTO.name, "no", ""));
        quay.setCentroid(simplePointDisassembler.disassemble(quayDTO.centroid));

        if(quayDTO.quayType != null && !quayDTO.quayType.isEmpty()) {
            logger.trace("Setting quay type on quay {} from string value {}", quay.getName(), quayDTO.quayType);
            quay.setQuayType(QuayTypeEnumeration.fromValue(quayDTO.quayType));
        }

        logger.trace("Set allAreasWheelchairAccessible {}", quayDTO.allAreasWheelchairAccessible);
        quay.setAllAreasWheelchairAccessible(quayDTO.allAreasWheelchairAccessible);

        logger.debug("Returning quay {}", quay.getName());
        return quay;
    }

}
