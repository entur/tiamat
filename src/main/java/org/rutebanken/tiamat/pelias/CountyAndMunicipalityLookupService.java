package org.rutebanken.tiamat.pelias;


import com.google.common.util.concurrent.Striped;
import com.vividsolutions.jts.geom.Point;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.pelias.model.Feature;
import org.rutebanken.tiamat.pelias.model.ReverseLookupResult;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CountyAndMunicipalityLookupService {

    private static final Logger logger = LoggerFactory.getLogger(CountyAndMunicipalityLookupService.class);

    @Autowired
    private PeliasReverseLookupClient peliasReverseLookupClient;

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;
    
    private Striped<Semaphore> stripedSemaphores = Striped.lazyWeakSemaphore(19, 1);


    /**
     * Reverse lookup stop place centroid from Pelias.
     * References to topographical places for municipality and county on the stopPLace.
     */
    public void populateCountyAndMunicipality(StopPlace stopPlace, AtomicInteger topographicPlacesCreatedCounter) throws IOException, InterruptedException {

        Point point = stopPlace.getCentroid().getLocation().getGeometryPoint();

        ReverseLookupResult reverseLookupResult = peliasReverseLookupClient.reverseLookup(String.valueOf(point.getY()),
                String.valueOf(point.getX()), 1);

        if (reverseLookupResult.getFeatures().isEmpty()) {
            logger.warn("Got empty features list from Pelias reverse. {},{}", String.valueOf(point.getY()),
                    String.valueOf(point.getX()));
            return;
        }

        Feature feature = reverseLookupResult.getFeatures().get(0);

        String region = feature.getProperties().getCounty();
        String locality = feature.getProperties().getLocaladmin();

        logger.trace("Got region {} and locality {}", region, locality);

        if (region == null) {
            logger.warn("'region' was null from Pelias for stop place {}. Ignoring.", stopPlace.getName());
            return;
        }

        if (locality == null) {
            logger.warn("'locality' was null from Pelias for stop place {}. Ignoring.", stopPlace.getName());
            return;
        }

        TopographicPlace municipality;

        Semaphore stripedSemaphore = stripedSemaphores.get(region);
        stripedSemaphore.acquire();
        try {
            List<TopographicPlace> counties = topographicPlaceRepository
                    .findByNameValueAndCountryRefRefAndTopographicPlaceType(
                            region,
                            IanaCountryTldEnumeration.NO,
                            TopographicPlaceTypeEnumeration.COUNTY);
            logger.info("Got {} counties for region {} from repository", counties.size(), region);

            TopographicPlace county = createOrUseExistingCounty(counties, region, topographicPlacesCreatedCounter);

            List<TopographicPlace> municipalities = topographicPlaceRepository
                    .findByNameValueAndCountryRefRefAndTopographicPlaceType(
                            locality,
                            IanaCountryTldEnumeration.NO,
                            TopographicPlaceTypeEnumeration.TOWN);

            logger.info("Got {} municipalities for locality {} from repository", counties.size(), locality);

            municipality = createOrUseExistingMunicipality(municipalities, county, locality, region, topographicPlacesCreatedCounter);
        } finally {
            logger.debug("Releasing semaphore for region {}", region);
            stripedSemaphore.release();
        }
        TopographicPlaceRefStructure municipalityRef = new TopographicPlaceRefStructure();
        municipalityRef.setRef(String.valueOf(municipality.getId()));

        logger.trace("Setting reference to municipality {} : {} to stop place {}",
                municipality.getName(), municipalityRef.getRef(), stopPlace.getName());

        stopPlace.setTopographicPlaceRef(municipalityRef);
    }

    private TopographicPlace createOrUseExistingMunicipality(List<TopographicPlace> municipalities,
                                                             TopographicPlace county, String locality, String region, AtomicInteger topographicPlacesCreatedCounter) {

        TopographicPlace municipality;

        if (municipalities.isEmpty()) {
            logger.debug("Creating new municipality for locality {}", locality);

            municipality = new TopographicPlace();
            municipality.setName(new MultilingualString(locality, "no", ""));
            municipality.setTopographicPlaceType(TopographicPlaceTypeEnumeration.TOWN);

            TopographicPlaceRefStructure countyRef = new TopographicPlaceRefStructure();
            countyRef.setRef(String.valueOf(county.getId()));

            CountryRef countryRef = new CountryRef();
            countryRef.setRef(IanaCountryTldEnumeration.NO);
            municipality.setCountryRef(countryRef);

            logger.debug("Adding reference to county {} from municipality {}", region, locality);

            municipality.setParentTopographicPlaceRef(countyRef);
            topographicPlaceRepository.save(municipality);
            topographicPlacesCreatedCounter.incrementAndGet();
            logger.info("Created municipality {} with id: {}, referencing county {}", locality, municipality.getId(), county.getId());

        } else {
            municipality = municipalities.get(0);
            logger.info("Found existing municipality {} with id {}", municipality.getName(), municipality.getId());
        }
        return municipality;
    }

    private TopographicPlace createOrUseExistingCounty(List<TopographicPlace> counties, String region, AtomicInteger topographicPlacesCreatedCounter) {

        TopographicPlace county;

        if (counties.isEmpty()) {

            logger.info("Creating new county for region {}", region);
            county = new TopographicPlace();
            county.setName(new MultilingualString(region, "no", ""));
            county.setTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY);

            CountryRef countryRef = new CountryRef();
            countryRef.setRef(IanaCountryTldEnumeration.NO);
            county.setCountryRef(countryRef);

            topographicPlaceRepository.save(county);
            topographicPlacesCreatedCounter.incrementAndGet();
            logger.info("Created county {} with id: {}", region, county.getId());
        } else {
            county = counties.get(0);
            logger.info("Found existing county for region {}: {}", region, county.getId());
        }
        return county;
    }

}
