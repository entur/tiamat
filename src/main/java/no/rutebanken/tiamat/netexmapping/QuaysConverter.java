package no.rutebanken.tiamat.netexmapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;
import no.rutebanken.netex.model.Quays_RelStructure;
import no.rutebanken.tiamat.model.Quay;

import java.util.ArrayList;
import java.util.List;

public class QuaysConverter extends CustomConverter<List<Quay>, Quays_RelStructure> {
    @Override
    public Quays_RelStructure convert(List<Quay> quays, Type<? extends Quays_RelStructure> type) {
        Quays_RelStructure quays_relStructure = new Quays_RelStructure();

        quays.forEach(quay -> {
            quays_relStructure.getQuayRefOrQuay().add(
                    mapperFacade.map(quay, no.rutebanken.netex.model.Quay.class)
            );
        });
        return quays_relStructure;
    };
}
