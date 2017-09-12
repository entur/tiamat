package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.tiamat.dtoassembling.disassembler.ChangedStopPlaceSearchDisassembler;
import org.rutebanken.tiamat.dtoassembling.dto.ChangedStopPlaceSearchDto;
import org.rutebanken.tiamat.exporter.PublicationDeliveryExporter;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;

import java.io.IOException;

import static org.rutebanken.tiamat.config.JerseyConfig.SERVICES_STOP_PLACE_PATH;

@Deprecated
@Path("publication_delivery")
public class LegacyExportResource {

    @Autowired
    private ExportResource exportResource;

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response exportStopPlaces(@BeanParam ExportParams exportParams) throws JAXBException, IOException, SAXException {
        return exportResource.exportStopPlaces(exportParams);
    }


    @GET
    @Path("changed")
    public Response exportStopPlacesWithEffectiveChangedInPeriod(@BeanParam ChangedStopPlaceSearchDto searchDTO,
                                                                 @BeanParam ExportParams exportParams,
                                                                 @Context UriInfo uriInfo) throws JAXBException, IOException, SAXException {
        return exportResource.exportStopPlacesWithEffectiveChangedInPeriod(searchDTO, exportParams, uriInfo);
    }
}
