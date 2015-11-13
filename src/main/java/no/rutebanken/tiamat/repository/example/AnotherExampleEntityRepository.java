package no.rutebanken.tiamat.repository.example;

import no.rutebanken.tiamat.model.example.AnotherExampleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnotherExampleEntityRepository extends JpaRepository<AnotherExampleEntity, Long> {
	
}

