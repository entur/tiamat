package no.rutebanken.tiamat.repository.ifopt;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import uk.org.netex.netex.Quay;

public interface QuayRepositoryCustom {

    Quay findQuayDetailed(String quayId);
    
    Page<Quay> findQuaysWithin(double xMin, double yMin, double xMax, double yMax, Pageable pageable);
}
