package org.rutebanken.tiamat.rest.dto;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.rutebanken.tiamat.dtoassembling.assembler.StopPlaceAssembler;
import org.rutebanken.tiamat.dtoassembling.disassembler.StopPlaceDisassembler;
import org.rutebanken.tiamat.dtoassembling.dto.BoundingBoxDto;
import org.rutebanken.tiamat.dtoassembling.dto.StopPlaceDto;
import org.rutebanken.tiamat.dtoassembling.dto.StopPlaceSearchDTO;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
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
import java.util.ArrayList;
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

    @GET
    public List<StopPlaceDto> getStopPlaces(
            @DefaultValue(value = "0") @QueryParam(value = "page") int page,
            @DefaultValue(value = "20") @QueryParam(value = "size") int size,
            @QueryParam(value = "q") String query,
            @QueryParam(value = "municipalityReference") List<String> municipalityReferences,
            @QueryParam(value = "countyReference") List<String> countyReferences,
            @QueryParam(value = "stopPlaceType") List<String> stopPlaceTypes) {

        List<StopTypeEnumeration> stopTypeEnums = new ArrayList<>();
        if (stopPlaceTypes != null) {
            stopPlaceTypes.forEach(string ->
                    stopTypeEnums.add(StopTypeEnumeration.fromValue(string)));
        }

        keyCloak();

        logger.info("Get stop places '{}'", MoreObjects.toStringHelper("Query")
                .add("municipalityReferences", municipalityReferences)
                .add("countyReference", countyReferences)
                .add("stopPlaceType", stopPlaceTypes)
                .add("query", query)
                .add("page", page)
                .add("size", size));

        Pageable pageable = new PageRequest(page, size);

        Page<StopPlace> stopPlaces;

        if ((query != null && !query.isEmpty()) || countyReferences != null || municipalityReferences != null || stopPlaceTypes != null) {
            stopPlaces = stopPlaceRepository.findStopPlace(query, municipalityReferences, countyReferences, stopTypeEnums, pageable);
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
                                                           StopPlaceSearchDTO stopPlaceSearchDTO) {
        BoundingBoxDto boundingBox = stopPlaceSearchDTO.boundingBox;

        logger.debug("Search for stop places within bounding box {}", ToStringBuilder.reflectionToString(boundingBox));
        Pageable pageable = new PageRequest(page, size);

        List<StopPlaceDto> stopPlaces = stopPlaceAssembler.assemble(stopPlaceRepository
                .findStopPlacesWithin(boundingBox.xMin, boundingBox.yMin, boundingBox.xMax, boundingBox.yMax, Long.valueOf(stopPlaceSearchDTO.ignoreStopPlaceId), pageable));
        logger.debug("Returning {} nearby stop places", stopPlaces.size());
        return stopPlaces;
    }

    @GET
    @Path("{id}")
    public StopPlaceDto getStopPlace(@PathParam("id") String id) {
       return stopPlaceAssembler.assemble(stopPlaceRepository.findOne(Long.valueOf(id)));
    }

    @POST
    @Path("{id}")
    public StopPlaceDto updateStopPlace(StopPlaceDto simpleStopPlaceDto) {
        keyCloak();

        logger.info("Save stop place {} with id {}", simpleStopPlaceDto.name, simpleStopPlaceDto.id);

        StopPlace currentStopPlace = stopPlaceRepository.findOne(Long.valueOf(simpleStopPlaceDto.id));
        StopPlace stopPlace = stopPlaceDisassembler.disassemble(currentStopPlace, simpleStopPlaceDto);
        if(stopPlace != null) {
            stopPlaceRepository.save(stopPlace);
            return stopPlaceAssembler.assemble(stopPlace);
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

        StopPlace stopPlace = stopPlaceDisassembler.disassemble(new StopPlace(), simpleStopPlaceDto);
        if(stopPlace != null) {
            stopPlaceRepository.save(stopPlace);
            quayRepository.save(stopPlace.getQuays());
            logger.info("Returning created stop place with id {}", stopPlace.getId());
            return stopPlaceAssembler.assemble(stopPlace);
        }

        throw new WebApplicationException("Cannot save stop place with name "+ simpleStopPlaceDto.name, 400);
    }
}
