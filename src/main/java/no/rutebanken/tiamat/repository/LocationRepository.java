package no.rutebanken.tiamat.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import no.rutebanken.tiamat.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long>{
}
