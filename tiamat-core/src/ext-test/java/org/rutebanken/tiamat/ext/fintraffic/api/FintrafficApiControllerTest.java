package org.rutebanken.tiamat.ext.fintraffic.api;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import io.swagger.v3.jaxrs2.Reader;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.tags.Tag;
import org.entur.netex2.validation.NeTExValidator;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rutebanken.tiamat.config.JerseyConfig;
import org.rutebanken.tiamat.ext.fintraffic.api.model.ReadApiEntityOutRecord;
import org.rutebanken.tiamat.ext.fintraffic.api.model.ReadApiSearchKey;
import org.rutebanken.tiamat.ext.fintraffic.api.repository.NetexRepository;
import org.rutebanken.tiamat.model.VehicleModeEnumeration;
import org.rutebanken.tiamat.netex.id.ValidPrefixList;
import org.rutebanken.tiamat.time.ExportTimeZone;
import org.springframework.web.bind.annotation.RequestParam;

import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FintrafficApiControllerTest {

    private NetexRepository netexRepository;
    private FintrafficApiController controller;
    private HttpServletResponse response;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() throws IOException {
        netexRepository = mock(NetexRepository.class);

        // Create real service with mocked dependencies
        ValidPrefixList validPrefixList = mock(ValidPrefixList.class);
        when(validPrefixList.getValidNetexPrefix()).thenReturn("FSR");

        ExportTimeZone exportTimeZone = mock(ExportTimeZone.class);
        when(exportTimeZone.getDefaultTimeZoneId()).thenReturn(ZoneId.of("Europe/Helsinki"));

        ReadApiNetexPublicationDeliveryService publicationDeliveryService = new ReadApiNetexPublicationDeliveryService(
                netexRepository,
                validPrefixList,
                exportTimeZone,
                "fin",
                "1.12:FI-NeTEx-stops:1.0"
        );

        controller = new FintrafficApiController(publicationDeliveryService);

        // Setup mock response
        response = mock(HttpServletResponse.class);
        outputStream = new ByteArrayOutputStream();
        ServletOutputStream servletOutputStream = new ServletOutputStream() {
            @Override
            public void write(int b) {
                outputStream.write(b);
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(jakarta.servlet.WriteListener writeListener) {
            }
        };
        when(response.getOutputStream()).thenReturn(servletOutputStream);
    }

    @Test
    void getNetexStreamWithMultipleEntityTypes() {
        String[] transportModes = {"bus"};
        String[] areaCodes = {"ABC"};

        ReadApiEntityOutRecord scheduledStopPoint = new ReadApiEntityOutRecord(
                "ScheduledStopPoint",
                "<ScheduledStopPoint id=\"FSR:ScheduledStopPoint:1\" version=\"1\"/>"
        );

        ReadApiEntityOutRecord topographicPlace = new ReadApiEntityOutRecord(
                "TopographicPlace",
                "<TopographicPlace id=\"FSR:TopographicPlace:91\" version=\"1\"/>"
        );

        ReadApiEntityOutRecord stopPlace = new ReadApiEntityOutRecord(
                "StopPlace",
                "<StopPlace id=\"FSR:StopPlace:1\" version=\"1\"/>"
        );

        ReadApiEntityOutRecord parking = new ReadApiEntityOutRecord(
                "Parking",
                "<Parking id=\"FSR:Parking:1\" version=\"1\"/>"
        );

        when(netexRepository.streamStopPlaces(any(ReadApiSearchKey.class)))
                .thenReturn(Stream.of(scheduledStopPoint, topographicPlace, stopPlace, parking));

        controller.getNetexStream(transportModes, areaCodes, null, response);

        String output = outputStream.toString(StandardCharsets.UTF_8);
        assertThat(output, containsString("FSR:ScheduledStopPoint:1"));
        assertThat(output, containsString("FSR:TopographicPlace:91"));
        assertThat(output, containsString("FSR:StopPlace:1"));
        assertThat(output, containsString("FSR:Parking:1"));
        assertThat(output, containsString("<scheduledStopPoints>"));
        assertThat(output, containsString("</scheduledStopPoints>"));
        assertThat(output, containsString("<topographicPlaces>"));
        assertThat(output, containsString("</topographicPlaces>"));
        assertThat(output, containsString("<stopPlaces>"));
        assertThat(output, containsString("</stopPlaces>"));
        assertThat(output, containsString("<parkings>"));
        assertThat(output, containsString("</parkings>"));
    }

    @Test
    void getNetexStreamRejectsNonNumericMunicipalityCode() {
        controller.getNetexStream(null, null, new String[]{"ABC"}, response);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void getNetexStreamRejectsTwoDigitMunicipalityCode() {
        controller.getNetexStream(null, null, new String[]{"99"}, response);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void getNetexStreamRejectsInvalidAreaCode() {
        controller.getNetexStream(null, new String[]{"123"}, null, response);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void getNetexStreamAcceptsFirstThreeDigitMunicipalityCode() {
        when(netexRepository.streamStopPlaces(any(ReadApiSearchKey.class)))
                .thenReturn(Stream.empty());

        controller.getNetexStream(null, null, new String[]{"000"}, response);
        verify(response, never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void getNetexStreamAcceptsLastFourDigitMunicipalityCode() {
        when(netexRepository.streamStopPlaces(any(ReadApiSearchKey.class)))
                .thenReturn(Stream.empty());

        controller.getNetexStream(null, null, new String[]{"9999"}, response);
        verify(response, never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void getNetexStreamRejectsFiveDigitMunicipalityCode() {
        controller.getNetexStream(null, null, new String[]{"10000"}, response);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void transportModesDocumentationListsEveryVehicleMode() throws Exception {
        Method method = FintrafficApiController.class.getMethod("getNetexStream",
                String[].class, String[].class, String[].class, HttpServletResponse.class);

        Parameter transportModes = Arrays.stream(method.getParameters())
                .filter(p -> p.isAnnotationPresent(RequestParam.class)
                        && "transportModes".equals(p.getAnnotation(RequestParam.class).value()))
                .findFirst()
                .orElseThrow();

        io.swagger.v3.oas.annotations.Parameter documentation =
                transportModes.getAnnotation(io.swagger.v3.oas.annotations.Parameter.class);
        assertThat(documentation, notNullValue());

        String[] documented = documentation.array().schema().allowableValues();
        String[] accepted = Arrays.stream(VehicleModeEnumeration.values())
                .map(VehicleModeEnumeration::value)
                .toArray(String[]::new);
        assertThat(Arrays.asList(documented), containsInAnyOrder(accepted));
    }

    @Test
    void openApiExampleValidatesAgainstNetexSchema() {
        assertThatCode(() -> NeTExValidator.getNeTExValidator(NeTExValidator.NetexVersion.v1_15)
                .validate(new StreamSource(new StringReader(ReadApiOpenApiExamples.PUBLICATION_DELIVERY))))
                .doesNotThrowAnyException();
    }

    @Test
    void readApiControllerIsNotInJerseyOpenApi() throws Exception {
        ServletContainer container = (ServletContainer) new JerseyConfig().publicJersey().getServlet();
        Field resourceConfigField = ServletContainer.class.getDeclaredField("resourceConfig");
        resourceConfigField.setAccessible(true);
        ResourceConfig resourceConfig = (ResourceConfig) resourceConfigField.get(container);
        Set<Class<?>> classes = new HashSet<>(resourceConfig.getClasses());
        classes.add(FintrafficApiController.class);

        OpenAPI openApi = new Reader(new SwaggerConfiguration().openAPI(new OpenAPI())).read(classes);

        List<String> paths = openApi.getPaths() == null ? List.of() : List.copyOf(openApi.getPaths().keySet());
        List<String> tags = openApi.getTags() == null ? List.of() : openApi.getTags().stream().map(Tag::getName).toList();

        assertThat(paths, not(empty()));
        assertThat(paths, not(hasItem(containsString("fintraffic/v1"))));
        assertThat(tags, not(hasItem("Read API")));
    }
}

