package no.rutebanken.tiamat.rest.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import no.rutebanken.tiamat.dtoassembling.assembler.StopPlaceAssembler;
import no.rutebanken.tiamat.dtoassembling.disassembler.StopPlaceDisassembler;
import no.rutebanken.tiamat.dtoassembling.dto.BoundingBoxDto;
import no.rutebanken.tiamat.dtoassembling.dto.StopPlaceDto;
import no.rutebanken.tiamat.model.*;
import no.rutebanken.tiamat.repository.QuayRepository;
import no.rutebanken.tiamat.repository.StopPlaceRepository;
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
public class DtoStopPlaceResource {

    private static final Logger logger = LoggerFactory.getLogger(DtoStopPlaceResource.class);

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
    public List<StopPlaceDto> getStopPlaces(
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
    public List<StopPlaceDto> getStopPlacesFromBoundingBox(@Context HttpServletResponse response,
                                                           @DefaultValue(value="0") @QueryParam(value="page") int page,
                                                           @DefaultValue(value="200") @QueryParam(value="size") int size,
                                                           BoundingBoxDto boundingBox) {

        logger.debug("Search for stop places within bounding box {}", ToStringBuilder.reflectionToString(boundingBox));
        Pageable pageable = new PageRequest(page, size);

        List<StopPlaceDto> stopPlaces = stopPlaceAssembler.assemble(stopPlaceRepository
                .findStopPlacesWithin(boundingBox.xMin, boundingBox.yMin, boundingBox.xMax, boundingBox.yMax, pageable));
        logger.debug("Returning {} nearby stop places", stopPlaces.size());
        return stopPlaces;
    }

    @GET
    @Path("{id}")
    public StopPlaceDto getStopPlace(@PathParam("id") String id) {
       return stopPlaceAssembler.assemble(stopPlaceRepository.findOne(id));
    }

    @POST
    @Path("{id}")
    public StopPlaceDto updateStopPlace(StopPlaceDto simpleStopPlaceDto) {
        keyCloak();

        logger.info("Save stop place {} with id {}", simpleStopPlaceDto.name, simpleStopPlaceDto.id);

        StopPlace currentStopPlace = stopPlaceRepository.findOne(simpleStopPlaceDto.id);
        StopPlace stopPlace = stopPlaceDisassembler.disassemble(currentStopPlace, simpleStopPlaceDto);
        if(stopPlace != null) {
            stopPlaceRepository.save(stopPlace);
            return stopPlaceAssembler.assemble(stopPlace);
        }

        throw new WebApplicationException("Cannot find stop place with id "+ simpleStopPlaceDto.id, 400);
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
        Iterable<StopPlace> iterableStopPlaces = stopPlaceRepository.findAll();

        StopPlaces stopPlaces = new StopPlaces();
        // TODO: Avoid iterating through stop places before serializing.
        iterableStopPlaces.forEach(stopPlace -> stopPlaces.getStopPlaces().add(stopPlace));

        String xml = null;
        try {
            // Using xml mapper directly to avoid lazy instantiation exception. This method is transactional.
            xml = xmlMapper.writeValueAsString(stopPlaces);
        } catch (JsonProcessingException e) {
            logger.warn("Error serializing stop place to xml", e);
        }

        return Response.ok(xml).build();
    }

    /*

    Commented out. Because xml serializer is not registered with jersey, after issues with running Tiamat as a fat jar:
    https://github.com/spring-projects/spring-boot/issues/1345

    @POST
    @Path("xml")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public List<String> importStopPlaces(String xml) throws IOException {

        // Using xml mapper directly because of issues registering it properly in JerseyConfig
        logger.trace("Got the following xml\n{}", xml);

        StopPlaces stopPlaces;
        try {
            stopPlaces = xmlMapper.readValue(xml, StopPlaces.class);
        } catch (IOException e) {
            logger.warn("Error deserializing stop places {}", e.getMessage(), e);
            throw e;
        }

        logger.info("Importing {} stop places", stopPlaces.getStopPlaces().size());

        stopPlaces.getStopPlaces()
                .stream()
                .filter(stopPlace -> stopPlace.getQuays() != null)
                .flatMap(stopPlace -> stopPlace.getQuays().stream())
                .forEach(quayRepository::save);

        stopPlaceRepository.save(stopPlaces.getStopPlaces());
        return stopPlaces.getStopPlaces().stream().map(EntityStructure::getId).collect(Collectors.toList());
    }
    */

    /**
     * For testing creation and serialization of a stop place.
     */
    @GET
    @Path("create")
    public StopPlaceDto createStopPlace() {

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
