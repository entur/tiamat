package org.rutebanken.tiamat.rest.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.dtoassembling.assembler.StopPlaceAssembler;
import org.rutebanken.tiamat.dtoassembling.disassembler.StopPlaceDisassembler;
import org.rutebanken.tiamat.dtoassembling.disassembler.StopPlaceSearchDisassembler;
import org.rutebanken.tiamat.dtoassembling.dto.*;
import org.rutebanken.tiamat.model.EntityStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.StopPlaceSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

import static org.rutebanken.tiamat.auth.AuthorizationConstants.ROLE_EDIT_STOPS;

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
    private StopPlaceSearchDisassembler stopPlaceSearchDisassembler;

	@Autowired
	private AuthorizationService authorizationService;

	@GET
	public List<StopPlaceDto> getStopPlaces(@BeanParam StopPlaceSearchDto stopPlaceSearchDto) {
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
                        boundingBox.yMax, stopPlaceBBoxSearchDTO.ignoreStopPlaceId, pageable),
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

        logger.info("Save stop place {} with id {}", simpleStopPlaceDto.name, simpleStopPlaceDto.id);

        StopPlace currentStopPlace = stopPlaceRepository.findOne(Long.valueOf(simpleStopPlaceDto.id));
        StopPlace stopPlace = stopPlaceDisassembler.disassemble(currentStopPlace, simpleStopPlaceDto);
        if(stopPlace != null) {
            authorizationService.assertAuthorized(ROLE_EDIT_STOPS, stopPlace, currentStopPlace);
            stopPlace = save(stopPlace);
            return stopPlaceAssembler.assemble(stopPlace, true);
        }

        throw new WebApplicationException("Cannot find stop place with id "+ simpleStopPlaceDto.id, 400);
    }

    @POST
    public StopPlaceDto createStopPlace(StopPlaceDto simpleStopPlaceDto) {
        logger.info("Creating stop place with name {}, {} quays and centroid: {}",
                simpleStopPlaceDto.name,
                simpleStopPlaceDto.quays != null ? simpleStopPlaceDto.quays.size() : 0,
                simpleStopPlaceDto.centroid);

        if(simpleStopPlaceDto.id != null) {
            throw new IllegalArgumentException("Cannot accept stop place ID when creating new stop place");
        }

        StopPlace stopPlace = stopPlaceDisassembler.disassemble(new StopPlace(), simpleStopPlaceDto);
        if(stopPlace != null) {
            authorizationService.assertAuthorized(ROLE_EDIT_STOPS, stopPlace);
            stopPlace = save(stopPlace);
            return stopPlaceAssembler.assemble(stopPlace, true);
        }

        throw new WebApplicationException("Cannot save stop place with name "+ simpleStopPlaceDto.name, 400);
    }

    private StopPlace save(StopPlace stopPlace) {
        stopPlace = stopPlaceRepository.save(stopPlace);
        logger.info("Returning created stop place with id {}", stopPlace.getNetexId());
        return stopPlace;
    }
}
