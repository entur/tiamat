package no.rutebanken.tiamat.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class TestEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue
    private Long id;
	
    @Column(nullable = false)
    private String name;
    
    public TestEntity() {}
    
	public TestEntity(String name) {
    	this.name = name;
    }
    
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


}
