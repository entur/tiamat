package org.rutebanken.tiamat.netex.mapping;

import ma.glasnost.orika.*;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.rutebanken.netex.model.*;
import org.rutebanken.netex.model.AccessibilityAssessment;
import org.rutebanken.netex.model.DataManagedObjectStructure;
import org.rutebanken.netex.model.PathLink;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.TopographicPlace;
import org.rutebanken.tiamat.netex.mapping.converter.*;
import org.rutebanken.tiamat.netex.mapping.mapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NetexMapper {
    private static final Logger logger = LoggerFactory.getLogger(NetexMapper.class);
    private final MapperFacade facade;

    @Autowired
    public NetexMapper(List<Converter> converters, KeyListToKeyValuesMapMapper keyListToKeyValuesMapMapper,
                       DataManagedObjectStructureMapper dataManagedObjectStructureMapper,
                       NetexIdMapper netexIdMapper) {

        logger.info("Setting up netexMapper with DI");

        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        logger.info("Creating netex mapperFacade with {} converters ", converters.size());

        if(logger.isDebugEnabled()) {
            logger.debug("Converters: {}", converters);
        }

        converters.forEach(converter -> mapperFactory.getConverterFactory().registerConverter(converter));

        // Issues with registering multiple mappers
        mapperFactory.registerMapper(keyListToKeyValuesMapMapper);

        mapperFactory.classMap(SiteFrame.class, org.rutebanken.tiamat.model.SiteFrame.class)
                .byDefault()
                .register();

        mapperFactory.classMap(TopographicPlace.class, org.rutebanken.tiamat.model.TopographicPlace.class)
                .fieldBToA("name", "descriptor.name")
                .byDefault()
                .register();

        mapperFactory.classMap(StopPlace.class, org.rutebanken.tiamat.model.StopPlace.class)
                .fieldBToA("topographicPlace", "topographicPlaceRef")
                // TODO: Excluding some fields while waiting for NRP-1354
                .exclude("localServices")
                .exclude("placeEquipments")
                .exclude("postalAddress")
                .exclude("roadAddress")
                .byDefault()
                .register();

        mapperFactory.classMap(Quay.class, org.rutebanken.tiamat.model.Quay.class)
                .exclude("localServices")
                .exclude("placeEquipments")
                .exclude("postalAddress")
                .exclude("roadAddress")
                .byDefault()
                .register();

        mapperFactory.classMap(PathLinkEndStructure.class, org.rutebanken.tiamat.model.PathLinkEnd.class)
                .byDefault()
                .register();

        mapperFactory.classMap(PathLink.class, org.rutebanken.tiamat.model.PathLink.class)
                .byDefault()
                .register();

        mapperFactory.classMap(AccessibilityAssessment.class, org.rutebanken.tiamat.model.AccessibilityAssessment.class)
                .customize(new CustomMapper<AccessibilityAssessment, org.rutebanken.tiamat.model.AccessibilityAssessment>() {
                    @Override
                    public void mapAtoB(AccessibilityAssessment accessibilityAssessment, org.rutebanken.tiamat.model.AccessibilityAssessment accessibilityAssessment2, MappingContext context) {
                        super.mapAtoB(accessibilityAssessment, accessibilityAssessment2, context);
                        netexIdMapper.toTiamatModel(accessibilityAssessment, accessibilityAssessment2);
                    }

                    @Override
                    public void mapBtoA(org.rutebanken.tiamat.model.AccessibilityAssessment accessibilityAssessment, AccessibilityAssessment accessibilityAssessment2, MappingContext context) {
                        super.mapBtoA(accessibilityAssessment, accessibilityAssessment2, context);
                        netexIdMapper.toNetexModel(accessibilityAssessment, accessibilityAssessment2);
                    }
                })
                .exclude("id")
                .byDefault()
                .register();

        mapperFactory.classMap(DataManagedObjectStructure.class, org.rutebanken.tiamat.model.DataManagedObjectStructure.class)
                .fieldBToA("keyValues", "keyList")
                .field("validBetween", "validBetweens")
                .customize(dataManagedObjectStructureMapper)
                .exclude("id")
                .exclude("keyList")
                .exclude("keyValues")
                .exclude("version")
                .byDefault()
                .register();

        facade = mapperFactory.getMapperFacade();
    }

    public NetexMapper() {
        this(getDefaultConverters(),
                new KeyListToKeyValuesMapMapper(),
                new DataManagedObjectStructureMapper(new NetexIdMapper()),
                new NetexIdMapper());
        logger.info("Setting up netexMapper without DI");
    }

    public static List<Converter> getDefaultConverters() {
        List<Converter> converters = new ArrayList<>();
        converters.add(new AccessSpacesConverter());
        converters.add(new LevelsConverter());
        converters.add(new QuayListConverter());
        converters.add(new AlternativeNamesConverter());
        converters.add(new EquipmentPlacesConverter());
        converters.add(new ValidBetweenConverter());
        converters.add(new BoardingPositionsConverter());
        converters.add(new CheckConstraintsConverter());
        converters.add(new DestinationDisplayViewsConverter());
        converters.add(new ZonedDateTimeConverter());
        converters.add(new OffsetDateTimeZonedDateTimeConverter());
        converters.add(new SimplePointVersionStructureConverter());
        converters.add(new KeyValuesToKeyListConverter());
        converters.add(new AccessibilityLimitationsListConverter());
//        converters.add(new PathLinkEndConverter());
        return converters;
    }

    public TopographicPlace mapToNetexModel(org.rutebanken.tiamat.model.TopographicPlace topographicPlace) {
        return facade.map(topographicPlace, TopographicPlace.class);
    }

    public SiteFrame mapToNetexModel(org.rutebanken.tiamat.model.SiteFrame tiamatSiteFrame) {
        SiteFrame siteFrame = facade.map(tiamatSiteFrame, SiteFrame.class);
        return siteFrame;
    }

    public StopPlace mapToNetexModel(org.rutebanken.tiamat.model.StopPlace tiamatStopPlace) {
        return facade.map(tiamatStopPlace, StopPlace.class);
    }

    public org.rutebanken.tiamat.model.TopographicPlace mapToTiamatModel(TopographicPlace topographicPlace) {
        return facade.map(topographicPlace, org.rutebanken.tiamat.model.TopographicPlace.class);
    }

    public List<org.rutebanken.tiamat.model.StopPlace> mapStopsToTiamatModel(List<StopPlace> stopPlaces) {
        return facade.mapAsList(stopPlaces, org.rutebanken.tiamat.model.StopPlace.class);
    }
    public List<org.rutebanken.tiamat.model.PathLink> mapPathLinksToTiamatModel(List<PathLink> pathLinks) {
        return facade.mapAsList(pathLinks, org.rutebanken.tiamat.model.PathLink.class);
    }

    public org.rutebanken.tiamat.model.SiteFrame mapToTiamatModel(SiteFrame netexSiteFrame) {
        org.rutebanken.tiamat.model.SiteFrame tiamatSiteFrame = facade.map(netexSiteFrame, org.rutebanken.tiamat.model.SiteFrame.class);
        return tiamatSiteFrame;
    }

    public org.rutebanken.tiamat.model.StopPlace mapToTiamatModel(StopPlace netexStopPlace) {
        return facade.map(netexStopPlace, org.rutebanken.tiamat.model.StopPlace.class);
    }

    public org.rutebanken.tiamat.model.Quay mapToTiamatModel(Quay netexQuay) {
        return facade.map(netexQuay, org.rutebanken.tiamat.model.Quay.class);
    }

    public Quay mapToNetexModel(org.rutebanken.tiamat.model.Quay tiamatQuay) {
        return facade.map(tiamatQuay, Quay.class);
    }

    public PathLink mapToNetexModel(org.rutebanken.tiamat.model.PathLink pathLink) {
        return facade.map(pathLink, PathLink.class);
    }

    public MapperFacade getFacade() {
        return facade;
    }
}
