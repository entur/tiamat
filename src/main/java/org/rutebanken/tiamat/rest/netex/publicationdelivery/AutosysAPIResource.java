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

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jakarta.xml.bind.JAXBException;
import org.entur.autosys.model.GetKjoretoyResponse;
import org.entur.autosys.service.AutosysVehicleService;
import org.rutebanken.tiamat.autosys.MapperService;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import java.io.IOException;

@Component
@Tag(name = "Sync export resource", description = "Sync export resource")
@Produces("application/xml")
@Path("autosys")
public class AutosysAPIResource {

    private static final Logger logger = LoggerFactory.getLogger(AutosysAPIResource.class);

    private final AutosysVehicleService autosysVehicleService;
    private final MapperService mapperService;
    private final PublicationDeliveryStreamingOutput publicationDeliveryStreamingOutput;

    @Autowired
    public AutosysAPIResource(PublicationDeliveryStreamingOutput publicationDeliveryStreamingOutput,
                              AutosysVehicleService autosysVehicleService, MapperService mapperService) {
        this.autosysVehicleService = autosysVehicleService;
        this.mapperService = mapperService;
        this.publicationDeliveryStreamingOutput = publicationDeliveryStreamingOutput;
    }

    @GET
    @Produces(MediaType.APPLICATION_XML + "; charset=UTF-8")
    public Response getVehicleFromAutosys(@QueryParam("registrationNumber")String registrationNumber) throws JAXBException, IOException, SAXException {

        GetKjoretoyResponse autosysData = autosysVehicleService.getVehicle(registrationNumber);

        var netexData = mapperService.exportPublicationDeliveryWithAutosysVehicle(new ExportParams(), autosysData.getKjoretoydataListe());

        return Response.ok(publicationDeliveryStreamingOutput.stream(netexData)).build();
    }
}
