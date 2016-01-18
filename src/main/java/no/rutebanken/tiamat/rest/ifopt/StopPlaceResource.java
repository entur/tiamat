package no.rutebanken.tiamat.rest.ifopt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import no.rutebanken.tiamat.ifopt.transfer.assembler.StopPlaceAssembler;
import no.rutebanken.tiamat.ifopt.transfer.disassembler.StopPlaceDisassembler;
import no.rutebanken.tiamat.ifopt.transfer.dto.BoundingBoxDTO;
import no.rutebanken.tiamat.ifopt.transfer.dto.StopPlaceDTO;
import no.rutebanken.tiamat.repository.ifopt.QuayRepository;
import no.rutebanken.tiamat.repository.ifopt.StopPlaceRepository;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.org.netex.netex.*;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Component
@Produces("application/json")
@Path("/stop_place")
@Transactional
public class StopPlaceResource {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceResource.class);

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private StopPlaceAssembler stopPlaceAssembler;

    @Autowired
    private StopPlaceDisassembler stopPlaceDisassembler;

    @Autowired
    private QuayRepository quayRepository;

    @Autowired
    private XmlMapper xmlMapper;

    @GET
    public List<StopPlaceDTO> getStopPlaces(
            @DefaultValue(value = "0") @QueryParam(value = "page") int page,
            @DefaultValue(value = "20") @QueryParam(value = "size") int size,
            @QueryParam(value = "name") String name) {


        keyCloak();

        logger.debug("Get stop places with name '{}'", name);

        Pageable pageable = new PageRequest(page, size);

        Page<StopPlace> stopPlaces;

        if (name != null && name.length() != 0) {
            stopPlaces = stopPlaceRepository.findByNameValueContainingIgnoreCaseOrderByChangedDesc(name, pageable);
        } else {
            stopPlaces = stopPlaceRepository.findAllByOrderByChangedDesc(pageable);
        }

        return stopPlaceAssembler.assemble(stopPlaces);
    }

    private KeycloakAuthenticationToken keyCloak() {
        // Example reading details about authenticated user
        KeycloakAuthenticationToken auth = (KeycloakAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();

        if(auth != null) {
            @SuppressWarnings("unchecked")
            KeycloakPrincipal<KeycloakSecurityContext> principal = (KeycloakPrincipal<KeycloakSecurityContext>) auth.getPrincipal();
            AccessToken token = principal.getKeycloakSecurityContext().getToken();
            String email = token.getEmail();
            String firstname = token.getGivenName();
            String lastname = token.getFamilyName();
            String fullname = token.getName();
            String preferredUsername = token.getPreferredUsername();
            List agencyids = (List) token.getOtherClaims().get("agencyid");


            // all means all agencies, if not a semicolon delimited list of agencies

            logger.info("Logged in " + principal + " with preferred username " + preferredUsername + ", name is " + firstname + " " + lastname + " and has email address " + email + " and represents agencie(s) " + ToStringBuilder.reflectionToString(agencyids));

            // TODO make sure we only return data according to agencyids
        }
        return auth;
    }

    @POST
    @Path("search")
    public List<StopPlaceDTO> getStopPlacesFromBoundingBox(@Context HttpServletResponse response,
            @DefaultValue(value="0") @QueryParam(value="page") int page,
            @DefaultValue(value="200") @QueryParam(value="size") int size,
            BoundingBoxDTO boundingBox) {

        logger.debug("Search for stop places within bounding box {}", ToStringBuilder.reflectionToString(boundingBox));
        Pageable pageable = new PageRequest(page, size);

        List<StopPlaceDTO> stopPlaces = stopPlaceAssembler.assemble(stopPlaceRepository
                .findStopPlacesWithin(boundingBox.xMin, boundingBox.yMin, boundingBox.xMax, boundingBox.yMax, pageable));
        logger.debug("Returning {} nearby stop places", stopPlaces.size());
        return stopPlaces;
    }

    @GET
    @Path("{id}")
    public StopPlaceDTO getStopPlace(@PathParam("id") String id) {
       return stopPlaceAssembler.assemble(stopPlaceRepository.findOne(id));
    }

    @POST
    @Path("{id}")
    public StopPlaceDTO updateStopPlace(StopPlaceDTO simpleStopPlaceDTO) {
        keyCloak();

        logger.info("Save stop place {} with id {}", simpleStopPlaceDTO.name, simpleStopPlaceDTO.id);

        StopPlace currentStopPlace = stopPlaceRepository.findOne(simpleStopPlaceDTO.id);
        StopPlace stopPlace = stopPlaceDisassembler.disassemble(currentStopPlace, simpleStopPlaceDTO);
        if(stopPlace != null) {
            stopPlaceRepository.save(stopPlace);
            return stopPlaceAssembler.assemble(stopPlace);
        }

        throw new WebApplicationException("Cannot find stop place with id "+simpleStopPlaceDTO.id, 400);
    }

    @GET
    @Path("xml/{id}")
    @Produces(MediaType.APPLICATION_XML)
    public Response getXmlStopPlace(@PathParam("id") String id) {
        StopPlace stopPlace = stopPlaceRepository.findStopPlaceDetailed(id);

        String xml = null;
        try {
            xml = xmlMapper.writeValueAsString(stopPlace);
        } catch (JsonProcessingException e) {
            logger.warn("Error serializing stop place to xml", e);
        }

        return Response.ok(xml).build();
    }


    @GET
    @Path("xml")
    @Produces(MediaType.APPLICATION_XML)
    public Response getAllStopPLaces() {

        //Without streaming because of LazyInstantiationException
        Iterable<StopPlace> stopPlaces = stopPlaceRepository.findAll();


        String xml = null;
        try {
            xml = xmlMapper.writeValueAsString(stopPlaces);
        } catch (JsonProcessingException e) {
            logger.warn("Error serializing stop place to xml", e);
        }

        return Response.ok(xml).build();
    }

    @POST
    @Path("xml")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public String importStopPlaces(List<StopPlace> stopPlaces) {

        logger.info("Importing {} stop places", stopPlaces.size());

        stopPlaces.stream().filter(stopPlace -> stopPlace.getId() != null)
                .forEach(stopPlace -> {
                    logger.info("Delete stop place with ID {}", stopPlace.getId());
                   // stopPlaceRepository.delete(stopPlace);
                  //  stopPlace.getCentroid().setId(null);
                  //  stopPlace.setId(null);
                    stopPlace.getQuays().stream().peek(quay -> logger.info("Saving quay with id {}", quay.getId())).forEach(quayRepository::save);
                });


        stopPlaceRepository.save(stopPlaces);
        return stopPlaces.size()+ " saved";
    }

    /**
     * For testing creation and serialization of a stop place.
     */
    @GET
    @Path("create")
    public StopPlaceDTO createStopPlace() {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace.setId("xxx-yyyy-zazzz");
        MultilingualString name = new MultilingualString();
        name.setTextIdType("textIdType");
        name.setValue("Bahnhof Ried");

        stopPlace.setName(name);

        MultilingualString shortName = new MultilingualString();
        shortName.setValue("Bahnhof");
        shortName.setTextIdType("textId");

        stopPlace.setShortName(shortName);

        SimplePoint centroid = new SimplePoint();

        stopPlace.setCentroid(centroid);

        MultilingualString description = new MultilingualString();
        description.setValue("description");
        stopPlace.setDescription(description);

        stopPlace.setTransportMode(VehicleModeEnumeration.RAIL);
        stopPlace.setAirSubmode(AirSubmodeEnumeration.UNDEFINED);
        stopPlace.setCoachSubmode(CoachSubmodeEnumeration.REGIONAL_COACH);
        stopPlace.setFunicularSubmode(FunicularSubmodeEnumeration.UNKNOWN);
        stopPlace.getOtherTransportModes().add(VehicleModeEnumeration.AIR);

        stopPlaceRepository.save(stopPlace);

        return stopPlaceAssembler.assemble(stopPlace);
    }


}
