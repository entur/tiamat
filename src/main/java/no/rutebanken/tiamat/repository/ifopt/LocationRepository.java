package no.rutebanken.tiamat.repository.ifopt;


import org.springframework.data.jpa.repository.JpaRepository;
import no.rutebanken.tiamat.model.Location;

public interface LocationRepository extends JpaRepository<Location, String>{
}
