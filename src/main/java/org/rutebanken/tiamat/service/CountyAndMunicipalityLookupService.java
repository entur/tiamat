package org.rutebanken.tiamat.service;


import com.google.common.util.concurrent.Striped;
import com.vividsolutions.jts.geom.Point;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.pelias.PeliasReverseLookupClient;
import org.rutebanken.tiamat.pelias.model.Properties;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.rutebanken.tiamat.versioning.VersionIncrementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class CountyAndMunicipalityLookupService {

    private static final Logger logger = LoggerFactory.getLogger(CountyAndMunicipalityLookupService.class);

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;

    public void populateCountyAndMunicipality(StopPlace stopPlace) throws IOException, InterruptedException {

        if (!stopPlace.hasCoordinates()) {
            return;
        }





    }
}
