package no.rutebanken.tiamat.importers;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.Striped;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import no.rutebanken.tiamat.model.Site_VersionStructure;
import no.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import no.rutebanken.tiamat.model.TopographicPlace;
import no.rutebanken.tiamat.model.TopographicPlaceRefStructure;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class TopographicPlaceCreator {

    private static final Logger logger = LoggerFactory.getLogger(TopographicPlaceCreator.class);

    private static final MapperFacade mapperFacade = new DefaultMapperFactory.Builder().build().getMapperFacade();

    private Striped<Semaphore> stripedSemaphores = Striped.lazyWeakSemaphore(Integer.MAX_VALUE, 1);

    private TopographicPlaceFromRefFinder topographicPlaceFromRefFinder;
    private TopographicPlaceRepository topographicPlaceRepository;


    private Cache<String, Optional<TopographicPlace>> topographicPlaces = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .build();

    @Autowired
    public TopographicPlaceCreator(TopographicPlaceFromRefFinder topographicPlaceFromRefFinder, TopographicPlaceRepository topographicPlaceRepository) {
        this.topographicPlaceFromRefFinder = topographicPlaceFromRefFinder;
        this.topographicPlaceRepository = topographicPlaceRepository;
    }

    public void invalidateCache() {
        topographicPlaces.invalidateAll();
    }

    /**
     * Find or create topographic place and, if found, set correct reference on provided site.
     */
    public void setTopographicReference(Site_VersionStructure site,
                                        List<TopographicPlace> incomingTopographicPlaces,
                                        AtomicInteger topographicPlacesCreatedCounter) throws ExecutionException {

        Optional<TopographicPlace> optionalTopographicPlace = findOrCreateTopographicPlace(
                incomingTopographicPlaces,
                site.getTopographicPlaceRef(),
                topographicPlacesCreatedCounter);

        if (!optionalTopographicPlace.isPresent()) {
            logger.warn("Got no topographic places back for site {} {}", site.getName(), site.getId());
        }

        optionalTopographicPlace.ifPresent(topographicPlace -> {
            logger.trace("Setting topographical ref {} on site {} {}",
                    topographicPlace.getId(), site.getName(), site.getId());
            TopographicPlaceRefStructure newRef = new TopographicPlaceRefStructure();
            newRef.setRef(topographicPlace.getId());
            site.setTopographicPlaceRef(newRef);
        });
    }

    /**
     * Using cache
     */
    public Optional<TopographicPlace> findOrCreateTopographicPlace(List<TopographicPlace> incomingTopographicPlaces,
                                                                   TopographicPlaceRefStructure topographicPlaceRef,
                                                                   AtomicInteger topographicPlacesCreatedCounter) throws ExecutionException {
        return topographicPlaces.get(topographicPlaceRef.getRef(), () -> findOrCreate(
                incomingTopographicPlaces,
                topographicPlaceRef,
                topographicPlacesCreatedCounter));
    }

    /**
     * Look for existing topographical places.
     * Use existing IDs to resolve references to parent topographical places,
     * but use generated IDs in references.
     */
    private Optional<TopographicPlace> findOrCreate(List<TopographicPlace> incomingTopographicPlaces,
                                                    TopographicPlaceRefStructure topographicPlaceRef,
                                                    AtomicInteger topographicPlacesCreatedCounter) throws InterruptedException, ExecutionException {
        logger.debug("Waiting for semaphore on ref {}");
        // Striped locking on the ref string.
        Semaphore semaphore = stripedSemaphores.get(topographicPlaceRef.getRef());
        semaphore.acquire();
        logger.debug("Got semaphore on ref {}");

        try {
            Optional<TopographicPlace> optionalTopographicPlaceFromRef = topographicPlaceFromRefFinder.findTopographicPlaceFromRef(incomingTopographicPlaces, topographicPlaceRef);

            if(optionalTopographicPlaceFromRef.isPresent()) {
                TopographicPlace topographicPlaceFromRef = optionalTopographicPlaceFromRef.get();
                logger.debug("Found topographic place '{}' '{}' '{}' from REF {}", topographicPlaceFromRef.getName(),
                        topographicPlaceFromRef.getCountryRef().getRef(), topographicPlaceFromRef.getTopographicPlaceType(), topographicPlaceRef.getRef());

                Optional<TopographicPlace> optionalTopographicPlaceFromRepo = findTopoGraphicPlaceInRepository(optionalTopographicPlaceFromRef.get());

                if(optionalTopographicPlaceFromRepo.isPresent()) {
                    TopographicPlace topographicPlaceFromRepo = optionalTopographicPlaceFromRepo.get();
                    logger.debug("Found topographic place '{}' '{}' '{}' in repository", topographicPlaceFromRepo.getName(),
                            topographicPlaceFromRepo.getCountryRef().getRef(),
                            topographicPlaceFromRepo.getTopographicPlaceType());
                    return optionalTopographicPlaceFromRepo;
                } else {
                    return createNewTopographicPlace(incomingTopographicPlaces, topographicPlaceFromRef, topographicPlacesCreatedCounter);
                }

            } else {
                logger.warn("Could not find topographic place from ref {} in incoming site frame data", topographicPlaceRef.getRef());
            }

        } finally {
            semaphore.release();
        }
        return Optional.empty();
    }

    private Optional<TopographicPlace> createNewTopographicPlace(List<TopographicPlace> incomingTopographicPlaces,
                                                                 TopographicPlace topographicPlaceFromRef,
                                                                 AtomicInteger topographicPlacesCreatedCounter) throws ExecutionException {

        TopographicPlace newTopographicPlace = mapperFacade.map(topographicPlaceFromRef, TopographicPlace.class);
        newTopographicPlace.setId(null);

        if(topographicPlaceFromRef.getParentTopographicPlaceRef() != null && !topographicPlaceFromRef.getParentTopographicPlaceRef().getRef().isEmpty()) {
            logger.debug("The topographic place contains reference to parent place '{}'", topographicPlaceFromRef.getParentTopographicPlaceRef().getRef());
            Optional<TopographicPlace> parentTopographicPlace = findOrCreateTopographicPlace(incomingTopographicPlaces,
                    topographicPlaceFromRef.getParentTopographicPlaceRef(), topographicPlacesCreatedCounter);
            parentTopographicPlace.ifPresent(parent -> {
                logger.debug("Found parent place '{}' for '{}'", parent.getName().getValue(), newTopographicPlace.getName().getValue());

                TopographicPlaceRefStructure parentRef = new TopographicPlaceRefStructure();
                parentRef.setRef(parent.getId());
                newTopographicPlace.setParentTopographicPlaceRef(parentRef);
            });
        }

        logger.debug("Saving new topographic place: name '{}', country ref '{}' and type '{}'",
                topographicPlaceFromRef.getName(),
                topographicPlaceFromRef.getCountryRef().getRef(),
                topographicPlaceFromRef.getTopographicPlaceType());

        topographicPlacesCreatedCounter.incrementAndGet();
        topographicPlaceRepository.save(newTopographicPlace);
        return Optional.of(newTopographicPlace);
    }

    public Optional<TopographicPlace> findTopoGraphicPlaceInRepository(TopographicPlace incomingTopographicalPlace) {
        return topographicPlaceRepository.findByNameValueAndCountryRefRefAndTopographicPlaceType(
                incomingTopographicalPlace.getName().getValue(),
                incomingTopographicalPlace.getCountryRef().getRef(),
                incomingTopographicalPlace.getTopographicPlaceType()).stream().findFirst();
    }

}
