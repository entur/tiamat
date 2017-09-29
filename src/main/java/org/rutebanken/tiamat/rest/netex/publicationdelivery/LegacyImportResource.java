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

package org.rutebanken.tiamat.rest.netex.publicationdelivery;


import org.rutebanken.tiamat.importer.ImportParams;
import org.rutebanken.tiamat.importer.ImportType;
import org.rutebanken.tiamat.importer.PublicationDeliveryImporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Deprecated
@Path("publication_delivery")
public class LegacyImportResource {

    @Autowired
    private ImportResource importResource;

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response importPublicationDelivery(InputStream inputStream, @BeanParam ImportParams importParams) throws IOException, JAXBException, SAXException {
        return importResource.importPublicationDelivery(inputStream, importParams);
    }


}
