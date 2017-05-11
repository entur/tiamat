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
import java.util.List;
import java.util.stream.Collectors;

import static org.rutebanken.tiamat.model.VersionOfObjectRefStructure.ANY_VERSION;

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
        List<TariffZone> relatedTariffZones = importedNetexStopPlaces.stream()
                .filter(stopPlace -> stopPlace.getTariffZones() != null)
                .flatMap(stopPlace -> stopPlace.getTariffZones().getTariffZoneRef().stream())
                .peek(tariffZoneRef -> logger.debug("Looking at tariffZoneRef: {}", tariffZoneRef))
                .filter(tariffZoneRef -> {
                    if(responseSiteFrame.getTariffZones() == null || responseSiteFrame.getTariffZones().getTariffZone() == null) {
                        return true;
                    }

                    // Check tariffzones already added to the response site frame
                    return responseSiteFrame.getTariffZones()
                            .getTariffZone()
                            .stream()
                            .peek(tariffZone -> logger.debug("Tariffzone added: {} - {} ref candidate: Tariffzone ref {} - {}", tariffZone.getId(), tariffZone.getVersion(), tariffZoneRef.getRef(), tariffZoneRef.getVersion()))
                            .noneMatch(alreadyAddedTariffZone ->
                                    alreadyAddedTariffZone.getId().equals(tariffZoneRef.getRef())
                                    && (alreadyAddedTariffZone.getVersion().equals(tariffZoneRef.getVersion())
                                    || alreadyAddedTariffZone.getVersion() == null || tariffZoneRef.getVersion() == null));
                })
                .map(tariffZoneRef -> netexMapper.getFacade().map(tariffZoneRef, TariffZoneRef.class))
                .peek(mappedTariffZoneRef -> logger.debug("Resolving ref: {}", mappedTariffZoneRef))
                .map(mappedTariffZoneRef -> referenceResolver.resolve(mappedTariffZoneRef))
                .peek(tiamatTariffZone -> logger.debug("Resolved tariffZone: {}", tiamatTariffZone))
                .map(tiamatTariffZone -> netexMapper.getFacade().map(tiamatTariffZone, TariffZone.class))
                .distinct()
                .collect(Collectors.toList());

        if(responseSiteFrame.getTariffZones() != null) {
            responseSiteFrame.getTariffZones().getTariffZone().addAll(relatedTariffZones);
        } else if(!relatedTariffZones.isEmpty()){
            responseSiteFrame.withTariffZones(new TariffZonesInFrame_RelStructure().withTariffZone(relatedTariffZones));
        }
    }

}
