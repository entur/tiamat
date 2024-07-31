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

package org.rutebanken.tiamat.rest.exception;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Optional;

@Provider
@Produces("application/xml;charset=UTF-8")
public class ErrorResponseEntityMessageBodyWriter implements MessageBodyWriter<ErrorResponseEntity> {

    private static final Logger logger = LoggerFactory.getLogger(ErrorResponseEntityMessageBodyWriter.class);

    @Override
    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return aClass == ErrorResponseEntity.class;
    }

    @Override
    public long getSize(ErrorResponseEntity errorResponseEntity, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        // Deprecated jaxrs 2.0
        return 0;
    }

    @Override
    public void writeTo(ErrorResponseEntity errorResponseEntity, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> multivaluedMap, OutputStream outputStream) throws IOException, WebApplicationException {
        Writer writer = new PrintWriter(outputStream);

        Optional.ofNullable(errorResponseEntity.errors)
                .ifPresent(errors -> errors.forEach(error -> {
                    try {
                        if (error.message != null) {
                            writer.write(error.message);
                            writer.write("\n");
                        }
                    } catch (IOException e) {
                        logger.error("Cannot write error message when serializing error response entity", e);
                    }
                }));

        writer.flush();
        writer.close();
    }
}
