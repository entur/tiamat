package org.rutebanken.tiamat.netexmapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.rutebanken.netex.model.DataManagedObjectStructure;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.tiamat.netexmapping.converters.*;
import org.rutebanken.tiamat.netexmapping.mapper.SiteFrameIdMapper;
import org.rutebanken.tiamat.netexmapping.mapper.QuayIdMapper;
import org.rutebanken.tiamat.netexmapping.mapper.StopPlaceIdMapper;
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
        mapperFactory.getConverterFactory().registerConverter(new QuayListConverter());
        mapperFactory.getConverterFactory().registerConverter(new AlternativeNamesConverter());
        mapperFactory.getConverterFactory().registerConverter(new EquipmentPlacesConverter());
        mapperFactory.getConverterFactory().registerConverter(new TariffZonesConverter());
        mapperFactory.getConverterFactory().registerConverter(new ValidityConditionsConverter());
        mapperFactory.getConverterFactory().registerConverter(new BoardinPositionsConverter());
        mapperFactory.getConverterFactory().registerConverter(new CheckConstraintsConverter());
        mapperFactory.getConverterFactory().registerConverter(new DestinationDisplayViewsConverter());
        mapperFactory.getConverterFactory().registerConverter(new ZonedDateTimeConverter());
        mapperFactory.getConverterFactory().registerConverter(new OffsetDateTimeZonedDateTimeConverter());
        mapperFactory.getConverterFactory().registerConverter("keyValConverter", new KeyValueConverter());

        mapperFactory.classMap(SiteFrame.class, org.rutebanken.tiamat.model.SiteFrame.class)
                .customize(new SiteFrameIdMapper())
                .exclude("id")
                .byDefault()
                .register();

        mapperFactory.classMap(StopPlace.class, org.rutebanken.tiamat.model.StopPlace.class)
                .customize(new StopPlaceIdMapper())
                .exclude("id")
                .byDefault()
                .register();

        mapperFactory.classMap(Quay.class, org.rutebanken.tiamat.model.Quay.class)
                .customize(new QuayIdMapper())
                .exclude("id")
                .byDefault()
                .register();

        mapperFactory.classMap(DataManagedObjectStructure.class, org.rutebanken.tiamat.model.DataManagedObjectStructure.class)
                .fieldMap("keyList", "keyValues").converter("keyValConverter").add();


    }

    public SiteFrame mapToNetexModel(org.rutebanken.tiamat.model.SiteFrame tiamatSiteFrame) {
        SiteFrame siteFrame = mapperFactory.getMapperFacade().map(tiamatSiteFrame, SiteFrame.class);
        return siteFrame;
    }

    public StopPlace mapToNetexModel(org.rutebanken.tiamat.model.StopPlace tiamatStopPlace) {
        return mapperFactory.getMapperFacade().map(tiamatStopPlace, StopPlace.class);
    }

    public org.rutebanken.tiamat.model.SiteFrame mapToTiamatModel(SiteFrame netexSiteFrame) {
        org.rutebanken.tiamat.model.SiteFrame tiamatSiteFrame = mapperFactory.getMapperFacade().map(netexSiteFrame, org.rutebanken.tiamat.model.SiteFrame.class);
        return tiamatSiteFrame;
    }

    public org.rutebanken.tiamat.model.StopPlace mapToTiamatModel(StopPlace netexStopPlace) {
        org.rutebanken.tiamat.model.StopPlace stopPlace = mapperFactory.getMapperFacade().map(netexStopPlace, org.rutebanken.tiamat.model.StopPlace.class);
        return stopPlace;
    }

    public org.rutebanken.tiamat.model.Quay mapToTiamatModel(Quay netexQuay) {
        return mapperFactory.getMapperFacade().map(netexQuay, org.rutebanken.tiamat.model.Quay.class);
    }

    public Quay mapToNetexModel(org.rutebanken.tiamat.model.Quay tiamatQuay) {
        return mapperFactory.getMapperFacade().map(tiamatQuay, Quay.class);
    }
}
