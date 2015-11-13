package no.rutebanken.tiamat.model.example;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

@Entity
public class ExampleEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue
    private Long id;
	
    @Column(nullable = false)
    private String name;

	@OneToMany(fetch = FetchType.EAGER)
	private List<AnotherExampleEntity> references;

	@ElementCollection(fetch = FetchType.EAGER)
	public List<ExampleReferenceEntity> referenceObjects;

	public ExampleEntity() {}

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setReferences(List<AnotherExampleEntity> references)
	{
		this.references = references;
	}

	public List<AnotherExampleEntity> getReferences() {
		return references;
	}

}
