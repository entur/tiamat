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

    public FintrafficApiController(
            ReadApiNetexPublicationDeliveryService readApiNetexPublicationDeliveryService,
            @Value("${tiamat.ext.fintraffic.area-code-pattern:[A-ZÅÄÖ]{3}}")
            String areaCodeRegex
    ) {
        this.readApiNetexPublicationDeliveryService = readApiNetexPublicationDeliveryService;
        this.areaCodeRegex = areaCodeRegex;
    }

    @GetMapping("/fintraffic/v1/stops")
    public void getNetexStream(
            @RequestParam(value = "transportModes", required = false) String[] transportMode,
            @RequestParam(value = "areaCodes", required = false) String[] areaCode,
            HttpServletResponse response
    ) {
        try {
            validateTransportModes(transportMode);
            validateAreaCodes(areaCode);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        response.setContentType(MediaType.APPLICATION_XML_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setBufferSize(32 * 1024); // 32 KB buffer size for efficient streaming

        try (OutputStream outputStream = response.getOutputStream()) {
            FintrafficReadApiSearchKey searchKey = new FintrafficReadApiSearchKey(transportMode, areaCode);
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
}
