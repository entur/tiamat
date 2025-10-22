package org.rutebanken.tiamat.netex.mapping.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.rutebanken.netex.model.Deck;

public class DeckMapper extends CustomMapper<Deck, org.rutebanken.tiamat.model.vehicle.Deck> {

    @Override
    public void mapAtoB(Deck netexDeck, org.rutebanken.tiamat.model.vehicle.Deck tiamatDeck, MappingContext context) {
        super.mapAtoB(netexDeck, tiamatDeck, context);
    }

    @Override
    public void mapBtoA(org.rutebanken.tiamat.model.vehicle.Deck tiamatDeck, Deck netexDeck, MappingContext context) {
        super.mapBtoA(tiamatDeck, netexDeck, context);

        if (tiamatDeck.getName() != null) {
            netexDeck.getName().withContent(tiamatDeck.getName().getValue());
        }

        if (tiamatDeck.getDescription() != null) {
            netexDeck.getDescription().withContent(tiamatDeck.getDescription().getValue());
        }

        if (tiamatDeck.getLabel() != null) {
            netexDeck.getLabel().withContent(tiamatDeck.getLabel().getValue());
        }
    }
}
