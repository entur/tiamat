package org.rutebanken.tiamat.rest.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.rutebanken.tiamat.dtoassembling.assembler.StopPlaceAssembler;
import org.rutebanken.tiamat.dtoassembling.disassembler.StopPlaceDisassembler;
import org.rutebanken.tiamat.dtoassembling.disassembler.StopPlaceSearchDisassembler;
import org.rutebanken.tiamat.dtoassembling.dto.*;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.StopPlaceSearch;
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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

@Component
@Produces("application/json")
@Path("/stop_place")
@Transactional
public class DtoStopPlaceResource {

    private static final boolean ASSEMBLE_QUAYS_WHEN_MULTIPLE_STOP_PLACES = false;

    private static final Logger logger = LoggerFactory.getLogger(DtoStopPlaceResource.class);

    @Autowired
    StopPlaceRepository stopPlaceRepository;

    @Autowired
    private StopPlaceAssembler stopPlaceAssembler;

    @Autowired
    private StopPlaceDisassembler stopPlaceDisassembler;

    @Autowired
    private QuayRepository quayRepository;

    @Autowired
    private StopPlaceSearchDisassembler stopPlaceSearchDisassembler;

    @GET
    public List<StopPlaceDto> getStopPlaces(@BeanParam StopPlaceSearchDto stopPlaceSearchDto) {

        keyCloak();

        StopPlaceSearch stopPlaceSearch = stopPlaceSearchDisassembler.disassemble(stopPlaceSearchDto);

        Page<StopPlace> stopPlaces;
        if(stopPlaceSearch.isEmpty()) {
            stopPlaces = stopPlaceRepository.findAllByOrderByChangedDesc(stopPlaceSearch.getPageable());
        } else {
            stopPlaces = stopPlaceRepository.findStopPlace(stopPlaceSearch);
        }

        return stopPlaceAssembler.assemble(stopPlaces, ASSEMBLE_QUAYS_WHEN_MULTIPLE_STOP_PLACES);
    }

    @GET
    @Produces("text/plain")
    @Path("/id_mapping")
    public Response getIdMapping(@DefaultValue(value = "20000") @QueryParam(value = "recordsPerRoundTrip") int recordsPerRoundTrip) {

        return  Response.ok().entity((StreamingOutput) output -> {

            int recordPosition = 0;
            boolean lastEmpty = false;

            try ( PrintWriter writer = new PrintWriter( new BufferedWriter( new OutputStreamWriter( output ) ) ) ) {
                while (!lastEmpty) {

                    List<IdMappingDto> stopPlaceMappings = stopPlaceRepository.findKeyValueMappingsForStop(recordPosition, recordsPerRoundTrip);
                    for (IdMappingDto mapping : stopPlaceMappings) {
                        writer.println(mapping.toCsvString());
                        recordPosition ++;
                    }
                    writer.flush();
                    if(stopPlaceMappings.isEmpty()) lastEmpty = true;
                }
                writer.close();
            }
        }).build();
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
                                                           StopPlaceBBoxSearchDTO stopPlaceBBoxSearchDTO) {
        BoundingBoxDto boundingBox = stopPlaceBBoxSearchDTO.boundingBox;

        logger.debug("Search for stop places within bounding box {}", ToStringBuilder.reflectionToString(boundingBox));
        Pageable pageable = new PageRequest(page, size);

        List<StopPlaceDto> stopPlaces = stopPlaceAssembler.assemble(stopPlaceRepository
                .findStopPlacesWithin(boundingBox.xMin, boundingBox.yMin, boundingBox.xMax,
                        boundingBox.yMax, Long.valueOf(stopPlaceBBoxSearchDTO.ignoreStopPlaceId), pageable),
                        ASSEMBLE_QUAYS_WHEN_MULTIPLE_STOP_PLACES);
        logger.debug("Returning {} nearby stop places", stopPlaces.size());
        return stopPlaces;
    }

    @GET
    @Path("{id}")
    public StopPlaceDto getStopPlace(@PathParam("id") String id) {
       return stopPlaceAssembler.assemble(stopPlaceRepository.findOne(Long.valueOf(id)), true);
    }

    @POST
    @Path("{id}")
    public StopPlaceDto updateStopPlace(StopPlaceDto simpleStopPlaceDto, @PathParam("id") String id) {
        keyCloak();

        logger.info("Save stop place {} with id {}", simpleStopPlaceDto.name, simpleStopPlaceDto.id);

        StopPlace currentStopPlace = stopPlaceRepository.findOne(Long.valueOf(simpleStopPlaceDto.id));
        StopPlace stopPlace = stopPlaceDisassembler.disassemble(currentStopPlace, simpleStopPlaceDto);
        if(stopPlace != null) {
            stopPlaceRepository.save(stopPlace);
            quayRepository.save(stopPlace.getQuays());
            return stopPlaceAssembler.assemble(stopPlace, true);
        }

        throw new WebApplicationException("Cannot find stop place with id "+ simpleStopPlaceDto.id, 400);
    }

    @POST
    public StopPlaceDto createStopPlace(StopPlaceDto simpleStopPlaceDto) {
        keyCloak();

        logger.info("Creating stop place with name {}, {} quays and centroid: {}",
                simpleStopPlaceDto.name,
                simpleStopPlaceDto.quays != null ? simpleStopPlaceDto.quays.size() : 0,
                simpleStopPlaceDto.centroid);

        if(simpleStopPlaceDto.id != null) {
            throw new IllegalArgumentException("Cannot accept stop place ID when creating new stop place");
        }

        StopPlace stopPlace = stopPlaceDisassembler.disassemble(new StopPlace(), simpleStopPlaceDto);
        if(stopPlace != null) {
            stopPlaceRepository.save(stopPlace);
            quayRepository.save(stopPlace.getQuays());
            logger.info("Returning created stop place with id {}", stopPlace.getId());
            return stopPlaceAssembler.assemble(stopPlace , true);
        }

        throw new WebApplicationException("Cannot save stop place with name "+ simpleStopPlaceDto.name, 400);
    }
}
