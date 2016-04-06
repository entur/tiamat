package no.rutebanken.tiamat.rest.ifopt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.util.concurrent.Striped;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
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
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Produces("application/xml")
@Path("/site_frame")
@Transactional
public class SiteFrameResource {

    private static final Logger logger = LoggerFactory.getLogger(SiteFrameResource.class);

    private static final MapperFacade mapperFacade = new DefaultMapperFactory.Builder().build().getMapperFacade();

    private StopPlaceRepository stopPlaceRepository;

    private QuayRepository quayRepository;

    private TopographicPlaceRepository topographicPlaceRepository;

    private Striped<Semaphore> stripedSemaphores = Striped.lazyWeakSemaphore(Integer.MAX_VALUE, 1);

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
        logger.info("Incoming xml is {} characters long", xml.length());

        AtomicInteger counter = new AtomicInteger(0);

        try {
            SiteFrame siteFrame = xmlMapper.readValue(xml, SiteFrame.class);
            logger.info("Received site frame for import. It contains {} topographical places and {} stop places. Starting import,",
                    siteFrame.getTopographicPlaces().getTopographicPlace().size(),
                    siteFrame.getStopPlaces().getStopPlace().size());

            siteFrame.getStopPlaces().getStopPlace()
                    .stream()
                    .forEach(stopPlace -> {
                        try {
                            importStopPlace(stopPlace, siteFrame, counter);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });

            String returnString = "Saved " + siteFrame.getTopographicPlaces().getTopographicPlace().size()
                    + " topographical places and " + siteFrame.getStopPlaces().getStopPlace().size() + " stop places";

            logger.info(returnString);

            return returnString;

        } catch (IOException e) {
            logger.warn("Problems parsing xml: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void importStopPlace(StopPlace stopPlace, SiteFrame siteFrame, AtomicInteger counter) throws InterruptedException {
        if (stopPlace.getCentroid() == null
                || stopPlace.getCentroid().getLocation() == null
                || stopPlace.getCentroid().getLocation().getGeometryPoint() == null) {
            logger.info("Ignoring stop place {} - {} because it lacks geometry", stopPlace.getName(), stopPlace.getId());
            return;
        }

        StopPlace existing = stopPlaceRepository
                .findByNameValueAndCentroidLocationGeometryPoint(
                        stopPlace.getName().getValue(),
                        stopPlace.getCentroid().getLocation().getGeometryPoint());

        if (existing != null) {
            logger.debug("Found existing stop place {} with the same name and same location {}. Delete...",
                    stopPlace.getName(), stopPlace.getCentroid().getLocation().getGeometryPoint().toText());
            stopPlaceRepository.delete(existing.getId());
        }

        if (stopPlace.getTopographicPlaceRef() != null) {
            Optional<TopographicPlace> optional = findOrCreateTopographicalPlace(
                    siteFrame.getTopographicPlaces().getTopographicPlace(),
                    stopPlace.getTopographicPlaceRef());

            if (!optional.isPresent()) {
                logger.warn("Got no topographic places back for stop place {} {}", stopPlace.getName(), stopPlace.getId());
            }

            optional.ifPresent(topographicPlace -> {
                logger.trace("Setting topographical ref {} on stop place {} {}",
                        topographicPlace.getId(), stopPlace.getName(), stopPlace.getId());
                TopographicPlaceRefStructure newRef = new TopographicPlaceRefStructure();
                newRef.setRef(topographicPlace.getId());
                stopPlace.setTopographicPlaceRef(newRef);
            });
        }

        stopPlace.setId(null);

        if (stopPlace.getQuays() != null) {
            stopPlace.getQuays().forEach(quay -> {
                quay.setId(null);
                quayRepository.save(quay);
            });
        }

        stopPlaceRepository.save(stopPlace);
        counter.incrementAndGet();
        logger.debug("Saving stop place {} {}", stopPlace.getName(), stopPlace.getId());

        if (counter.get() % 100 == 0) {
            int total = siteFrame.getStopPlaces().getStopPlace().size();
            logger.info("Stop place number {} of {}. {}%", counter.get(), siteFrame.getStopPlaces().getStopPlace().size(), (counter.get() * 100f) / total);
        }
    }



    /**
     * fixme: Make this more readable and clean
     *
     * Look for existing topographical places.
     * Use existing IDs to resolve references to parent topographical places,
     * but used the genererated IDs from saving in references.
     */
    public Optional<TopographicPlace> findOrCreateTopographicalPlace(List<TopographicPlace> places, TopographicPlaceRefStructure topographicPlaceRef) throws InterruptedException {

        // Striped locking on the ref string.
        Semaphore semaphore = stripedSemaphores.get(topographicPlaceRef.getRef());

        semaphore.acquire();

        try {
            Optional<TopographicPlace> optional = findTopographicPlaceFromRefOrRepository(places, topographicPlaceRef);

            if (optional.isPresent()) {

                TopographicPlace topographicPlaceFromRef = optional.get();

                logger.debug("Topographical place found from ref {}", topographicPlaceRef.getRef());

                TopographicPlace topographicPlace = findSimilarOrUse(topographicPlaceFromRef);

                if (topographicPlace.getParentTopographicPlaceRef() != null) {
                    logger.debug("The topographical place {} contains a parent reference {}", topographicPlace.getName(),
                            topographicPlace.getParentTopographicPlaceRef().getRef());

                    findOrCreateTopographicalPlace(places, topographicPlace.getParentTopographicPlaceRef())
                            .ifPresent(parent -> topographicPlace.getParentTopographicPlaceRef().setRef(parent.getId()));
                }

                // if(!topographicPlaceRepository.exists(topographicPlace.getId())) {
                topographicPlaceRepository.save(topographicPlace);
                logger.debug("Saved {} place {} of type {} - got id {}", topographicPlace.getTopographicPlaceType(), topographicPlace.getName(),
                        topographicPlace.getTopographicPlaceType(), topographicPlace.getId());
                //}

                return Optional.of(topographicPlace);
            }

            logger.warn("Found no topographical place from ref {} looking in {} topographical places", topographicPlaceRef.getRef(), places.size());
            places.forEach(place -> logger.info("{} - {}", place.getName(), place.getId()));

            return Optional.empty();
        } finally {
            semaphore.release();
        }

    }

    /**
     * Search for similar topographic place in the repository.
     * If no match, use the topographic place that origins from the incoming xml, but clear the ID.
     */
    public TopographicPlace findSimilarOrUse(TopographicPlace topographicPlaceFromRef) {

        // Check if similar place already exists. If not, use the one from the xml, but remember to remove the ID..
        return topographicPlaceRepository
                .findByNameValueAndCountryRefRefAndTopographicPlaceType(
                        topographicPlaceFromRef.getName().getValue(),
                        topographicPlaceFromRef.getCountryRef().getRef(),
                        topographicPlaceFromRef.getTopographicPlaceType())
                .stream()
                .peek(topographicPlace1 -> logger.debug("Found already persisted {} with name {} and id {}",
                        topographicPlace1.getTopographicPlaceType(),
                        topographicPlace1.getName(),
                        topographicPlace1.getId()))
                .findFirst()
                .orElseGet(() -> {

                    logger.debug("Could not find similar {} to {} with country {} in database. Creating new one.",
                            topographicPlaceFromRef.getTopographicPlaceType(),
                            topographicPlaceFromRef.getName().getValue(),
                            topographicPlaceFromRef.getCountryRef().getRef());

                    TopographicPlace newTopographicalPlace = mapperFacade.map(topographicPlaceFromRef, TopographicPlace.class);
                    newTopographicalPlace.setId(null);

                    return newTopographicalPlace;
                });
    }

    public Optional<TopographicPlace> findTopographicPlaceFromRefOrRepository(List<TopographicPlace> places, TopographicPlaceRefStructure topographicPlaceRef) {

        Optional<TopographicPlace> optional = places
                .stream()
                .filter(topographicPlace -> topographicPlace.getId() != null)
                .filter(topographicPlace -> topographicPlace.getId().equals(topographicPlaceRef.getRef()))
                .peek(topographicPlace -> logger.trace("Looking at topographical place with name {} and id {}", topographicPlace.getName(), topographicPlace.getId()))
                .findFirst();

        //If another stop place or municipality was processed, the topographic place is already persisted.
        if (!optional.isPresent()) {
            optional = Optional.of(topographicPlaceRepository.findOne(topographicPlaceRef.getRef()));
        }

        return optional;

    }
}
