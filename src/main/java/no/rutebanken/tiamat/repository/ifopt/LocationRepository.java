package no.rutebanken.tiamat.repository.ifopt;


import org.springframework.data.jpa.repository.JpaRepository;
import uk.org.netex.netex.Location;

public interface LocationRepository extends JpaRepository<Location, String>{
}
