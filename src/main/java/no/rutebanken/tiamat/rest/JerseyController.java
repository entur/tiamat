package no.rutebanken.tiamat.rest;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import no.rutebanken.tiamat.model.TestEntity;
import no.rutebanken.tiamat.repository.TestEntityRepository;

@Component
@Path("/jersey")
public class JerseyController {

	@Autowired
	private TestEntityRepository testEntityRepository;
	
    private final AtomicLong counter = new AtomicLong();

	@GET
	@Produces("application/json")
	public List<TestEntity> getEntities() {
    	
    	testEntityRepository.save(new TestEntity(counter.incrementAndGet() + " entity"));
    	  	
    	return testEntityRepository.findAll();
    }
}
