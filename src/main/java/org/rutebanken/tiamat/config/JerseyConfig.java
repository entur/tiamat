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


import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.rutebanken.tiamat.filter.LoggingFilter;
import org.rutebanken.tiamat.jersey.JerseyJava8TimeConverterProvider;
import org.rutebanken.tiamat.rest.dto.DtoJbvCodeMappingResource;
import org.rutebanken.tiamat.rest.dto.DtoQuayResource;
import org.rutebanken.tiamat.rest.dto.DtoStopPlaceResource;
import org.rutebanken.tiamat.rest.exception.ErrorResponseEntityMessageBodyWriter;
import org.rutebanken.tiamat.rest.exception.GeneralExceptionMapper;
import org.rutebanken.tiamat.rest.graphql.GraphQLResource;
import org.rutebanken.tiamat.rest.health.HealthResource;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.AsyncExportResource;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.ExportResource;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.ImportResource;
import org.rutebanken.tiamat.rest.promethouse.PrometheusResource;
import org.rutebanken.tiamat.rest.write.controllers.JobControllerImpl;
import org.rutebanken.tiamat.rest.write.controllers.StopPlaceControllerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class JerseyConfig {

    /**
     * Client ID header.
     * Is used for identifiying clients calling our API
     */
    public static final String ET_CLIENT_ID_HEADER = "ET-Client-ID";

    /**
     * Client Name header.
     * Is used for getting the name of clients calling our API.
     */
    public static final String ET_CLIENT_NAME_HEADER = "ET-Client-Name";

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

    private static final String PROMETHEUS_SWAGGER_SCANNER_ID = "prometheus-scanner";
    private static final String PROMETHEUS_SWAGGER_CONFIG_ID = "prometheus-swagger-doc";

    @Bean
    public ServletRegistrationBean publicJersey() {

        Set<Class<?>> publicResources = new HashSet<>();
        publicResources.add(DtoStopPlaceResource.class);
        publicResources.add(DtoQuayResource.class);
        publicResources.add(ImportResource.class);
        publicResources.add(AsyncExportResource.class);
        publicResources.add(ExportResource.class);
        publicResources.add(GraphQLResource.class);
        publicResources.add(StopPlaceControllerImpl.class);
        publicResources.add(JobControllerImpl.class);

        publicResources.add(GeneralExceptionMapper.class);
        publicResources.add(ErrorResponseEntityMessageBodyWriter.class);

        publicResources.add(OpenApiResource.class);

        ResourceConfig resourceConfig = new ResourceConfig(publicResources);
        resourceConfig.register(JerseyJava8TimeConverterProvider.class);
        ServletRegistrationBean publicServicesJersey = new ServletRegistrationBean(new ServletContainer(resourceConfig));


        publicServicesJersey.addUrlMappings(SERVICES_STOP_PLACE_PATH + "/*");

        publicServicesJersey.setName("PublicJersey");

        publicServicesJersey.setLoadOnStartup(0);
        publicServicesJersey.getInitParameters().put("swagger.scanner.id", PUBLIC_SWAGGER_SCANNER_ID);
        publicServicesJersey.getInitParameters().put("swagger.config.id", PUBLIC_SWAGGER_CONFIG_ID);
        publicServicesJersey.getInitParameters().put("jersey.config.server.provider.packages", "org.rutebanken.tiamat");
        return publicServicesJersey;
    }

    @Bean
    public ServletRegistrationBean healthJersey() {

        Set<Class<?>> resources = new HashSet<>();

        resources.add(HealthResource.class);

        resources.add(OpenApiResource.class);


        resources.add(GeneralExceptionMapper.class);
        resources.add(ErrorResponseEntityMessageBodyWriter.class);

        ResourceConfig resourceConfig = new ResourceConfig(resources);
        ServletRegistrationBean healthServicesJersey = new ServletRegistrationBean(new ServletContainer(resourceConfig));


        healthServicesJersey.addUrlMappings(SERVICES_HEALTH_PATH + "/*");
        healthServicesJersey.setName("HealthJersey");

        healthServicesJersey.getInitParameters().put("swagger.scanner.id", HEALTH_SWAGGER_SCANNER_ID);
        healthServicesJersey.getInitParameters().put("swagger.config.id", HEALTH_SWAGGER_CONFIG_ID);
        healthServicesJersey.getInitParameters().put("jersey.config.server.provider.packages", "org.rutebanken.tiamat");
        healthServicesJersey.setLoadOnStartup(0);
        return healthServicesJersey;
    }

    @Bean
    public ServletRegistrationBean prometheusJersey() {

        Set<Class<?>> resources = new HashSet<>();

        resources.add(PrometheusResource.class);

        resources.add(OpenApiResource.class);


        resources.add(GeneralExceptionMapper.class);
        resources.add(ErrorResponseEntityMessageBodyWriter.class);

        ResourceConfig resourceConfig = new ResourceConfig(resources);
        ServletRegistrationBean prometheusServicesJersey = new ServletRegistrationBean(new ServletContainer(resourceConfig));


        prometheusServicesJersey.addUrlMappings(SERVICES_HEALTH_PATH + "/scrape/*");
        prometheusServicesJersey.setName("PrometheusJersey");

        prometheusServicesJersey.getInitParameters().put("swagger.scanner.id", PROMETHEUS_SWAGGER_SCANNER_ID);
        prometheusServicesJersey.getInitParameters().put("swagger.config.id", PROMETHEUS_SWAGGER_CONFIG_ID);
        prometheusServicesJersey.getInitParameters().put("jersey.config.server.provider.packages", "org.rutebanken.tiamat");
        prometheusServicesJersey.setLoadOnStartup(0);
        return prometheusServicesJersey;
    }


    @Bean
    public ServletRegistrationBean adminJersey() {

        Set<Class<?>> adminResources = new HashSet<>();
        adminResources.add(DtoJbvCodeMappingResource.class);
        adminResources.add(GeneralExceptionMapper.class);

        adminResources.add(OpenApiResource.class);


        adminResources.add(ErrorResponseEntityMessageBodyWriter.class);

        ResourceConfig resourceConfig = new ResourceConfig(adminResources);

        ServletRegistrationBean adminServicesJersey = new ServletRegistrationBean(new ServletContainer(resourceConfig));


        adminServicesJersey.addUrlMappings(SERVICES_ADMIN_PATH + "/*");
        adminServicesJersey.setName("AdminJersey");

        adminServicesJersey.setLoadOnStartup(0);
        adminServicesJersey.getInitParameters().put("swagger.scanner.id", ADMIN_SWAGGER_SCANNER_ID);
        adminServicesJersey.getInitParameters().put("swagger.config.id", ADMIN_SWAGGER_CONFIG_ID);
        adminServicesJersey.getInitParameters().put("jersey.config.server.provider.packages", "org.rutebanken.tiamat");
        return adminServicesJersey;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean(@Autowired LoggingFilter loggingFilter) {

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(loggingFilter);
        registration.addUrlPatterns("/*");
        registration.setName("loggingFilter");
        registration.setOrder(1);
        return registration;
    }


}
