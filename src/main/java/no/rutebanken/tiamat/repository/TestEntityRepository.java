package no.rutebanken.tiamat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import no.rutebanken.tiamat.model.TestEntity;

public interface TestEntityRepository extends JpaRepository<TestEntity, Long> {
	
}

