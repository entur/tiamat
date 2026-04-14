package org.rutebanken.tiamat.ext.fintraffic.api;

import jakarta.servlet.http.HttpServletResponse;
import org.rutebanken.tiamat.ext.fintraffic.api.model.FintrafficReadApiSearchKey;
import org.rutebanken.tiamat.model.VehicleModeEnumeration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@Profile("fintraffic-read-api")
@Controller
public class FintrafficApiController {
    private final ReadApiNetexPublicationDeliveryService readApiNetexPublicationDeliveryService;
    private final String areaCodeRegex;
    private final String municipalityCodeRegex;

    public FintrafficApiController(
            ReadApiNetexPublicationDeliveryService readApiNetexPublicationDeliveryService,
            @Value("${tiamat.ext.fintraffic.area-code-pattern:[A-ZÅÄÖ]{3}}")
            String areaCodeRegex,
            @Value("${tiamat.ext.fintraffic.municipality-code-pattern:\\d{3}}")
            String municipalityCodeRegex
    ) {
        this.readApiNetexPublicationDeliveryService = readApiNetexPublicationDeliveryService;
        this.areaCodeRegex = areaCodeRegex;
        this.municipalityCodeRegex = municipalityCodeRegex;
    }

    @GetMapping("/fintraffic/v1/stops")
    public void getNetexStream(
            @RequestParam(value = "transportModes", required = false) String[] transportMode,
            @RequestParam(value = "areaCodes", required = false) String[] areaCode,
            @RequestParam(value = "municipalityCodes", required = false) String[] municipalityCode,
            HttpServletResponse response
    ) {
        try {
            validateTransportModes(transportMode);
            validateAreaCodes(areaCode);
            validateMunicipalityCodes(municipalityCode);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        response.setContentType(MediaType.APPLICATION_XML_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setBufferSize(32 * 1024); // 32 KB buffer size for efficient streaming

        try (OutputStream outputStream = response.getOutputStream()) {
            FintrafficReadApiSearchKey searchKey = new FintrafficReadApiSearchKey(transportMode, areaCode, municipalityCode);
            readApiNetexPublicationDeliveryService.streamPublicationDelivery(searchKey, outputStream);
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void validateTransportModes(String[] transportModes) {
        if (transportModes != null) {
            for (String mode : transportModes) {
                VehicleModeEnumeration.fromValue(mode);
            }
        }
    }

    private void validateAreaCodes(String[] areaCodes) {
        if (areaCodes != null) {
            for (String code : areaCodes) {
                if (!code.matches(areaCodeRegex)) {
                    throw new IllegalArgumentException("Invalid areaCode: " + code);
                }
            }
        }
    }

    private void validateMunicipalityCodes(String[] municipalityCodes) {
        if (municipalityCodes != null) {
            for (String code : municipalityCodes) {
                if (!code.matches(municipalityCodeRegex)) {
                    throw new IllegalArgumentException("Invalid municipalityCode: " + code);
                }
            }
        }
    }
}
