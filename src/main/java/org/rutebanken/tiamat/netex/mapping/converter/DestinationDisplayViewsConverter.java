package org.rutebanken.tiamat.netex.mapping.converter;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.DestinationDisplayViews_RelStructure;
import org.rutebanken.tiamat.model.DestinationDisplayView;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DestinationDisplayViewsConverter extends BidirectionalConverter<List<DestinationDisplayView>, DestinationDisplayViews_RelStructure> {

    @Override
    public DestinationDisplayViews_RelStructure convertTo(List<DestinationDisplayView> destinationDisplayViews, Type<DestinationDisplayViews_RelStructure> type) {
        return null;
    }

    @Override
    public List<DestinationDisplayView> convertFrom(DestinationDisplayViews_RelStructure destinationDisplayViews_relStructure, Type<List<DestinationDisplayView>> type) {
        return null;
    }
}
