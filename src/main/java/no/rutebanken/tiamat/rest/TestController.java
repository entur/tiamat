package no.rutebanken.tiamat.rest;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.rutebanken.tiamat.model.TestEntity;
import no.rutebanken.tiamat.repository.TestEntityRepository;

@RestController
public class TestController {
	
	@Autowired
	private TestEntityRepository testEntityRepository;
	
	private static final String template = "Hello, %s!";
	    private final AtomicLong counter = new AtomicLong();

	    @RequestMapping("/test")
	    public List<TestEntity> getEntities() {
	    	
	    	testEntityRepository.save(new TestEntity(counter.incrementAndGet() + " entity"));
	    	  	
	    	return testEntityRepository.findAll();
	    }
}
