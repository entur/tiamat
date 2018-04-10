/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.service;


import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import org.rutebanken.tiamat.general.ResettableMemoizer;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TariffZone;
import org.rutebanken.tiamat.model.TariffZoneRef;
import org.rutebanken.tiamat.repository.TariffZoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static java.util.stream.Collectors.*;

@Service
@Transactional
public class TariffZonesLookupService {

    private static final Logger logger = LoggerFactory.getLogger(TariffZonesLookupService.class);

    private final ResettableMemoizer<List<Pair<String, Polygon>>> tariffZones = new ResettableMemoizer<>(getTariffZones());

    private final TariffZoneRepository tariffZoneRepository;

    private final boolean removeExistingReferences;

    @Autowired
    public TariffZonesLookupService(TariffZoneRepository tariffZoneRepository,
                                    @Value("${tariffzoneLookupService.resetReferences:false}") boolean removeExistingReferences) {
        this.tariffZoneRepository = tariffZoneRepository;
        this.removeExistingReferences = removeExistingReferences;
    }

    public boolean populateTariffZone(StopPlace stopPlace) {
        if(stopPlace.getCentroid() != null) {

            if(stopPlace.getTariffZones() == null) {
                stopPlace.setTariffZones(new HashSet<>());
            }

            Set<String> refsBefore = mapToIdStrings(stopPlace.getTariffZones());

            if(removeExistingReferences) {
                stopPlace.getTariffZones().clear();
            }

            Set<TariffZoneRef> matches = findTariffZones(stopPlace.getCentroid())
                    .stream()
                    .filter(tariffZone -> stopPlace.getTariffZones().isEmpty() ? true : stopPlace.getTariffZones()
                            .stream()
                            .noneMatch(tariffZoneRef -> tariffZone.getNetexId().equals(tariffZoneRef.getRef())))
                    .map(tariffZone -> new TariffZoneRef(tariffZone.getNetexId()))
                    .collect(toSet());

            stopPlace.getTariffZones().addAll(matches);

            Set<String> refsAfter = mapToIdStrings(stopPlace.getTariffZones());

            return !Sets.symmetricDifference(refsBefore, refsAfter).isEmpty();
        }
        return false;
    }

    private Set<String> mapToIdStrings(Set<TariffZoneRef> tariffZoneRefs) {
        return tariffZoneRefs.stream().map(tzr -> tzr.getRef()).collect(toSet());
    }

    public List<TariffZone> findTariffZones(Point point) {
        return tariffZones.get()
                       .stream()
                       .filter(pair -> point.coveredBy(pair.getSecond()))
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
                    .peek(tariffZone -> logger.debug("Memoizing tariff zone {} {}", tariffZone.getNetexId(), tariffZone.getVersion()))
                    .map(tariffZone -> Pair.of(tariffZone.getNetexId(), tariffZone.getPolygon()))
                    .collect(toList());

        };
    }

    public void reset() {
        tariffZones.reset();
    }


}
