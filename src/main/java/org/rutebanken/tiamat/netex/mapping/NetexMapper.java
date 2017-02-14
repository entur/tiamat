package org.rutebanken.tiamat.netex.mapping;

import ma.glasnost.orika.*;
import ma.glasnost.orika.impl.ConfigurableMapper;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.glassfish.jersey.internal.inject.Custom;
import org.rutebanken.netex.model.*;
import org.rutebanken.netex.model.DataManagedObjectStructure;
import org.rutebanken.netex.model.PathLink;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.TopographicPlace;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.netex.mapping.converter.*;
import org.rutebanken.tiamat.netex.mapping.mapper.DataManagedObjectStructureIdMapper;
import org.rutebanken.tiamat.netex.mapping.mapper.KeyListToKeyValuesMapMapper;
import org.rutebanken.tiamat.netex.mapping.mapper.TopographicPlaceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NetexMapper {
    private static final Logger logger = LoggerFactory.getLogger(NetexMapper.class);
    private final MapperFactory mapperFactory;

    @Autowired
    public NetexMapper(List<Converter> converters, List<Mapper> mappers, DataManagedObjectStructureIdMapper dataManagedObjectStructureIdMapper) {
        logger.info("Setting up netexMapper with DI");

        mapperFactory = new DefaultMapperFactory.Builder().build();
        logger.info("Creating netex mapper");

        mappers.forEach(mapper -> mapperFactory.registerMapper(mapper));
        converters.forEach(converter -> mapperFactory.getConverterFactory().registerConverter(converter));

        mapperFactory.classMap(SiteFrame.class, org.rutebanken.tiamat.model.SiteFrame.class)
                .byDefault()
                .register();

        mapperFactory.classMap(TopographicPlace.class, org.rutebanken.tiamat.model.TopographicPlace.class)
                .customize(new TopographicPlaceMapper())
                .byDefault()
                .register();

        mapperFactory.classMap(StopPlace.class, org.rutebanken.tiamat.model.StopPlace.class)
                .byDefault()
                .register();

        mapperFactory.classMap(Quay.class, org.rutebanken.tiamat.model.Quay.class)
                .byDefault()
                .register();

        mapperFactory.classMap(DataManagedObjectStructure.class, org.rutebanken.tiamat.model.DataManagedObjectStructure.class)
                .fieldBToA("keyValues", "keyList")
                .customize(dataManagedObjectStructureIdMapper)
                .exclude("id")
                .exclude("keyList")
                .exclude("keyValues")
                .byDefault()
                .register();
    }

    public NetexMapper() {
        this(getDefaultConverters(), getDefaultMappers(), new DataManagedObjectStructureIdMapper());
        logger.info("Setting up netexMapper without DI");
    }

    public static List<Converter> getDefaultConverters() {
        List<Converter> converters = new ArrayList<>();
        converters.add(new AccessSpacesConverter());
        converters.add(new LevelsConverter());
        converters.add(new QuayListConverter());
        converters.add(new AlternativeNamesConverter());
        converters.add(new EquipmentPlacesConverter());
        converters.add(new ValidityConditionsConverter());
        converters.add(new BoardingPositionsConverter());
        converters.add(new CheckConstraintsConverter());
        converters.add(new DestinationDisplayViewsConverter());
        converters.add(new ZonedDateTimeConverter());
        converters.add(new OffsetDateTimeZonedDateTimeConverter());
        converters.add(new SimplePointVersionStructureConverter());
        converters.add(new KeyValuesToKeyListConverter());
        converters.add(new PathLinkConverter());
        return converters;
    }

    public static List<Mapper> getDefaultMappers() {
        List<Mapper> mappers = new ArrayList<>();
        mappers.add(new KeyListToKeyValuesMapMapper());
        return mappers;
    }

    public SiteFrame mapToNetexModel(org.rutebanken.tiamat.model.SiteFrame tiamatSiteFrame) {
        SiteFrame siteFrame = mapperFactory.getMapperFacade().map(tiamatSiteFrame, SiteFrame.class);
        return siteFrame;
    }

    public StopPlace mapToNetexModel(org.rutebanken.tiamat.model.StopPlace tiamatStopPlace) {
        return mapperFactory.getMapperFacade().map(tiamatStopPlace, StopPlace.class);
    }

    public org.rutebanken.tiamat.model.TopographicPlace mapToTiamatModel(TopographicPlace topographicPlace) {
        return mapperFactory.getMapperFacade().map(topographicPlace, org.rutebanken.tiamat.model.TopographicPlace.class);
    }

    public List<org.rutebanken.tiamat.model.StopPlace> mapStopsToTiamatModel(List<StopPlace> stopPlaces) {
        return mapperFactory.getMapperFacade().mapAsList(stopPlaces, org.rutebanken.tiamat.model.StopPlace.class);
    }
    public List<org.rutebanken.tiamat.model.PathLink> mapPathLinksToTiamatModel(List<PathLink> pathLinks) {
        return mapperFactory.getMapperFacade().mapAsList(pathLinks, org.rutebanken.tiamat.model.PathLink.class);
    }

    public org.rutebanken.tiamat.model.SiteFrame mapToTiamatModel(SiteFrame netexSiteFrame) {
        org.rutebanken.tiamat.model.SiteFrame tiamatSiteFrame = mapperFactory.getMapperFacade().map(netexSiteFrame, org.rutebanken.tiamat.model.SiteFrame.class);
        return tiamatSiteFrame;
    }

    public org.rutebanken.tiamat.model.StopPlace mapToTiamatModel(StopPlace netexStopPlace) {
        return mapperFactory.getMapperFacade().map(netexStopPlace, org.rutebanken.tiamat.model.StopPlace.class);
    }

    public org.rutebanken.tiamat.model.Quay mapToTiamatModel(Quay netexQuay) {
        return mapperFactory.getMapperFacade().map(netexQuay, org.rutebanken.tiamat.model.Quay.class);
    }

    public Quay mapToNetexModel(org.rutebanken.tiamat.model.Quay tiamatQuay) {
        return mapperFactory.getMapperFacade().map(tiamatQuay, Quay.class);
    }

    public PathLink mapToNetexModel(org.rutebanken.tiamat.model.PathLink pathLink) {
        return mapperFactory.getMapperFacade().map(pathLink, PathLink.class);
    }
}
