package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.tiamat.dtoassembling.disassembler.ChangedStopPlaceSearchDisassembler;
import org.rutebanken.tiamat.dtoassembling.dto.ChangedStopPlaceSearchDto;
import org.rutebanken.tiamat.exporter.PublicationDeliveryExporter;
import org.rutebanken.tiamat.exporter.PublicationDeliveryStructurePage;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.repository.ChangedStopPlaceSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.URI;

@Component
@Produces("application/xml")
@Path("/publication_delivery")
public class ExportResource {

    private static final Logger logger = LoggerFactory.getLogger(ExportResource.class);

    private final PublicationDeliveryStreamingOutput publicationDeliveryStreamingOutput;

    private final PublicationDeliveryExporter publicationDeliveryExporter;

    private final ChangedStopPlaceSearchDisassembler changedStopPlaceSearchDisassembler;

    @Autowired
    public ExportResource(PublicationDeliveryStreamingOutput publicationDeliveryStreamingOutput,
                          PublicationDeliveryExporter publicationDeliveryExporter,
                          ChangedStopPlaceSearchDisassembler changedStopPlaceSearchDisassembler) {

        this.publicationDeliveryStreamingOutput = publicationDeliveryStreamingOutput;
        this.publicationDeliveryExporter = publicationDeliveryExporter;
        this.changedStopPlaceSearchDisassembler = changedStopPlaceSearchDisassembler;
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response exportStopPlaces(@BeanParam ExportParams exportParams) throws JAXBException, IOException, SAXException {
        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryExporter.exportStopPlaces(exportParams);
        return Response.ok(publicationDeliveryStreamingOutput.stream(publicationDeliveryStructure)).build();
    }


    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("changed")
    public Response exportStopPlacesWithEffectiveChangedInPeriod(@BeanParam ChangedStopPlaceSearchDto searchDTO,
                                                                 @BeanParam ExportParams exportParams,
                                                                 @Context UriInfo uriInfo)
            throws JAXBException, IOException, SAXException {

        ChangedStopPlaceSearch search = changedStopPlaceSearchDisassembler.disassemble(searchDTO);
        PublicationDeliveryStructurePage resultPage =
                publicationDeliveryExporter.exportStopPlacesWithEffectiveChangeInPeriod(search, exportParams.getTopopgraphicPlaceExportMode());

        if (resultPage.totalElements == 0) {
            return Response.noContent().build();
        }

        Response.ResponseBuilder rsp = Response.ok(publicationDeliveryStreamingOutput.stream(resultPage.publicationDeliveryStructure));

        if (resultPage.hasNext) {
            rsp.link(createLinkToNextPage(searchDTO.from, searchDTO.to, search.getPageable().getPageNumber() + 1, search.getPageable().getPageSize(), exportParams.getTopopgraphicPlaceExportMode(), uriInfo), "next");
        }

        return rsp.build();
    }

    private URI createLinkToNextPage(String from, String to, int page, int perPage, ExportParams.ExportMode includeTopographicPlaces, UriInfo uriInfo) {
        UriBuilder linkBuilder = uriInfo.getAbsolutePathBuilder()
                .queryParam("page", page)
                .queryParam("per_page", perPage)
                .queryParam("include_topographic_places", includeTopographicPlaces);

        if (from != null) linkBuilder.queryParam("from", from);
        if (to != null) linkBuilder.queryParam("to", to);
        return linkBuilder.build();
    }
}
