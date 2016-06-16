package no.rutebanken.tiamat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import no.rutebanken.tiamat.model.StopPlace;

@Configuration
@EnableAutoConfiguration
@EntityScan(basePackageClasses={StopPlace.class})
@ComponentScan
public class TiamatApplication {

    public static void main(String[] args) {
        // Server webServer = Server.createWebServer("-web", "-webAllowOthers", "-webPort","9876").start();
        // Server server = Server.createTcpServer("-tcp","-tcpAllowOthers","-tcpPort","9875").start();

        SpringApplication.run(TiamatApplication.class, args);
    }
}
