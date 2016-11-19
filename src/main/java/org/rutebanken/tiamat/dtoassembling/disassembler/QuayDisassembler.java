package org.rutebanken.tiamat.dtoassembling.disassembler;

import org.rutebanken.tiamat.dtoassembling.dto.QuayDto;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.rutebanken.tiamat.model.MultilingualStringEntity;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.QuayTypeEnumeration;

@Component
public class QuayDisassembler {

    private static final Logger logger = LoggerFactory.getLogger(QuayDisassembler.class);

    private QuayRepository quayRepository;

    private PointDisassembler pointDisassembler;

    @Autowired
    public QuayDisassembler(QuayRepository quayRepository, PointDisassembler pointDisassembler) {
        this.quayRepository = quayRepository;
        this.pointDisassembler = pointDisassembler;
    }

    public Quay disassemble(QuayDto quayDto) {

        Quay quay;

        if(quayDto == null) {
            logger.warn("The incoming quay is null. Returning null");
            return null;
        }

        if(quayDto.id == null || quayDto.id.isEmpty()) {
            logger.trace("The quay to disassemble has no Id, which means it's new.");
            quay = new Quay();
        } else {
             quay = quayRepository.findOne(Long.valueOf(quayDto.id));
            if (quay == null) {
                logger.warn("There is no existing quay with id {}, returning null", quayDto.id);
                return null;
            }
        }


        quay.setName(new EmbeddableMultilingualString(quayDto.name, "no"));
        quay.setDescription(new EmbeddableMultilingualString(quayDto.description, "no"));
        quay.setCentroid(pointDisassembler.disassemble(quayDto.centroid));

        logger.trace("Set allAreasWheelchairAccessible {}", quayDto.allAreasWheelchairAccessible);
        quay.setAllAreasWheelchairAccessible(quayDto.allAreasWheelchairAccessible);

        logger.debug("Returning quay {}", quay.getName());
        return quay;
    }

}
