package org.rutebanken.tiamat.exporter;

import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.TariffZone;
import org.rutebanken.netex.model.TariffZonesInFrame_RelStructure;
import org.rutebanken.tiamat.model.TariffZoneRef;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.ReferenceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Transactional
public class TariffZonesFromStopsExporter {

    private static final Logger logger = LoggerFactory.getLogger(TariffZonesFromStopsExporter.class);

    @Autowired
    private NetexMapper netexMapper;

    @Autowired
    private ReferenceResolver referenceResolver;

    /**
     * Resolve and add relevant tariffzones to the response site frame.
     * If a tariffzone already exists in the response, it's not added twice.
     *
     * @param importedNetexStopPlaces stop places that were imported, that could reference to tariff zones
     * @param responseSiteFrame the site fra to append or add tariff zones to
     */
    public void resolveTariffZones(Collection<StopPlace> importedNetexStopPlaces, SiteFrame responseSiteFrame) {

        Map<String, TariffZone> tariffZoneMap = new HashMap<>();

        if(responseSiteFrame.getTariffZones() != null && responseSiteFrame.getTariffZones().getTariffZone() != null) {
            responseSiteFrame.getTariffZones().getTariffZone()
                    .forEach(tariffZone -> tariffZoneMap.put(key(tariffZone.getId(), tariffZone.getVersion()), tariffZone));
        }

        importedNetexStopPlaces.stream()
                .filter(stopPlace -> stopPlace.getTariffZones() != null)
                .flatMap(stopPlace -> stopPlace.getTariffZones().getTariffZoneRef().stream())
                .filter(tariffZoneRef -> !tariffZoneMap.containsKey(key(tariffZoneRef.getRef(), tariffZoneRef.getVersion())))
                .map(tariffZoneRef -> netexMapper.getFacade().map(tariffZoneRef, TariffZoneRef.class))
                .peek(mappedTariffZoneRef -> logger.debug("Resolving ref: {}", mappedTariffZoneRef))
                .map(mappedTariffZoneRef -> referenceResolver.resolve(mappedTariffZoneRef))
                .peek(tiamatTariffZone -> logger.debug("Resolved tariffZone: {}", tiamatTariffZone))
                .map(tiamatTariffZone -> netexMapper.getFacade().map(tiamatTariffZone, TariffZone.class))
                .forEach(tariffZone -> tariffZoneMap.put(key(tariffZone.getId(), tariffZone.getVersion()), tariffZone));

        responseSiteFrame.withTariffZones(new TariffZonesInFrame_RelStructure().withTariffZone(tariffZoneMap.values()));
    }

    private String key(String id, String version) {
        return id + "-" + version;
    }

}
