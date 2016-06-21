package no.rutebanken.tiamat.netexmapping.converters;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import no.rutebanken.netex.model.Quays_RelStructure;
import no.rutebanken.tiamat.model.Quay;

import java.util.ArrayList;
import java.util.List;

public class QuaysConverter extends BidirectionalConverter<List<Quay>, Quays_RelStructure> {

    @Override
    public Quays_RelStructure convertTo(List<Quay> quays, Type<Quays_RelStructure> type) {
        Quays_RelStructure quays_relStructure = new Quays_RelStructure();

        quays.forEach(quay -> {
            quays_relStructure.getQuayRefOrQuay().add(
                    mapperFacade.map(quay, no.rutebanken.netex.model.Quay.class)
            );
        });
        return quays_relStructure;
    }

    @Override
    public List<Quay> convertFrom(Quays_RelStructure quays_relStructure, Type<List<Quay>> type) {
        return null;
    }
}
