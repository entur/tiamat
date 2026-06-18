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

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;


public class ErrorResponseEntityMessageBodyWriterTest {

    private String write(ErrorResponseEntity entity) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ErrorResponseEntityMessageBodyWriter().writeTo(
                entity, ErrorResponseEntity.class, ErrorResponseEntity.class,
                new Annotation[0], MediaType.TEXT_PLAIN_TYPE, new MultivaluedHashMap<>(), out);
        return out.toString(StandardCharsets.UTF_8);
    }

    @Test
    public void writesMessage() throws Exception {
        Assert.assertEquals("boom\n", write(new ErrorResponseEntity("boom")));
    }

    @Test
    public void nullMessageIsWrittenWithoutThrowing() throws Exception {
        // A null message (e.g. from a NullPointerException) must not NPE the writer itself.
        Assert.assertEquals("\n", write(new ErrorResponseEntity((String) null)));
    }
}
