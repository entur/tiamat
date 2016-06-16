package no.rutebanken.tiamat.pelias;


import com.google.common.util.concurrent.Striped;
import com.vividsolutions.jts.geom.Point;
import no.rutebanken.tiamat.model.*;
import no.rutebanken.tiamat.pelias.model.Feature;
import no.rutebanken.tiamat.pelias.model.ReverseLookupResult;
import no.rutebanken.tiamat.repository.ifopt.StopPlaceRepository;
import no.rutebanken.tiamat.repository.ifopt.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Semaphore;

@Service
public class CountyAndMunicipalityLookupService {

    private static final Logger logger = LoggerFactory.getLogger(CountyAndMunicipalityLookupService.class);

    @Autowired
    private PeliasReverseLookupClient peliasReverseLookupClient;

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    private Striped<Semaphore> stripedSemaphores = Striped.lazyWeakSemaphore(19, 1);


    /**
     * Reverse lookup stop place centroid from Pelias.
     * References to topographical places for municipality and county on the stopPLace.
     */
    public void populateCountyAndMunicipality(StopPlace stopPlace) throws IOException, InterruptedException {

        Point point = stopPlace.getCentroid().getLocation().getGeometryPoint();

        ReverseLookupResult reverseLookupResult = peliasReverseLookupClient.reverseLookup(String.valueOf(point.getY()),
                String.valueOf(point.getX()), 1);

        if (reverseLookupResult.getFeatures().isEmpty()) {
            logger.warn("Got empty features list from Pelias reverse. {},{}", String.valueOf(point.getY()),
                    String.valueOf(point.getX()));
            return;
        }

        Feature feature = reverseLookupResult.getFeatures().get(0);

        String region = feature.getProperties().getRegion();
        String locality = feature.getProperties().getLocality();

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

            TopographicPlace county = createOrUseExistingCounty(counties, region);


            List<TopographicPlace> municipalities = topographicPlaceRepository
                    .findByNameValueAndCountryRefRefAndTopographicPlaceType(
                            locality,
                            IanaCountryTldEnumeration.NO,
                            TopographicPlaceTypeEnumeration.TOWN);

            municipality = createOrUseExistingMunicipality(municipalities, county, locality, region);
        } finally {
            stripedSemaphore.release();
        }
        TopographicPlaceRefStructure municipalityRef = new TopographicPlaceRefStructure();
        municipalityRef.setRef(municipality.getId());

        logger.trace("Setting reference to municipality {} : {} on stop place {}",
                municipality.getName(), municipalityRef.getRef(), stopPlace.getName());

        stopPlace.setTopographicPlaceRef(municipalityRef);
    }

    private TopographicPlace createOrUseExistingMunicipality(List<TopographicPlace> municipalities,
                                                             TopographicPlace county, String locality, String region) {

        TopographicPlace municipality;

        if (municipalities.isEmpty()) {
            logger.info("Creating new municipality for locality {}", locality);

            municipality = new TopographicPlace();
            municipality.setName(new MultilingualString(locality, "no", ""));
            municipality.setTopographicPlaceType(TopographicPlaceTypeEnumeration.TOWN);

            TopographicPlaceRefStructure countyRef = new TopographicPlaceRefStructure();
            countyRef.setRef(county.getId());

            CountryRef countryRef = new CountryRef();
            countryRef.setRef(IanaCountryTldEnumeration.NO);
            municipality.setCountryRef(countryRef);

            logger.info("Adding reference to county {} from municipality {}", region, locality);

            municipality.setParentTopographicPlaceRef(countyRef);
            topographicPlaceRepository.save(municipality);

        } else {
            municipality = municipalities.get(0);
            logger.info("Found existing municipality {} with id {}", municipality.getName(), municipality.getId());
        }
        return municipality;
    }

    private TopographicPlace createOrUseExistingCounty(List<TopographicPlace> counties, String region) {

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
        } else {
            county = counties.get(0);
            logger.info("Found existing county for region {}: {}", region, county.getId());
        }
        return county;
    }

}
