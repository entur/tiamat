package org.rutebanken.tiamat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.rutebanken.tiamat.model.StopPlace;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EntityScan(basePackageClasses={StopPlace.class})
@ComponentScan
public class TiamatApplication {

    public static void main(String[] args) {
        // Server webServer = Server.createWebServer("-web", "-webAllowOthers", "-webPort","9876").start();
        // Server server = Server.createTcpServer("-tcp","-tcpAllowOthers","-tcpPort","9875").start();

        SpringApplication.run(TiamatApplication.class, args);
    }
}
