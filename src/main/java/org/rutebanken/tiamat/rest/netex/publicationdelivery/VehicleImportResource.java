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

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.xml.bind.JAXBException;
import org.rutebanken.helper.organisation.NotAuthenticatedException;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.tiamat.importer.ImportParams;
import org.rutebanken.tiamat.importer.ImportType;
import org.rutebanken.tiamat.importer.PublicationDeliveryImporter;
import org.rutebanken.tiamat.importer.PublicationDeliveryTariffZoneImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * Import publication deliveries
 */
@Component
@Tag(name = "Import resource", description = "Import resource")
@Produces(MediaType.APPLICATION_XML + "; charset=UTF-8")
@Path("netex")
public class VehicleImportResource {

    private static final Logger logger = LoggerFactory.getLogger(VehicleImportResource.class);

    private final PublicationDeliveryUnmarshaller publicationDeliveryUnmarshaller;

    private final PublicationDeliveryStreamingOutput publicationDeliveryStreamingOutput;

    private final PublicationDeliveryImporter publicationDeliveryImporter;

    private final PublicationDeliveryTariffZoneImporter publicationDeliveryTariffZoneImporter;

    private final Set<ImportType> enabledImportTypes;

    @Autowired
    public VehicleImportResource(PublicationDeliveryUnmarshaller publicationDeliveryUnmarshaller,
                                 PublicationDeliveryStreamingOutput publicationDeliveryStreamingOutput,
                                 PublicationDeliveryImporter publicationDeliveryImporter,
                                 PublicationDeliveryTariffZoneImporter publicationDeliveryTariffZoneImporter,
                                 @Value("#{'${netex.import.enabled.types:ID_MATCH}'.split(',')}") Set<ImportType> enabledImportTypes) {

        this.publicationDeliveryUnmarshaller = publicationDeliveryUnmarshaller;
        this.publicationDeliveryStreamingOutput = publicationDeliveryStreamingOutput;
        this.publicationDeliveryImporter = publicationDeliveryImporter;
        this.publicationDeliveryTariffZoneImporter = publicationDeliveryTariffZoneImporter;
        this.enabledImportTypes = enabledImportTypes;
    }

    public Response importPublicationDelivery(InputStream inputStream) throws IOException, JAXBException, SAXException {
        return importPublicationDelivery(inputStream, null);
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML + "; charset=UTF-8")
    public Response importPublicationDelivery(@Parameter(hidden = true) InputStream inputStream, @BeanParam ImportParams importParams) throws IOException, JAXBException, SAXException {
        logger.info("Received Netex publication delivery, starting to parse...");
        boolean importOnlyTariffZones = importParams != null && importParams.importOnlyTariffZones;

        return importPublicationDelivery(inputStream, importParams, importOnlyTariffZones);
    }


    private Response importPublicationDelivery(InputStream inputStream, ImportParams importParams, boolean importOnlyTariffZones) throws JAXBException, IOException, SAXException {
        ImportType effectiveImportType = safeGetImportType(importParams);
        if (!enabledImportTypes.contains(effectiveImportType)) {
            String error = "ImportType: " + effectiveImportType + " not enabled!";
            logger.warn(error);
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        PublicationDeliveryStructure incomingPublicationDelivery = publicationDeliveryUnmarshaller.unmarshal(inputStream);
        try {
            PublicationDeliveryStructure responsePublicationDelivery;
            if(importOnlyTariffZones) {
                responsePublicationDelivery = publicationDeliveryTariffZoneImporter.importPublicationDelivery(incomingPublicationDelivery,importParams);
            } else {
                responsePublicationDelivery = publicationDeliveryImporter.importPublicationDelivery(incomingPublicationDelivery, importParams);
            }
            if (importParams != null && importParams.skipOutput) {
                return Response.ok().build();
            } else {
                return Response.ok(publicationDeliveryStreamingOutput.stream(responsePublicationDelivery)).build();
            }


        } catch (NotAuthenticatedException | NotAuthorizedException e) {
            logger.debug("Access denied for publication delivery: " + e.getMessage(), e);
            throw e;
        } catch (RuntimeException e) {
            logger.warn("Caught exception while importing publication delivery: " + incomingPublicationDelivery, e);
            throw e;
        }
    }

    /**
     * Return specified ImportType or default value if not set.
     */
    private ImportType safeGetImportType(ImportParams importParams) {
        if (importParams == null || importParams.importType == null) {
            return new ImportParams().importType;
        }
        return importParams.importType;
    }

}
