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
@Produces(MediaType.APPLICATION_XML)
public class LegacyExportResource {

    @Autowired
    private ExportResource exportResource;

    @GET
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
