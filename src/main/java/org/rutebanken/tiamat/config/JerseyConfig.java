/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.config;

import com.google.common.collect.Sets;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.rutebanken.tiamat.rest.dto.*;
import org.rutebanken.tiamat.rest.exception.GeneralExceptionMapper;
import org.rutebanken.tiamat.rest.graphql.GraphQLResource;
import org.rutebanken.tiamat.rest.health.HealthResource;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ApplicationPath;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class JerseyConfig {

    public static final String SERVICES_PATH = "/services";

    public static final String SERVICES_ADMIN_PATH = SERVICES_PATH + "/admin";

    public static final String SERVICES_STOP_PLACE_PATH = SERVICES_PATH + "/stop_places";

    public static final String SERVICES_HEALTH_PATH = "/health";

    private static final String PUBLIC_SWAGGER_SCANNER_ID = "public-scanner";
    private static final String PUBLIC_SWAGGER_CONFIG_ID = "public-swagger-doc";

    private static final String ADMIN_SWAGGER_SCANNER_ID = "admin-scanner";
    private static final String ADMIN_SWAGGER_CONFIG_ID = "admin-swagger-doc";

    private static final String HEALTH_SWAGGER_SCANNER_ID = "health-scanner";
    private static final String HEALTH_SWAGGER_CONFIG_ID = "health-swagger-doc";

    @Bean
    public ServletRegistrationBean publicJersey() {

        Set<Class<?>> publicResources = new HashSet<>();
        publicResources.add(DtoStopPlaceResource.class);
        publicResources.add(DtoQuayResource.class);
        publicResources.add(ImportResource.class);
        publicResources.add(AsyncExportResource.class);
        publicResources.add(ExportResource.class);
        publicResources.add(GraphQLResource.class);
        publicResources.add(GeneralExceptionMapper.class);

        publicResources.add(ApiListingResource.class);
        publicResources.add(SwaggerSerializers.class);

        ResourceConfig resourceConfig = new ResourceConfig(publicResources);
        ServletRegistrationBean publicServicesJersey = new ServletRegistrationBean(new ServletContainer(resourceConfig));

        BeanConfig config = new BeanConfig();
        config.setConfigId(PUBLIC_SWAGGER_CONFIG_ID);
        config.setTitle("Tiamat Public API");
        config.setVersion("v1");
        config.setSchemes(new String[]{"http", "https"});
        config.setBasePath(SERVICES_STOP_PLACE_PATH);
        config.setResourcePackage("org.rutebanken.tiamat");
        config.setPrettyPrint(true);
        config.setScan(true);

        publicServicesJersey.addUrlMappings(SERVICES_STOP_PLACE_PATH + "/*");
        publicServicesJersey.setName("PublicJersey");

        publicServicesJersey.setLoadOnStartup(0);
        publicServicesJersey.getInitParameters().put("swagger.scanner.id", PUBLIC_SWAGGER_SCANNER_ID);
        publicServicesJersey.getInitParameters().put("swagger.config.id", PUBLIC_SWAGGER_CONFIG_ID);
        return publicServicesJersey;
    }

    @Bean
    public ServletRegistrationBean healthJersey() {

        Set<Class<?>> resources = new HashSet<>();

        resources.add(HealthResource.class);

        resources.add(ApiListingResource.class);
        resources.add(SwaggerSerializers.class);

        resources.add(GeneralExceptionMapper.class);

        ResourceConfig resourceConfig = new ResourceConfig(resources);
        ServletRegistrationBean healthServicesJersey = new ServletRegistrationBean(new ServletContainer(resourceConfig));

        BeanConfig config = new BeanConfig();
        config.setConfigId(HEALTH_SWAGGER_CONFIG_ID);
        config.setTitle("Tiamat Health API");
        config.setVersion("v1");
        config.setSchemes(new String[]{"http", "https"});
        config.setBasePath(SERVICES_HEALTH_PATH);
        config.setResourcePackage("org.rutebanken.tiamat");
        config.setPrettyPrint(true);
        config.setScan(true);

        healthServicesJersey.addUrlMappings(SERVICES_HEALTH_PATH + "/*");
        healthServicesJersey.setName("HealthJersey");

        healthServicesJersey.getInitParameters().put("swagger.scanner.id", HEALTH_SWAGGER_SCANNER_ID);
        healthServicesJersey.getInitParameters().put("swagger.config.id", HEALTH_SWAGGER_CONFIG_ID);
        healthServicesJersey.setLoadOnStartup(0);
        return healthServicesJersey;
    }

    @Bean
    public ServletRegistrationBean adminJersey() {

        Set<Class<?>> adminResources = new HashSet<>();
        adminResources.add(DtoJbvCodeMappingResource.class);
        adminResources.add(RestoringImportResource.class);
        adminResources.add(GeneralExceptionMapper.class);

        adminResources.add(ApiListingResource.class);
        adminResources.add(SwaggerSerializers.class);

        ResourceConfig resourceConfig = new ResourceConfig(adminResources);

        ServletRegistrationBean adminServicesJersey = new ServletRegistrationBean(new ServletContainer(resourceConfig));

        BeanConfig config = new BeanConfig();
        config.setConfigId(ADMIN_SWAGGER_CONFIG_ID);
        config.setTitle("Tiamat Admin API");
        config.setVersion("v1");
        config.setSchemes(new String[]{"http", "https"});
        config.setBasePath(SERVICES_ADMIN_PATH);
        config.setResourcePackage("org.rutebanken.tiamat");
        config.setPrettyPrint(true);
        config.setScan(true);

        adminServicesJersey.addUrlMappings(SERVICES_ADMIN_PATH + "/*");
        adminServicesJersey.setName("AdminJersey");

        adminServicesJersey.setLoadOnStartup(0);
        adminServicesJersey.getInitParameters().put("swagger.scanner.id", ADMIN_SWAGGER_SCANNER_ID);
        adminServicesJersey.getInitParameters().put("swagger.config.id", ADMIN_SWAGGER_CONFIG_ID);
        return adminServicesJersey;
    }

}
