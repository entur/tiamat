package no.rutebanken.tiamat.repository.example;

import org.springframework.data.jpa.repository.JpaRepository;

import no.rutebanken.tiamat.model.example.ExampleEntity;

public interface ExampleEntityRepository extends JpaRepository<ExampleEntity, Long> {
	
}

