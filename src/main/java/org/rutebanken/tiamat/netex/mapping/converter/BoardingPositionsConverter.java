package org.rutebanken.tiamat.netex.mapping.converter;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.BoardingPositions_RelStructure;
import org.rutebanken.tiamat.model.BoardingPosition;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BoardingPositionsConverter extends BidirectionalConverter<List<BoardingPosition>, org.rutebanken.netex.model.BoardingPositions_RelStructure> {
    @Override
    public BoardingPositions_RelStructure convertTo(List<BoardingPosition> boardingPositions, Type<BoardingPositions_RelStructure> type) {
        return null;
    }

    @Override
    public List<BoardingPosition> convertFrom(BoardingPositions_RelStructure boardingPositions_relStructure, Type<List<BoardingPosition>> type) {
        return null;
    }
}
