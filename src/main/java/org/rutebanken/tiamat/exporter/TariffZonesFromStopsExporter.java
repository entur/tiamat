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
                .filter(tariffZoneRef -> responseSiteFrame.getTariffZones() != null)
                .filter(tariffZoneRef -> responseSiteFrame.getTariffZones().getTariffZone() != null)
                .flatMap(stopPlace -> stopPlace.getTariffZones().getTariffZoneRef().stream())
                .peek(tariffZoneRef -> logger.debug("Looking at tariffZoneRef: {}", tariffZoneRef))
                .filter(tariffZoneRef -> responseSiteFrame.getTariffZones()
                        .getTariffZone()
                        .stream()
                        .peek(tariffZone -> logger.debug("Tariffzone: {} - Tariffzone ref {}", tariffZone.getId(), tariffZoneRef.getRef()))
                        .noneMatch(tariffZone -> tariffZone.getId().equals(tariffZoneRef.getRef())))
                .map(tariffZoneRef -> {
                    TariffZoneRef tiamatRef = new TariffZoneRef();
                    tiamatRef.setRef(tariffZoneRef.getRef());
                    return tiamatRef;
                })
                .map(tariffZoneRef -> referenceResolver.resolve(tariffZoneRef))
                .map(tiamatTariffZone -> netexMapper.getFacade().map(tiamatTariffZone, TariffZone.class))
                .collect(Collectors.toList());

        if(responseSiteFrame.getTariffZones() != null) {
            responseSiteFrame.getTariffZones().getTariffZone().addAll(relatedTariffZones);
        } else if(!relatedTariffZones.isEmpty()){
            responseSiteFrame.withTariffZones(new TariffZonesInFrame_RelStructure().withTariffZone(relatedTariffZones));
        }
    }

}
