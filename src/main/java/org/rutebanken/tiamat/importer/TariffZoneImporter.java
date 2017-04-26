package org.rutebanken.tiamat.importer;

import org.rutebanken.netex.model.TariffZone;
import org.rutebanken.netex.model.TopographicPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.TariffZoneRepository;
import org.rutebanken.tiamat.versioning.TariffZoneVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.toList;

@Transactional
@Component
public class TariffZoneImporter {

    private static final Logger logger = LoggerFactory.getLogger(TariffZoneImporter.class);

    @Autowired
    private NetexMapper netexMapper;

    @Autowired
    private TariffZoneVersionedSaverService tariffZoneVersionedSaverService;

    public List<TariffZone> importTariffZones(List<org.rutebanken.tiamat.model.TariffZone> tariffZones) {

        return tariffZones
                .stream()
                .map(incomingTariffZone -> tariffZoneVersionedSaverService.saveNewVersion(incomingTariffZone))
                .map(savedTariffZone -> netexMapper.getFacade().map(savedTariffZone, TariffZone.class))
                .collect(toList());
    }

}
