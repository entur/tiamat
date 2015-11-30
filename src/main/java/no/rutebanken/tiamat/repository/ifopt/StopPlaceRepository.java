package no.rutebanken.tiamat.repository.ifopt;

import org.springframework.data.repository.PagingAndSortingRepository;
import uk.org.netex.netex.StopPlace;

public interface StopPlaceRepository extends PagingAndSortingRepository<StopPlace, String>, StopPlaceRepositoryCustom {

}

