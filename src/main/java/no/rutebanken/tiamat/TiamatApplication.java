package no.rutebanken.tiamat;

import no.rutebanken.tiamat.model.example.ExampleEntity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import uk.org.netex.netex.StopPlace;

@Configuration
@EnableAutoConfiguration
@EntityScan(basePackageClasses={StopPlace.class, ExampleEntity.class})
@ComponentScan
public class TiamatApplication {

    public static void main(String[] args) {
        SpringApplication.run(TiamatApplication.class, args);
    }
}
