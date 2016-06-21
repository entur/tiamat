package no.rutebanken.tiamat.netexmapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import no.rutebanken.netex.model.Levels_RelStructure;
import no.rutebanken.tiamat.model.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LevelsConverter extends BidirectionalConverter<List<Level>, Levels_RelStructure> {
    private static final Logger logger = LoggerFactory.getLogger(LevelsConverter.class);

    @Override
    public Levels_RelStructure convertTo(List<Level> levels, Type<Levels_RelStructure> type) {
        logger.debug("Mapping {} levels into levels_RelStructure", levels.size());

        Levels_RelStructure levels_relStructure = new Levels_RelStructure();

        levels.forEach(level -> {
                    levels_relStructure.getLevelRefOrLevel().add(
                            mapperFacade.map(level, no.rutebanken.netex.model.Level.class)
                    );
                }
        );
        return levels_relStructure;

    }

    @Override
    public List<Level> convertFrom(Levels_RelStructure levels_relStructure, Type<List<Level>> type) {
        return null;
    }
}
