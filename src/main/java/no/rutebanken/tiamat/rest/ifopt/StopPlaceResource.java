package no.rutebanken.tiamat.rest.ifopt;

import no.rutebanken.tiamat.ifopt.transfer.assembler.StopPlaceAssembler;
import no.rutebanken.tiamat.ifopt.transfer.disassembler.StopPlaceDisassembler;
import no.rutebanken.tiamat.ifopt.transfer.dto.BoundingBoxDTO;
import no.rutebanken.tiamat.ifopt.transfer.dto.StopPlaceDTO;
import no.rutebanken.tiamat.repository.ifopt.StopPlaceRepository;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import uk.org.netex.netex.*;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Produces("application/json")
@Path("/stop_place")
public class StopPlaceResource {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceResource.class);

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private StopPlaceAssembler stopPlaceAssembler;

    @Autowired
    private StopPlaceDisassembler stopPlaceDisassembler;

    @GET
    public List<StopPlaceDTO> getStopPlaces(
            @DefaultValue(value="0") @QueryParam(value="page") int page,
            @DefaultValue(value="20") @QueryParam(value="size") int size,
            @QueryParam(value="name") String name) {


        keyCloak();
    	
        logger.info("Get stop places with names that contains {}", name);

        Pageable pageable = new PageRequest(page, size);

        List<StopPlace> stopPlaces;

        if(name != null && name.length() != 0) {
            stopPlaces = stopPlaceRepository
                    .findByNameValueContainingIgnoreCase(name, pageable)
                    .getContent();
        } else {
            stopPlaces = stopPlaceRepository
                    .findAll(pageable)
                    .getContent();
        }

       return stopPlaces
                .stream()
                .map(stopPlace -> stopPlaceAssembler.assemble(stopPlace))
                .collect(Collectors.toList());
    }

    private KeycloakAuthenticationToken keyCloak() {
        // Example reading details about authenticated user
        KeycloakAuthenticationToken auth = (KeycloakAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();

        @SuppressWarnings("unchecked")
        KeycloakPrincipal<KeycloakSecurityContext> principal = (KeycloakPrincipal<KeycloakSecurityContext>)auth.getPrincipal();
        AccessToken token = principal.getKeycloakSecurityContext().getToken();
        String email = token.getEmail();
        String firstname = token.getGivenName();
        String lastname = token.getFamilyName();
        String fullname = token.getName();
        String preferredUsername = token.getPreferredUsername();
        List agencyids = (List) token.getOtherClaims().get("agencyid");


        // all means all agencies, if not a semicolon delimited list of agencies

        logger.info("Logged in "+principal+" with preferred username "+preferredUsername+", name is "+firstname+" "+lastname+" and has email address "+email+" and represents agencie(s) "+ToStringBuilder.reflectionToString(agencyids));;

        // TODO make sure we only return data according to agencyids

        return auth;
    }

    @POST
    @Path("search")
    public List<StopPlaceDTO> getStopPlacesFromBoundingBox(@Context HttpServletResponse response,
            @DefaultValue(value="0") @QueryParam(value="page") int page,
            @DefaultValue(value="20") @QueryParam(value="size") int size,
            BoundingBoxDTO boundingBox) {

        logger.info("Search for stop places within bounding box {}, {} {}, {}", boundingBox.xMin, boundingBox.yMin, boundingBox.xMax, boundingBox.yMax);
        
        return stopPlaceRepository.findStopPlacesWithin(boundingBox.xMin, boundingBox.yMin, boundingBox.xMax, boundingBox.yMax)
                .stream()
                .filter(Objects::nonNull)
                .map(stopPlace -> stopPlaceAssembler.assemble(stopPlace))
                .collect(Collectors.toList());
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
