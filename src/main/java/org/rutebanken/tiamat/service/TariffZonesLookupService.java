package org.rutebanken.tiamat.service;


import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TariffZone;
import org.rutebanken.tiamat.model.TariffZoneRef;
import org.rutebanken.tiamat.repository.TariffZoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Service
@Transactional
public class TariffZonesLookupService {

    private static final Logger logger = LoggerFactory.getLogger(TariffZonesLookupService.class);

    private final Supplier<List<Pair<String, Polygon>>> tariffZones = Suppliers.memoizeWithExpiration(getTariffZones(), 10, TimeUnit.HOURS);

    private final TariffZoneRepository tariffZoneRepository;

    @Autowired
    public TariffZonesLookupService(TariffZoneRepository tariffZoneRepository) {
        this.tariffZoneRepository = tariffZoneRepository;
    }

    public void populateTariffZone(StopPlace stopPlace) {
        if(stopPlace.getCentroid() != null) {

            Set<TariffZoneRef> matches = findTariffZones(stopPlace.getCentroid())
                    .stream()
                    .filter(tariffZone -> stopPlace.getTariffZones() == null ? true : stopPlace.getTariffZones()
                            .stream()
                            .noneMatch(tariffZoneRef -> tariffZone.getNetexId().equals(tariffZoneRef.getRef())))
                    .map(tariffZone -> new TariffZoneRef(tariffZone.getNetexId()))
                    .collect(toSet());

            if(stopPlace.getTariffZones() == null) {
                stopPlace.setTariffZones(new HashSet<>());
            }
            stopPlace.getTariffZones().addAll(matches);

        }
    }

    public List<TariffZone> findTariffZones(Point point) {
        return tariffZones.get()
                       .stream()
                       .filter(pair -> point.within(pair.getSecond()))
                       .map(pair -> tariffZoneRepository.findFirstByNetexIdOrderByVersionDesc(pair.getFirst()))
                       .filter(tariffZone -> tariffZone != null)
                       .collect(toList());
    }

    public Supplier<List<Pair<String, Polygon>>> getTariffZones() {
        return () -> {
            logger.info("Fetching and memoizing tariff zones from repository");
            return tariffZoneRepository.findAll()
                    .stream()
                    .filter(tariffZone -> tariffZone.getPolygon() != null)
                    .collect(
                            groupingBy(TariffZone::getNetexId,
                                    maxBy((TariffZone tz1, TariffZone tz2) -> Long.compare(tz1.getVersion(), tz2.getVersion()))))
                    .values()
                    .stream()
                    .filter(Optional::isPresent)
                    .map(optional -> optional.get())
                    .peek(tariffZone -> logger.info("Memoizing tariff zone {} {}", tariffZone.getNetexId(), tariffZone.getVersion()))
                    .map(tariffZone -> Pair.of(tariffZone.getNetexId(), tariffZone.getPolygon()))
                    .collect(toList());

        };
    }


}
