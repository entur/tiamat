package org.rutebanken.tiamat.netexmapping.converters;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.Quays_RelStructure;
import org.rutebanken.tiamat.model.Quay;

import java.util.ArrayList;
import java.util.List;

public class QuaysConverter extends BidirectionalConverter<List<Quay>, Quays_RelStructure> {

    @Override
    public Quays_RelStructure convertTo(List<Quay> quays, Type<Quays_RelStructure> type) {
        Quays_RelStructure quays_relStructure = new Quays_RelStructure();

        quays.forEach(quay -> {
            quays_relStructure.getQuayRefOrQuay().add(
                    mapperFacade.map(quay, org.rutebanken.netex.model.Quay.class)
            );
        });
        return quays_relStructure;
    }

    @Override
    public List<Quay> convertFrom(Quays_RelStructure quays_relStructure, Type<List<Quay>> type) {
        List<Quay> quays = new ArrayList<>();
        if(quays_relStructure.getQuayRefOrQuay() != null) {
            quays_relStructure.getQuayRefOrQuay().stream()
                    .filter(object -> object instanceof org.rutebanken.netex.model.Quay)
                    .map(object -> ((org.rutebanken.netex.model.Quay) object))
                    .map(netexQuay -> mapperFacade.map(netexQuay, Quay.class))
                    .forEach(quay -> quays.add(quay));
        }

        return quays;
    }
}
