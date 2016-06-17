package no.rutebanken.tiamat.netexmapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeBuilder;
import no.rutebanken.netex.model.*;
import no.rutebanken.tiamat.model.AccessSpace;
import no.rutebanken.tiamat.model.Level;
import no.rutebanken.tiamat.model.Quay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class NetexSiteFrameMapper {

    private static final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
    private static final Logger logger = LoggerFactory.getLogger(NetexSiteFrameMapper.class);

    public NetexSiteFrameMapper() {

//        mapperFactory.classMap(no.rutebanken.tiamat.model.SiteFrame.class, no.rutebanken.netex.model.SiteFrame.class)
//                .field("stopPlaces.stopPlace{accessSpaces}",
//                        "stopPlaces.stopPlace{accessSpaces}")
//                .register();

        Type<ArrayList<AccessSpace>> arrayListType = new TypeBuilder<ArrayList<AccessSpace>>() {}.build();


        CustomMapper<ArrayList<AccessSpace>, AccessSpaces_RelStructure> accessSpaceMapper = new CustomMapper<ArrayList<AccessSpace>, AccessSpaces_RelStructure>() {
            @Override
            public void mapAtoB(ArrayList<AccessSpace> accessSpaces, AccessSpaces_RelStructure accessSpaces_relStructure, MappingContext context) {

                logger.debug("Mapping {} access spaces into accessSpaces_relStructure", accessSpaces.size());

                accessSpaces.forEach(accessSpace -> {
                            no.rutebanken.netex.model.AccessSpace netexAccessSpace = mapperFactory.getMapperFacade().map(accessSpace, no.rutebanken.netex.model.AccessSpace.class);
                            accessSpaces_relStructure.getAccessSpaceRefOrAccessSpace().add(netexAccessSpace);

                            System.out.println(accessSpace);
                            System.out.println(netexAccessSpace);
                        }
                );
            }
        };

        CustomMapper<ArrayList<Level>, Levels_RelStructure> levelsMapper = new CustomMapper<ArrayList<Level>, Levels_RelStructure>() {
            @Override
            public void mapAtoB(ArrayList<Level> levels, Levels_RelStructure levels_relStructure, MappingContext context) {

                logger.debug("Mapping {} access spaces into levels_RelStructure", levels.size());

                levels.forEach(accessSpace -> {
                            levels_relStructure.getLevelRefOrLevel().add(accessSpace);
                        }
                );
            }
        };

        CustomMapper<ArrayList<Quay>, Quays_RelStructure> quaysMapper = new CustomMapper<ArrayList<Quay>, Quays_RelStructure>() {
            @Override
            public void mapAtoB(ArrayList<Quay> quays, Quays_RelStructure quays_relStructure, MappingContext context) {

                logger.debug("Mapping {} access spaces into levels_RelStructure", quays.size());

                quays.forEach(accessSpace -> {
                            quays_relStructure.getQuayRefOrQuay().add(accessSpace);
                        }
                );
            }
        };

        CustomMapper<ArrayList<ValidityCondition>, ValidityConditions_RelStructure> validityConditionMapper = new CustomMapper<ArrayList<ValidityCondition>, ValidityConditions_RelStructure>() {
            @Override
            public void mapAtoB(ArrayList<ValidityCondition> validityConditions, ValidityConditions_RelStructure validityConditions_RelStructure, MappingContext context) {

                logger.debug("Mapping {} access spaces into levels_RelStructure", validityConditions.size());

                validityConditions.forEach(validityCondition -> {
                            validityConditions_RelStructure.getValidityConditionRefOrValidBetweenOrValidityCondition_().add(validityCondition);
                        }
                );
            }
        };

        CustomConverter<ArrayList<AccessSpace>, AccessSpaces_RelStructure> accessSpaceConverter = new CustomConverter<ArrayList<AccessSpace>, AccessSpaces_RelStructure>() {
            @Override
            public AccessSpaces_RelStructure convert(ArrayList<AccessSpace> accessSpaces, Type<? extends AccessSpaces_RelStructure> type) {

                System.out.println("Custom converter");

                accessSpaces.forEach(accessSpace -> {
//                            no.rutebanken.netex.model.AccessSpace
                        }
                );

                return null;
            }
        };

        mapperFactory.registerMapper(validityConditionMapper);
        mapperFactory.registerMapper(accessSpaceMapper);
        mapperFactory.registerMapper(levelsMapper);
        mapperFactory.registerMapper(quaysMapper);
    }

    public AccessSpaces_RelStructure mapAccessSpaceList(ArrayList<AccessSpace> accessSpaces) {
        AccessSpaces_RelStructure accessSpaces_RelStructure =  mapperFactory.getMapperFacade().map(accessSpaces, AccessSpaces_RelStructure.class);
        return accessSpaces_RelStructure;
    }

    public SiteFrame map(no.rutebanken.tiamat.model.SiteFrame tiamatSiteFrame) {
        SiteFrame siteFrame = mapperFactory.getMapperFacade().map(tiamatSiteFrame, SiteFrame.class);
        return siteFrame;
    }

}
