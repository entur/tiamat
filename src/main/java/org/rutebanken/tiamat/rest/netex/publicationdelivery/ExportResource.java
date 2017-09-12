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

import io.swagger.annotations.Api;
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

import static org.rutebanken.tiamat.config.JerseyConfig.SERVICES_PATH;
import static org.rutebanken.tiamat.config.JerseyConfig.SERVICES_STOP_PLACE_PATH;

@Component
@Api
@Produces("application/xml")
@Path("netex")
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
        logger.info("Exporting publication delivery. {}", exportParams);
        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryExporter.exportStopPlaces(exportParams);
        return Response.ok(publicationDeliveryStreamingOutput.stream(publicationDeliveryStructure)).build();
    }


    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("changed_in_period")
    public Response exportStopPlacesWithEffectiveChangedInPeriod(@BeanParam ChangedStopPlaceSearchDto searchDTO,
                                                                        @BeanParam ExportParams exportParams,
                                                                        @Context UriInfo uriInfo)
            throws JAXBException, IOException, SAXException {

        ChangedStopPlaceSearch search = changedStopPlaceSearchDisassembler.disassemble(searchDTO);
        logger.info("Exporting stop places. Search: {}, topographic export mode: {}", search, exportParams.getTopographicPlaceExportMode());
        PublicationDeliveryStructurePage resultPage =
                publicationDeliveryExporter.exportStopPlacesWithEffectiveChangeInPeriod(search, exportParams);

        if (resultPage.totalElements == 0) {
            logger.debug("Returning no content. No stops changed in period.");
            return Response.noContent().build();
        }

        logger.info("Streaming {} changed stops in publication delivery structure", resultPage.size);
        Response.ResponseBuilder rsp = Response.ok(publicationDeliveryStreamingOutput.stream(resultPage.publicationDeliveryStructure));

        if (resultPage.hasNext) {
            rsp.link(createLinkToNextPage(searchDTO.from, searchDTO.to, search.getPageable().getPageNumber() + 1, search.getPageable().getPageSize(), exportParams.getTopographicPlaceExportMode(), exportParams.getTariffZoneExportMode(), uriInfo), "next");
        }

        return rsp.build();
    }

    private URI createLinkToNextPage(String from, String to, int page, int perPage, ExportParams.ExportMode topographicPlaceExportMode, ExportParams.ExportMode tariffZoneExportMode, UriInfo uriInfo) {
        UriBuilder linkBuilder = uriInfo.getAbsolutePathBuilder()
                                         .queryParam("page", page)
                                         .queryParam("per_page", perPage)
                                         .queryParam("topographicPlaceExportMode", topographicPlaceExportMode)
                                         .queryParam("tariffZoneExportMode", tariffZoneExportMode);

        if (from != null) linkBuilder.queryParam("from", from);
        if (to != null) linkBuilder.queryParam("to", to);
        return linkBuilder.build();
    }
}
