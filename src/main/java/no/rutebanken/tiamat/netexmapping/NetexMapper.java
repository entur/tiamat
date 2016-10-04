package no.rutebanken.tiamat.netexmapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import no.rutebanken.netex.model.SiteFrame;
import no.rutebanken.netex.model.StopPlace;
import no.rutebanken.tiamat.netexmapping.converters.*;
import no.rutebanken.tiamat.netexmapping.mapper.StopPlaceIdMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NetexMapper {

    private static final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
    private static final Logger logger = LoggerFactory.getLogger(NetexMapper.class);

    static {
        logger.info("Creating netex mapper");
        mapperFactory.getConverterFactory().registerConverter(new AccessSpacesConverter());
        mapperFactory.getConverterFactory().registerConverter(new LevelsConverter());
        mapperFactory.getConverterFactory().registerConverter(new QuaysConverter());
        mapperFactory.getConverterFactory().registerConverter(new AlternativeNamesConverter());
        mapperFactory.getConverterFactory().registerConverter(new EquipmentPlacesConverter());
        mapperFactory.getConverterFactory().registerConverter(new TariffZonesConverter());
        mapperFactory.getConverterFactory().registerConverter(new ValidityConditionsConverter());
        mapperFactory.getConverterFactory().registerConverter(new BoardinPositionsConverter());
        mapperFactory.getConverterFactory().registerConverter(new CheckConstraintsConverter());
        mapperFactory.getConverterFactory().registerConverter(new DestinationDisplayViewsConverter());
        mapperFactory.getConverterFactory().registerConverter(new ZonedDateTimeConverter());
        mapperFactory.getConverterFactory().registerConverter(new OffsetDateTimeZonedDateTimeConverter());

        mapperFactory.classMap(StopPlace.class, no.rutebanken.tiamat.model.StopPlace.class)
                .customize(new StopPlaceIdMapper())
                .exclude("id")
                .byDefault()
                .register();
    }

    public SiteFrame mapToNetexModel(no.rutebanken.tiamat.model.SiteFrame tiamatSiteFrame) {
        SiteFrame siteFrame = mapperFactory.getMapperFacade().map(tiamatSiteFrame, SiteFrame.class);
        return siteFrame;
    }

    public StopPlace mapToNetexModel(no.rutebanken.tiamat.model.StopPlace tiamatStopPlace) {
        return mapperFactory.getMapperFacade().map(tiamatStopPlace, StopPlace.class);
    }

    public no.rutebanken.tiamat.model.SiteFrame mapToTiamatModel(SiteFrame netexSiteFrame) {
        no.rutebanken.tiamat.model.SiteFrame tiamatSiteFrame = mapperFactory.getMapperFacade().map(netexSiteFrame, no.rutebanken.tiamat.model.SiteFrame.class);
        return tiamatSiteFrame;
    }

    public no.rutebanken.tiamat.model.StopPlace mapToTiamatModel(StopPlace netexStopPlace) {
        no.rutebanken.tiamat.model.StopPlace stopPlace = mapperFactory.getMapperFacade().map(netexStopPlace, no.rutebanken.tiamat.model.StopPlace.class);
        return stopPlace;
    }
}
