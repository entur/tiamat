package org.rutebanken.tiamat.autosys;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.entur.autosys.model.Kjoretoydata;
import org.rutebanken.netex.model.*;
import org.rutebanken.tiamat.exporter.PublicationDeliveryCreator;
import org.rutebanken.tiamat.exporter.TiamatComositeFrameExporter;
import org.rutebanken.tiamat.exporter.TiamatResourceFrameExporter;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.model.ResourceFrame;
import org.rutebanken.tiamat.model.vehicle.CompositeFrame;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.xml.bind.JAXBElement;

@Service
public class MapperService {
    @PersistenceContext
    private EntityManager entityManager;

    private static final Logger logger = LoggerFactory.getLogger(MapperService.class);
    private final TiamatResourceFrameExporter tiamatResourceFrameExporter;
    private final TiamatComositeFrameExporter tiamatComositeFrameExporter;
    private final PublicationDeliveryCreator publicationDeliveryCreator;
    private final NetexMapper netexMapper;
    private static final ObjectFactory netexObjectFactory = new ObjectFactory();

    public MapperService(PublicationDeliveryCreator publicationDeliveryCreator,
                                            TiamatResourceFrameExporter tiamatResourceFrameExporter,
                                            TiamatComositeFrameExporter tiamatComositeFrameExporter,
                                            NetexMapper netexMapper) {
        this.publicationDeliveryCreator = publicationDeliveryCreator;
        this.tiamatResourceFrameExporter = tiamatResourceFrameExporter;
        this.tiamatComositeFrameExporter = tiamatComositeFrameExporter;
        this.netexMapper = netexMapper;
    }

    public PublicationDeliveryStructure exportPublicationDeliveryWithAutosysVehicle(ExportParams exportParams, List<Kjoretoydata> kjoretoyList) {
        logger.info("Preparing publication delivery export");

        final CompositeFrame compositeFrame = tiamatComositeFrameExporter.createTiamatCompositeFrame("Composite frame " + exportParams);
        final ResourceFrame resourceFrame = tiamatResourceFrameExporter.createTiamatResourceFrame("Resource frame " + exportParams);

        AtomicInteger mappedVehicleCount = new AtomicInteger();
        AtomicInteger mappedVehicleTypeCount = new AtomicInteger();
        AtomicInteger mappedVehicleModelCount = new AtomicInteger();
        logger.info("Mapping resource frame to netex model");
        final org.rutebanken.netex.model.ResourceFrame netexResourceFrame = netexMapper.mapToNetexModel(resourceFrame);
        logger.info("Mapping composite frame to netex model");
        org.rutebanken.netex.model.CompositeFrame netexCompositeFrame = netexMapper.mapToNetexModel(compositeFrame);

        Frames_RelStructure framesRelStructure = new Frames_RelStructure();
        framesRelStructure.withCommonFrame(new ObjectFactory().createResourceFrame(netexResourceFrame));
        netexCompositeFrame.withFrames(framesRelStructure);

        logger.info("Preparing scrollable iterators");
        prepareVehicleTypes(exportParams, kjoretoyList, mappedVehicleTypeCount, netexResourceFrame);
        prepareVehicleModels(exportParams, kjoretoyList, mappedVehicleModelCount, netexResourceFrame);
        prepareVehicles(exportParams, kjoretoyList, mappedVehicleCount, netexResourceFrame);

        return publicationDeliveryCreator.createPublicationDelivery(netexCompositeFrame);

    }

    private void prepareVehicleModels(ExportParams exportParams, List<Kjoretoydata> kjoretoyList, AtomicInteger mappedVehicleModelCount, org.rutebanken.netex.model.ResourceFrame resourceFrame) {

        if (!kjoretoyList.isEmpty()) {
            logger.info("There are vehicle models to export");

            VehicleModelsInFrame_RelStructure vehicleModelsInFrameRelStructure = new VehicleModelsInFrame_RelStructure();
            List<org.rutebanken.netex.model.VehicleModel> vehicleModels = kjoretoyList.stream().map(vt -> {
                return new org.rutebanken.netex.model.VehicleModel()
                        .withDescription(new MultilingualString().withContent(List.of(vt.getGodkjenning().getTekniskGodkjenning().getTekniskeData().getGenerelt().getMerke().get(0).getMerke() + vt.getGodkjenning().getTekniskGodkjenning().getTekniskeData().getGenerelt().getHandelsbetegnelse())));
                }).toList();

            setField(VehicleModelsInFrame_RelStructure.class, "vehicleModel", vehicleModelsInFrameRelStructure, vehicleModels);
            resourceFrame.setVehicleModels(vehicleModelsInFrameRelStructure);
        } else {
            logger.info("No vehicle models to export");
        }
    }

    private void prepareVehicles(ExportParams exportParams, List<Kjoretoydata> kjoretoyList, AtomicInteger mappedVehicleCount, org.rutebanken.netex.model.ResourceFrame resourceFrame) {
        if (!kjoretoyList.isEmpty()) {
            logger.info("There are vehicles to export");

            VehiclesInFrame_RelStructure vehiclesInFrame_relStructure = new VehiclesInFrame_RelStructure();

            List<org.rutebanken.netex.model.Vehicle> vehicles = kjoretoyList.stream().map(kjoretoy -> {
                return new org.rutebanken.netex.model.Vehicle()
                        .withChassisNumber(kjoretoy.getKjoretoyId().getUnderstellsnummer())
                        .withRegistrationNumber(kjoretoy.getKjoretoyId().getKjennemerke())
                        // .withOperationalNumber("what is this??")
                        // .withTransportTypeRef(transportTypeRefJAXBElement)
                        // .withActualVehicleEquipments(null) ??
                        // .withContactRef(null) ??
                        // .withEquipmentProfiles(null) ??
                        // .withTransportOrganisationRef(null) ??
                        // .withStatus(null) ??
                        // .withBrandingRef(null) ??
                        .withRegistrationDate(kjoretoy.getRegistrering().getFomTidspunkt().toLocalDateTime());
            }).toList();

            setField(VehiclesInFrame_RelStructure.class, "vehicle", vehiclesInFrame_relStructure, vehicles);
            resourceFrame.setVehicles(vehiclesInFrame_relStructure);
        } else {
            logger.info("No vehicles to export");
        }
    }

    private void prepareVehicleTypes(ExportParams exportParams, List<Kjoretoydata> kjoretoyList, AtomicInteger mappedVehicleTypeCount, org.rutebanken.netex.model.ResourceFrame resourceFrame) {
        // Override lists with custom iterator to be able to scroll database results on the fly.
        if (!kjoretoyList.isEmpty()) {
            logger.info("There are vehicle types to export");

            VehicleTypesInFrame_RelStructure vehicleTypesInFrameRelStructure = new VehicleTypesInFrame_RelStructure();
            List<org.rutebanken.netex.model.VehicleType> vehicleTypes = kjoretoyList.stream().map(vt -> {
                return new org.rutebanken.netex.model.VehicleType()
                        //.withId(netexIdHelper.getNetexId(kjoretoy, kjoretoy.getKjoretoyId().hashCode()))
                        .withVersion("1.0")
                        .withDescription(new MultilingualString().withContent(List.of("test vehicle type")));
            }).toList();

            List<JAXBElement<org.rutebanken.netex.model.VehicleType>> jaxbVehicleTypes = vehicleTypes.stream().map(vt -> new ObjectFactory().createVehicleType(vt)).toList();
            setField(VehicleTypesInFrame_RelStructure.class, "transportType_DummyType", vehicleTypesInFrameRelStructure, jaxbVehicleTypes);
            resourceFrame.setVehicleTypes(vehicleTypesInFrameRelStructure);
        } else {
            logger.info("No vehicle types to export");
        }
    }


    /**
     * Set field value with reflection.
     * Used for setting list values in netex model.
     */
    private void setField(Class clazz, String fieldName, Object instance, Object fieldValue) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(instance, fieldValue);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException("Cannot set field " + fieldName + " of " + instance, e);
        }
    }


}
