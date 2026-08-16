package org.rutebanken.tiamat.rest.write;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.rutebanken.tiamat.jersey.interceptor.XmlPathValidator;
import org.rutebanken.tiamat.rest.write.controllers.SupportedStopPlaceElement;
import org.rutebanken.tiamat.rest.write.dto.StopPlacesDto;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.Set;

/**
 * Turns a raw request payload into a {@link StopPlacesDto}.
 * <p>
 * This runs inside the asynchronous processing unit rather than on the request thread, so that
 * the request thread does no parsing before the job is accepted. Rejections therefore surface as
 * a failed job rather than as a 400, which matches how the rest of the validation in this API
 * behaves.
 * <p>
 * The JAXBContext is built once; creating one per request is expensive.
 */
@Component
public class StopPlacesPayloadUnmarshaller {

    private static final JAXBContext JAXB_CONTEXT = createContext();

    private final Set<String> allowedPaths;

    public StopPlacesPayloadUnmarshaller(SupportedStopPlaceElement supportedStopPlaceElement) {
        this.allowedPaths = supportedStopPlaceElement.allowedPaths();
    }

    private static JAXBContext createContext() {
        try {
            return JAXBContext.newInstance(StopPlacesDto.class);
        } catch (JAXBException e) {
            throw new IllegalStateException("Could not create JAXBContext for " + StopPlacesDto.class, e);
        }
    }

    public StopPlacesDto unmarshal(byte[] payload) {
        XmlPathValidator.validate(payload, allowedPaths);
        try {
            Unmarshaller unmarshaller = JAXB_CONTEXT.createUnmarshaller();
            return (StopPlacesDto) unmarshaller.unmarshal(new ByteArrayInputStream(payload));
        } catch (JAXBException e) {
            throw new IllegalArgumentException("Could not parse stop places payload: " + e.getMessage());
        }
    }
}
