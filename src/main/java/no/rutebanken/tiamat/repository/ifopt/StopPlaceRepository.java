package no.rutebanken.tiamat.repository.ifopt;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.org.netex.netex.StopPlace;

public interface StopPlaceRepository extends JpaRepository<StopPlace, String>, StopPlaceRepositoryCustom {

}

