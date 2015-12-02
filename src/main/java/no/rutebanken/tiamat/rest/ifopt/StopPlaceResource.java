package no.rutebanken.tiamat.rest.ifopt;

import no.rutebanken.tiamat.ifopt.transfer.assembler.StopPlaceAssembler;
import no.rutebanken.tiamat.ifopt.transfer.dto.StopPlaceDTO;
import no.rutebanken.tiamat.repository.ifopt.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import uk.org.netex.netex.*;

import javax.ws.rs.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
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

    @GET
    public List<StopPlaceDTO> getStopPlaces(
            @DefaultValue(value="0") @QueryParam(value="page") int page,
            @DefaultValue(value="20") @QueryParam(value="size") int size,
            @QueryParam(value="name") String name) {


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

    @GET
    @Path("{id}")
    public StopPlaceDTO getStopPlace(@PathParam("id") String id) {
       return stopPlaceAssembler.assemble(stopPlaceRepository.findOne(id));
    }

    @POST
    @Path("{id}")
    public StopPlaceDTO updateStopPlace(StopPlaceDTO simpleStopPlaceDTO) {
        logger.info("Save stop place {} with id {}", simpleStopPlaceDTO.name, simpleStopPlaceDTO.id);

        StopPlace stopPlace = stopPlaceRepository.findOne(simpleStopPlaceDTO.id);

        //Code belongs in mapper/class and service.
        if(stopPlace != null) {
            stopPlace.setName(new MultilingualString(simpleStopPlaceDTO.name, "no", ""));
            stopPlace.setChanged(new Date());
            stopPlace.setShortName(new MultilingualString(simpleStopPlaceDTO.shortName, "no", ""));
            stopPlace.setDescription(new MultilingualString(simpleStopPlaceDTO.description, "no", ""));

            if(simpleStopPlaceDTO.stopPlaceType != null && !simpleStopPlaceDTO.stopPlaceType.isEmpty()) {
                stopPlace.setStopPlaceType(StopTypeEnumeration.fromValue(simpleStopPlaceDTO.stopPlaceType));
            } else {
                stopPlace.setStopPlaceType(null);
            }

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

        LocationStructure locationStructure = new LocationStructure();
        locationStructure.setLatitude(new BigDecimal(10));
        locationStructure.setLongitude(new BigDecimal(20));

        SimplePoint_VersionStructure centroid = new SimplePoint_VersionStructure();
        centroid.setLocation(locationStructure);

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
