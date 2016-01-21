package no.rutebanken.tiamat.rest.ifopt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import no.rutebanken.tiamat.repository.ifopt.QuayRepository;
import no.rutebanken.tiamat.repository.ifopt.StopPlaceRepository;
import no.rutebanken.tiamat.repository.ifopt.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.org.netex.netex.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Component
@Produces("application/xml")
@Path("/site_frame")
@Transactional
public class SiteFrameResource {

    private static final Logger logger = LoggerFactory.getLogger(SiteFrameResource.class);

    private StopPlaceRepository stopPlaceRepository;

    private QuayRepository quayRepository;

    private TopographicPlaceRepository topographicPlaceRepository;

    private XmlMapper xmlMapper;

    @Autowired
    public SiteFrameResource(StopPlaceRepository stopPlaceRepository, QuayRepository quayRepository, TopographicPlaceRepository topographicPlaceRepository, XmlMapper xmlMapper) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.quayRepository = quayRepository;
        this.topographicPlaceRepository = topographicPlaceRepository;
        this.xmlMapper = xmlMapper;
    }

    @GET
    public Response getSiteFrame() throws JsonProcessingException {

        SiteFrame siteFrame = new SiteFrame();

        //Without streaming because of LazyInstantiationException
        Iterable<StopPlace> iterableStopPlaces = stopPlaceRepository.findAll();

        StopPlacesInFrame_RelStructure stopPlacesInFrame_relStructure = new StopPlacesInFrame_RelStructure();

        iterableStopPlaces.forEach(stopPlace -> stopPlacesInFrame_relStructure.getStopPlace().add(stopPlace));

        siteFrame.setStopPlaces(stopPlacesInFrame_relStructure);

        Iterator<TopographicPlace> topographicPlaceIterable = topographicPlaceRepository.findAll().iterator();

        TopographicPlacesInFrame_RelStructure topographicPlaces = new TopographicPlacesInFrame_RelStructure();
        topographicPlaceIterable
                .forEachRemaining(topographicPlace -> topographicPlaces.getTopographicPlace().add(topographicPlace));

        siteFrame.setTopographicPlaces(topographicPlaces);

        try {
            // Using xml mapper directly to avoid lazy instantiation exception. This method is transactional.
            String xml = xmlMapper.writeValueAsString(siteFrame);
            return Response.ok(xml).build();
        } catch (JsonProcessingException e) {
            logger.warn("Error serializing stop place to xml", e);
            throw e;
        }

    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public String importSiteFrame(String xml) throws IOException {
        // Using xml mapper directly because of issues registering it properly in JerseyConfig
        logger.info("Got the following xml\n{}", xml);

        try {
            SiteFrame siteFrame = xmlMapper.readValue(xml, SiteFrame.class);
            logger.info("Got site frame {}", siteFrame);

            siteFrame.getStopPlaces().getStopPlace()
                    .stream()
                    .forEach(stopPlace -> {
                        stopPlace.setId("");

                        if (stopPlace.getTopographicPlaceRef() != null) {
                            findOrCreateTopographicalPlace(
                                    siteFrame.getTopographicPlaces().getTopographicPlace(),
                                    stopPlace.getTopographicPlaceRef())
                                    .ifPresent(topographicPlace -> stopPlace.getTopographicPlaceRef().setRef(topographicPlace.getId()));
                        }
                    });

            stopPlaceRepository.save(siteFrame.getStopPlaces().getStopPlace());

            return "Saved " + siteFrame.getTopographicPlaces().getTopographicPlace().size()
                    + " topographical places and " + siteFrame.getStopPlaces().getStopPlace().size() + "stop places";

        } catch (IOException e) {
            logger.warn("Problems parsing xml: {}", e.getMessage(), e);
            throw e;
        }

    }


    /**
     * Look for existing topographical places.
     * Use existing IDs to resolve references to parent topographical places,
     * but used the genererated IDs from saving in references.
     */
    public Optional<TopographicPlace> findOrCreateTopographicalPlace(List<TopographicPlace> places, TopographicPlaceRefStructure topographicPlaceRef) {


        Optional<TopographicPlace> placeFromRef = places
                .stream()
                .filter(topographicPlace -> topographicPlace.getId() != null)
                .filter(topographicPlace -> topographicPlace.getId().equals(topographicPlaceRef.getRef()))
                .peek(topographicPlace -> logger.info("Peeking at topographical place {}", topographicPlace.getId()))
                .findFirst();

        placeFromRef.ifPresent(topographicPlace -> {

            logger.debug("Topographical place found from ref {}", topographicPlaceRef.getRef());

            // Check if it already exists.
            TopographicPlace topographicPlaceToSave = topographicPlaceRepository
                    .findByNameValueAndCountryRefRefAndTopographicPlaceType(
                            topographicPlace.getName().getValue(),
                            topographicPlace.getCountryRef().getRef(),
                            topographicPlace.getTopographicPlaceType())
                    .stream()
                    .findFirst()
                    .orElse(topographicPlace);

            if (topographicPlaceToSave.getParentTopographicPlaceRef() != null) {
                logger.debug("The topographical place {} contains a parent reference {}", topographicPlace.getId(), topographicPlace.getParentTopographicPlaceRef().getRef());
                findOrCreateTopographicalPlace(places, topographicPlace.getParentTopographicPlaceRef())
                        .ifPresent(parent -> topographicPlaceToSave.getParentTopographicPlaceRef().setRef(parent.getId()));
            }

            topographicPlaceRepository.save(topographicPlace);

            logger.info("Saved topographical place {} - got id {}", topographicPlace.getName(), topographicPlace.getId());
        });

        return placeFromRef;
    }
}
