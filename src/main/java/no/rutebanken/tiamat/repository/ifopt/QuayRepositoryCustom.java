package no.rutebanken.tiamat.repository.ifopt;

import uk.org.netex.netex.Quay;

public interface QuayRepositoryCustom {

    Quay findQuayDetailed(String quayId);
}
