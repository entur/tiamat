package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.tiamat.dtoassembling.disassembler.ChangedStopPlaceSearchDisassembler;
import org.rutebanken.tiamat.dtoassembling.disassembler.StopPlaceSearchDisassembler;
import org.rutebanken.tiamat.dtoassembling.dto.ChangedStopPlaceSearchDto;
import org.rutebanken.tiamat.dtoassembling.dto.ExportParamsDto;
import org.rutebanken.tiamat.dtoassembling.dto.StopPlaceSearchDto;
import org.rutebanken.tiamat.exporter.PublicationDeliveryExporter;
import org.rutebanken.tiamat.exporter.PublicationDeliveryStructurePage;
import org.rutebanken.tiamat.repository.ChangedStopPlaceSearch;
import org.rutebanken.tiamat.repository.StopPlaceSearch;
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

    private final StopPlaceSearchDisassembler stopPlaceSearchDisassembler;

    private final PublicationDeliveryExporter publicationDeliveryExporter;

    private final ChangedStopPlaceSearchDisassembler changedStopPlaceSearchDisassembler;

    @Autowired
    public ExportResource(PublicationDeliveryStreamingOutput publicationDeliveryStreamingOutput,
                          StopPlaceSearchDisassembler stopPlaceSearchDisassembler,
                          PublicationDeliveryExporter publicationDeliveryExporter,
                          ChangedStopPlaceSearchDisassembler changedStopPlaceSearchDisassembler) {

        this.publicationDeliveryStreamingOutput = publicationDeliveryStreamingOutput;
        this.stopPlaceSearchDisassembler = stopPlaceSearchDisassembler;
        this.publicationDeliveryExporter = publicationDeliveryExporter;
        this.changedStopPlaceSearchDisassembler = changedStopPlaceSearchDisassembler;
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response exportStopPlaces(@BeanParam StopPlaceSearchDto stopPlaceSearchDto,
                                     @QueryParam(value = "includeTopographicPlaces") boolean includeTopographicPlaces) throws JAXBException, IOException, SAXException {
        StopPlaceSearch stopPlaceSearch = stopPlaceSearchDisassembler.disassemble(stopPlaceSearchDto);
        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryExporter.exportStopPlaces(stopPlaceSearch, includeTopographicPlaces);
        return Response.ok(publicationDeliveryStreamingOutput.stream(publicationDeliveryStructure)).build();
    }


    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("changed")
    public Response exportStopPlacesWithEffectiveChangedInPeriod(@BeanParam ChangedStopPlaceSearchDto searchDTO,
                                                                 @BeanParam ExportParamsDto exportParamsDto,
                                                                 @Context UriInfo uriInfo)
            throws JAXBException, IOException, SAXException {

        ChangedStopPlaceSearch search = changedStopPlaceSearchDisassembler.disassemble(searchDTO);
        PublicationDeliveryStructurePage resultPage =
                publicationDeliveryExporter.exportStopPlacesWithEffectiveChangeInPeriod(search, exportParamsDto.includeTopographicPlaces);

        if (resultPage.totalElements == 0) {
            return Response.noContent().build();
        }

        Response.ResponseBuilder rsp = Response.ok(publicationDeliveryStreamingOutput.stream(resultPage.publicationDeliveryStructure));

        if (resultPage.hasNext) {
            rsp.link(createLinkToNextPage(searchDTO.from, searchDTO.to, search.getPageable().getPageNumber() + 1, search.getPageable().getPageSize(), exportParamsDto.includeTopographicPlaces, uriInfo), "next");
        }

        return rsp.build();
    }

    private URI createLinkToNextPage(String from, String to, int page, int perPage, boolean includeTopographicPlaces, UriInfo uriInfo) {
        UriBuilder linkBuilder = uriInfo.getAbsolutePathBuilder()
                .queryParam("page", page)
                .queryParam("per_page", perPage)
                .queryParam("include_topographic_places", includeTopographicPlaces);

        if (from != null) linkBuilder.queryParam("from", from);
        if (to != null) linkBuilder.queryParam("to", to);
        return linkBuilder.build();
    }
}
