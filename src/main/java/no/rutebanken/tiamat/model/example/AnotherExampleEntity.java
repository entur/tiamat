package no.rutebanken.tiamat.model.example;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class AnotherExampleEntity {
    @Id
    @GeneratedValue
    public long id;
}
