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
