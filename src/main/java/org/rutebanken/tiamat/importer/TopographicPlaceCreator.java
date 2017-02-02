package org.rutebanken.tiamat.importer;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.Striped;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.rutebanken.tiamat.importer.finder.TopographicPlaceFromRefFinder;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceRefStructure;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public void setTopographicReference(StopPlace stopPlace,
                                        List<TopographicPlace> incomingTopographicPlaces,
                                        AtomicInteger topographicPlacesCreatedCounter) throws ExecutionException {
        if(stopPlace.getTopographicPlaceRef() == null) return;


        Optional<TopographicPlace> optionalTopographicPlace = findOrCreateTopographicPlace(
                incomingTopographicPlaces,
                stopPlace.getTopographicPlaceRef(),
                topographicPlacesCreatedCounter);

        if (!optionalTopographicPlace.isPresent()) {
            logger.warn("Got no topographic places back for StopPlace {} {}", stopPlace.getName(), stopPlace.getId());
        }

        optionalTopographicPlace.ifPresent(topographicPlace -> {
            logger.trace("Setting topographical ref {} on StopPlace {} {}", topographicPlace.getId(), stopPlace.getName(), stopPlace.getId());
            stopPlace.setTopographicPlace(topographicPlace);
        });
    }

    /**
     * Using cache
     */
    public Optional<TopographicPlace> findOrCreateTopographicPlace(List<TopographicPlace> incomingTopographicPlaces,
                                                                   TopographicPlaceRefStructure topographicPlaceRef,
                                                                   AtomicInteger topographicPlacesCreatedCounter) throws ExecutionException {
        if(topographicPlaceRef.getRef() == null) return Optional.empty();
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
                    if (topographicPlaceFromRef.getParentTopographicPlace() != null) {
                        //If topographic place has parent - check repo for already existing
                        Optional<TopographicPlace> parentTopographicPlaceInRepo = findTopoGraphicPlaceInRepository(topographicPlaceFromRef.getParentTopographicPlace());
                        if (parentTopographicPlaceInRepo.isPresent()) {
                            //Use already existing
                            topographicPlaceFromRef.setParentTopographicPlace(parentTopographicPlaceInRepo.get());
                        } else {
                            // Parent does not exist in repo - create and set
                            Optional<TopographicPlace> newParent = createNewTopographicPlace(incomingTopographicPlaces, topographicPlaceFromRef.getParentTopographicPlace(), topographicPlacesCreatedCounter);
                            if (newParent.isPresent()) {
                                topographicPlaceFromRef.setParentTopographicPlace(newParent.get());
                            }
                        }
                    }
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

        if(topographicPlaceFromRef.getParentTopographicPlaceRef() != null && topographicPlaceFromRef.getParentTopographicPlaceRef().getRef() != null ) {
            logger.debug("The topographic place contains reference to parent place '{}'", topographicPlaceFromRef.getParentTopographicPlaceRef().getRef());

            TopographicPlaceRefStructure parentRef = new TopographicPlaceRefStructure();
            parentRef.setRef(String.valueOf(topographicPlaceFromRef.getParentTopographicPlaceRef().getRef()));

            Optional<TopographicPlace> parentTopographicPlace = findOrCreateTopographicPlace(incomingTopographicPlaces,
                    parentRef, topographicPlacesCreatedCounter);

            parentTopographicPlace.ifPresent(parent -> {
                logger.debug("Found parent place '{}' for '{}'", parent.getName().getValue(), newTopographicPlace.getName().getValue());

                parentRef.setRef(String.valueOf(parent.getId()));
                newTopographicPlace.setParentTopographicPlaceRef(parentRef);

                newTopographicPlace.setParentTopographicPlace(parent);
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
