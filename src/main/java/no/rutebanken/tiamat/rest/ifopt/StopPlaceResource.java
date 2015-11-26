package no.rutebanken.tiamat.rest.ifopt;

import no.rutebanken.tiamat.ifopt.transfer.assembler.SimpleStopPlaceAssembler;
import no.rutebanken.tiamat.ifopt.transfer.dto.SimpleStopPlaceDTO;
import no.rutebanken.tiamat.repository.ifopt.StopPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.org.netex.netex.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Produces("application/json")
@Path("/stop_place")
public class StopPlaceResource {

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private SimpleStopPlaceAssembler simpleStopPlaceAssembler;

    @GET
    public List<SimpleStopPlaceDTO> getStopPlaces() {
        List<SimpleStopPlaceDTO> stopPlaces = stopPlaceRepository.findAll().stream()
                .map(stopPlace -> simpleStopPlaceAssembler.assemble(stopPlace)).collect(Collectors.toList());
        return stopPlaces;
    }

    @GET
    @Path("{id}")
    public SimpleStopPlaceDTO getStopPlace(@PathParam("id") String id) {
       return simpleStopPlaceAssembler.assemble(stopPlaceRepository.findOne(id));
    }

    /**
     * For testing creation and serialization of a stop place.
     */
    @GET
    @Path("create")
    public SimpleStopPlaceDTO createStopPlace() {

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

        return simpleStopPlaceAssembler.assemble(stopPlace);
    }


}
