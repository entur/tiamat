package org.rutebanken.tiamat.autosys;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.entur.autosys.model.Kjoretoydata;
import org.entur.autosys.model.Kode;
import org.entur.autosys.model.TekniskeData;
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
        AtomicInteger mappedDeckPlanCount = new AtomicInteger();
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
        prepareDeckPlans(exportParams, kjoretoyList, mappedDeckPlanCount, netexResourceFrame);
        prepareVehicles(exportParams, kjoretoyList, mappedVehicleCount, netexResourceFrame);

        return publicationDeliveryCreator.createPublicationDelivery(netexCompositeFrame);

    }

    private void prepareDeckPlans(ExportParams exportParams, List<Kjoretoydata> kjoretoyList, AtomicInteger mappedDeckPlanCount, org.rutebanken.netex.model.ResourceFrame netexResourceFrame) {
        if (!kjoretoyList.isEmpty()) {
            logger.info("There are deck plans to export");

            DeckPlans_RelStructure deckPlansRelStructure = new DeckPlans_RelStructure();
            List<org.rutebanken.netex.model.DeckPlan> deckPlans = kjoretoyList.stream().map(vt -> {
                return new org.rutebanken.netex.model.DeckPlan()
                        .withId(deckPlanId(vt, kjoretoyList.indexOf(vt)))
                        .withVersion("1")
                        .withName(new MultilingualString().withContent(List.of(deckPlanName(vt))))
                        .withDescription(new MultilingualString().withContent(List.of(deckPlanDescription(vt))));
            }).toList();

            setField(DeckPlans_RelStructure.class, "deckPlan", deckPlansRelStructure, deckPlans);
            netexResourceFrame.setDeckPlans(deckPlansRelStructure);
        } else {
            logger.info("No deck plans to export");
        }

    }

    private String deckPlanName(Kjoretoydata vt) {
        if (vt.getGodkjenning() == null) return "";
        var godkjenning = vt.getGodkjenning();
        if (godkjenning.getTekniskGodkjenning() == null) return "";
        var tekniskGodkjenning = godkjenning.getTekniskGodkjenning();
        if (tekniskGodkjenning.getKjoretoyklassifisering() == null) return "";
        var tekniskeData = tekniskGodkjenning.getTekniskeData();
        if(tekniskeData == null) return "";
        var kaorsseriOgLasteplan = tekniskeData.getKarosseriOgLasteplan();
        if(kaorsseriOgLasteplan == null) return "";
        var karosseriType = kaorsseriOgLasteplan.getKarosseritype();
        if(karosseriType == null) return "";
        return karosseriType.getKodeNavn();
    }

    private void prepareVehicleModels(ExportParams exportParams, List<Kjoretoydata> kjoretoyList, AtomicInteger mappedVehicleModelCount, org.rutebanken.netex.model.ResourceFrame resourceFrame) {

        if (!kjoretoyList.isEmpty()) {
            logger.info("There are vehicle models to export");

            VehicleModelsInFrame_RelStructure vehicleModelsInFrameRelStructure = new VehicleModelsInFrame_RelStructure();
            List<org.rutebanken.netex.model.VehicleModel> vehicleModels = kjoretoyList.stream().map(vt -> {
                return new org.rutebanken.netex.model.VehicleModel()
                        .withId(vehicleModelId(vt, kjoretoyList.indexOf(vt)))
                        .withVersion("1")
                        .withDescription(new MultilingualString().withContent(List.of(modelDescription(vt))));
                }).toList();

            setField(VehicleModelsInFrame_RelStructure.class, "vehicleModel", vehicleModelsInFrameRelStructure, vehicleModels);
            resourceFrame.setVehicleModels(vehicleModelsInFrameRelStructure);
        } else {
            logger.info("No vehicle models to export");
        }
    }

    private String deckPlanDescription(Kjoretoydata vt) {
        if (vt.getGodkjenning() == null) return "";
        var godkjenning = vt.getGodkjenning();
        if (godkjenning.getTekniskGodkjenning() == null) return "";
        var tekniskGodkjenning = godkjenning.getTekniskGodkjenning();
        if (tekniskGodkjenning.getKjoretoyklassifisering() == null) return "";
        var tekniskeData = tekniskGodkjenning.getTekniskeData();
        if(tekniskeData == null) return "";
        var kaorsseriOgLasteplan = tekniskeData.getKarosseriOgLasteplan();
        if(kaorsseriOgLasteplan == null) return "";
        var karosseriType = kaorsseriOgLasteplan.getKarosseritype();
        if(karosseriType == null) return "";
        var generelt = tekniskeData.getGenerelt();
        if(generelt == null) return "";
        var merker = generelt.getMerke();
        if(merker == null || merker.isEmpty()) return "";
        var merke = merker.get(0);
        var handelsbetegnelse = generelt.getHandelsbetegnelse();
        return merke.getMerke() + (handelsbetegnelse == null || handelsbetegnelse.isEmpty() ? "" : " "  + handelsbetegnelse.getFirst()) + " - " + karosseriType.getKodeNavn();
    }

    private String deckPlanId(Kjoretoydata vt, int index) {
        if (vt.getGodkjenning() == null) return String.format("AUTOSYS:DeckPlan:%d", index);
        var godkjenning = vt.getGodkjenning();
        if (godkjenning.getTekniskGodkjenning() == null) return String.format("AUTOSYS:DeckPlan:%d", index);
        var tekniskGodkjenning = godkjenning.getTekniskGodkjenning();
        if (tekniskGodkjenning.getKjoretoyklassifisering() == null) return String.format("AUTOSYS:DeckPlan:%d", index);
        var kjoretoyklassifisering = tekniskGodkjenning.getKjoretoyklassifisering();
        if (kjoretoyklassifisering.getEfTypegodkjenning() == null) return String.format("AUTOSYS:DeckPlan:%d", index);
        var efTypegodkjenning = kjoretoyklassifisering.getEfTypegodkjenning();
        if (efTypegodkjenning.getTypegodkjenningNrTekst() == null) return String.format("AUTOSYS:DeckPlan:%d", index);
        var tekniskeData = tekniskGodkjenning.getTekniskeData();
        if(tekniskeData == null) return String.format("AUTOSYS:DeckPlan:%d", index);
        var kaorsseriOgLasteplan = tekniskeData.getKarosseriOgLasteplan();
        if(kaorsseriOgLasteplan == null) return String.format("AUTOSYS:DeckPlan:%d", index);
        var karosseriType = kaorsseriOgLasteplan.getKarosseritype();
        if(karosseriType == null) return String.format("AUTOSYS:DeckPlan:%d", index);
        return String.format("AUTOSYS:DeckPlan:%s", efTypegodkjenning.getTypegodkjenningNrTekst() + "-" + karosseriType.getKodeVerdi());
    }

    private String vehicleModelId(Kjoretoydata vt, int index) {
        if (vt.getGodkjenning() == null) return String.format("AUTOSYS:VehicleModel:%d", index);
        var godkjenning = vt.getGodkjenning();
        if (godkjenning.getTekniskGodkjenning() == null) return String.format("AUTOSYS:VehicleModel:%d", index);
        var tekniskGodkjenning = godkjenning.getTekniskGodkjenning();
        if (tekniskGodkjenning.getKjoretoyklassifisering() == null) return String.format("AUTOSYS:VehicleModel:%d", index);
        var kjoretoyklassifisering = tekniskGodkjenning.getKjoretoyklassifisering();
        if (kjoretoyklassifisering.getEfTypegodkjenning() == null) return String.format("AUTOSYS:VehicleModel:%d", index);
        var efTypegodkjenning = kjoretoyklassifisering.getEfTypegodkjenning();
        if (efTypegodkjenning.getTypegodkjenningNrTekst() == null) return String.format("AUTOSYS:VehicleModel:%d", index);
        return String.format("AUTOSYS:VehicleModel:%s", efTypegodkjenning.getTypegodkjenningNrTekst());
    }

    private String vehicleTypeId(Kjoretoydata vt, int index) {
        if (vt.getGodkjenning() == null) return String.format("AUTOSYS:VehicleType:%d", index);
        var godkjenning = vt.getGodkjenning();
        if (godkjenning.getTekniskGodkjenning() == null) return String.format("AUTOSYS:VehicleType:%d", index);
        var tekniskGodkjenning = godkjenning.getTekniskGodkjenning();
        if (tekniskGodkjenning.getKjoretoyklassifisering() == null) return String.format("AUTOSYS:VehicleType:%d", index);
        var kjoretoyklassifisering = tekniskGodkjenning.getKjoretoyklassifisering();
        if (kjoretoyklassifisering.getEfTypegodkjenning() == null) return String.format("AUTOSYS:VehicleType:%d", index);
        var efTypegodkjenning = kjoretoyklassifisering.getEfTypegodkjenning();
        if (efTypegodkjenning.getTypegodkjenningNrTekst() == null) return String.format("AUTOSYS:VehicleType:%d", index);
        return String.format("AUTOSYS:VehicleType:%s", efTypegodkjenning.getTypegodkjenningNrTekst());
    }

    private String modelDescription(Kjoretoydata vt) {
        if (vt.getGodkjenning() == null) return "";
        var godkjenning = vt.getGodkjenning();
        if (godkjenning.getTekniskGodkjenning() == null) return "";
        var tekniskGodkjenning = godkjenning.getTekniskGodkjenning();
        if (tekniskGodkjenning.getTekniskeData() == null) return "";
        var tekniskeData = tekniskGodkjenning.getTekniskeData();
        if (tekniskeData.getGenerelt() == null) return "";
        var generelt = tekniskeData.getGenerelt();
        if (generelt.getMerke() == null || generelt.getMerke().isEmpty()) return "";
        var merke = generelt.getMerke().get(0);
        if (generelt.getHandelsbetegnelse() == null) return merke.getMerke();
        return merke.getMerke() + generelt.getHandelsbetegnelse();
    }

    private void prepareVehicles(ExportParams exportParams, List<Kjoretoydata> kjoretoyList, AtomicInteger mappedVehicleCount, org.rutebanken.netex.model.ResourceFrame resourceFrame) {
        if (!kjoretoyList.isEmpty()) {
            logger.info("There are vehicles to export");

            VehiclesInFrame_RelStructure vehiclesInFrame_relStructure = new VehiclesInFrame_RelStructure();

            List<org.rutebanken.netex.model.Vehicle> vehicles = kjoretoyList.stream().map(kjoretoy -> {
                return new org.rutebanken.netex.model.Vehicle()
                        .withChassisNumber(kjoretoy.getKjoretoyId().getUnderstellsnummer())
                        .withRegistrationNumber(kjoretoy.getKjoretoyId().getKjennemerke())
                        .withId("AUTOSYS:Vehicle:" + kjoretoy.getKjoretoyId().getKjennemerke())
                        .withVersion("1")
                        .withTransportTypeRef(createVechicleTypeRef(kjoretoy, kjoretoyList.indexOf(kjoretoy)))
                        .withVehicleModelRef(createModelRef(kjoretoy, kjoretoyList.indexOf(kjoretoy)))
                        // .withOperationalNumber("what is this??")
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

    private VehicleModelRefStructure createModelRef(Kjoretoydata vt, int index) {
        VehicleModelRefStructure vehicleModelRefStructure = new VehicleModelRefStructure();
        vehicleModelRefStructure.setRef(vehicleModelId(vt, index));
        vehicleModelRefStructure.setVersion("1");
        return vehicleModelRefStructure;
    }

    private JAXBElement<? extends TransportTypeRefStructure> createVechicleTypeRef(Kjoretoydata vt, int index) {
        TransportTypeRefStructure transportTypeRefStructure = new TransportTypeRefStructure();
        transportTypeRefStructure.setRef(vehicleTypeId(vt, index));
        transportTypeRefStructure.setVersion("1");
        return netexObjectFactory.createTransportTypeRef(transportTypeRefStructure);
    }

    private DeckPlanRefStructure createDeckPlanRef(Kjoretoydata vt, int index) {
        DeckPlanRefStructure deckPlanRefStructure  = new DeckPlanRefStructure();
        deckPlanRefStructure.setRef(deckPlanId(vt, index));
        deckPlanRefStructure.setVersion("1");
        return deckPlanRefStructure;
    }

    private void prepareVehicleTypes(ExportParams exportParams, List<Kjoretoydata> kjoretoyList, AtomicInteger mappedVehicleTypeCount, org.rutebanken.netex.model.ResourceFrame resourceFrame) {
        // Override lists with custom iterator to be able to scroll database results on the fly.
        if (!kjoretoyList.isEmpty()) {
            logger.info("There are vehicle types to export");

            VehicleTypesInFrame_RelStructure vehicleTypesInFrameRelStructure = new VehicleTypesInFrame_RelStructure();
            List<org.rutebanken.netex.model.VehicleType> vehicleTypes = kjoretoyList.stream().map(vt -> {
                return mapOneVehicleType(vt, kjoretoyList.indexOf(vt));
            }).toList();

            List<JAXBElement<org.rutebanken.netex.model.VehicleType>> jaxbVehicleTypes = vehicleTypes.stream().map(vt -> new ObjectFactory().createVehicleType(vt)).toList();
            setField(VehicleTypesInFrame_RelStructure.class, "transportType_DummyType", vehicleTypesInFrameRelStructure, jaxbVehicleTypes);
            resourceFrame.setVehicleTypes(vehicleTypesInFrameRelStructure);
        } else {
            logger.info("No vehicle types to export");
        }
    }

    private VehicleType mapOneVehicleType(Kjoretoydata vt, int index) {
        var vehicleType = new org.rutebanken.netex.model.VehicleType()
                .withId(vehicleTypeId(vt, index))
                .withDeckPlanRef(createDeckPlanRef(vt, index))
                .withVersion("1");

        if (vt.getGodkjenning() == null) return vehicleType;
        var godkjenning = vt.getGodkjenning();
        if (godkjenning.getTekniskGodkjenning() == null) return vehicleType;
        var tekniskGodkjenning = godkjenning.getTekniskGodkjenning();
        if (tekniskGodkjenning.getTekniskeData() == null) return vehicleType;
        var tekniskeData = tekniskGodkjenning.getTekniskeData();

        var passengerCapacityStructure = mapPassengerCapacity(tekniskeData.getPersontall());
        if (passengerCapacityStructure != null) vehicleType.withPassengerCapacity(passengerCapacityStructure);

        var vekter = tekniskeData.getVekter();
        if(vekter != null && vekter.getEgenvekt() != null) {
            vehicleType.withWeight(BigDecimal.valueOf(vekter.getEgenvekt()));
        }

        var dimensjoner = tekniskeData.getDimensjoner();
        if(dimensjoner != null) {
            if(dimensjoner.getBredde() != null) vehicleType.withWidth(BigDecimal.valueOf(dimensjoner.getBredde()));
            if(dimensjoner.getLengde() != null) vehicleType.withLength(BigDecimal.valueOf(dimensjoner.getLengde()));
            if(dimensjoner.getHoyde() != null) vehicleType.withHeight(BigDecimal.valueOf(dimensjoner.getHoyde()));
        }

        var miljoData = tekniskeData.getMiljodata();
        if(miljoData != null) {
            if(miljoData.getEuroKlasse() != null) vehicleType.withEuroClass(miljoData.getEuroKlasse().getKodeVerdi());
            if(miljoData.getMiljoOgdrivstoffGruppe() != null) {
                vehicleType.withFuelTypes(miljoData.getMiljoOgdrivstoffGruppe().stream().map(this::mapFuelType).toList());
            }
        }

        var motorOgDrivverk = tekniskeData.getMotorOgDrivverk();
        if(motorOgDrivverk != null && motorOgDrivverk.getMotor() != null && !motorOgDrivverk.getMotor().isEmpty()) {
            vehicleType.withPropulsionTypes(motorOgDrivverk.getMotor().stream().map(this::mapPropulsionType).toList());

        }

        if (tekniskeData.getKarosseriOgLasteplan() != null && tekniskeData.getKarosseriOgLasteplan().getKarosseritype() != null) {
            vehicleType.withLowFloor(isLowFloor(tekniskeData.getKarosseriOgLasteplan().getKarosseritype()));
        }

        return vehicleType;
    }

    private PassengerCapacityStructure mapPassengerCapacity(TekniskeData.Persontall pt) {
        if(pt.getStaplasser() == null && pt.getSitteplasserTotalt() == null) return null;
        PassengerCapacityStructure passengerCapacityStructure = new PassengerCapacityStructure();

        if(pt.getSitteplasserTotalt() != null) passengerCapacityStructure.withSeatingCapacity(BigInteger.valueOf(pt.getSitteplasserTotalt()));
        if(pt.getStaplasser() != null) passengerCapacityStructure.withStandingCapacity(BigInteger.valueOf((pt.getStaplasser())));
        return passengerCapacityStructure;
    }

    private Boolean isLowFloor(Kode karosseritype) {
        if (karosseritype == null) {
            return null;
        }
        return switch (karosseritype.getKodeVerdi()) {
            case "CE", "CF", "CG", "CH", "CM", "CO", "CV" -> true;
            default -> false;
        };

    }

    private FuelTypeEnumeration mapFuelType(TekniskeData.MiljoOgdrivstoffGruppe dg) {
        switch (dg.getDrivstoffKodeMiljodata().getKodeVerdi().toUpperCase()) {
            case "1":  // Bensin
                return FuelTypeEnumeration.PETROL;
            case "2": // Diesel
                return FuelTypeEnumeration.DIESEL;
            case "3":  // Parafin
                return FuelTypeEnumeration.OTHER;
            case "4": // Gass
                return FuelTypeEnumeration.LIQUID_GAS;
            case "13", "18", "19", "22": // CNG-gass
                return FuelTypeEnumeration.NATURAL_GAS;
            case "ELEKTRISK", "5":
                return FuelTypeEnumeration.BATTERY;
            case "HYDROGEN", "6":
                return FuelTypeEnumeration.HYDROGEN;
            case "7":
                return FuelTypeEnumeration.PETROL_BATTERY_HYBRID;
            case "8":
                return FuelTypeEnumeration.DIESEL_BATTERY_HYBRID;
            case "10":
                return FuelTypeEnumeration.BIODIESEL;
            case "11":  // Biobensin ??
                return FuelTypeEnumeration.PETROL;
            case "12": // LPG
                return FuelTypeEnumeration.LIQUID_GAS;
            case "14": // Metanol
                return FuelTypeEnumeration.METHANE;
            case "15": // Etanol
                return FuelTypeEnumeration.ETHANOL;
            case "16": // LPG A
            case "17": // LPG B
                return FuelTypeEnumeration.LIQUID_GAS;
            case "20": // Komprimert luft
                return FuelTypeEnumeration.OTHER;
            case "21":
                return FuelTypeEnumeration.NATURAL_GAS;
            case "9":
            default:
                return FuelTypeEnumeration.OTHER;
        }

    }

    private PropulsionTypeEnumeration mapPropulsionType(TekniskeData.Motor md) {
        if(md.getArbeidsprinsipp() == null) return null;
        return switch (md.getArbeidsprinsipp().getKodeVerdi().toUpperCase()) {
            case "ELEKTRISK" -> PropulsionTypeEnumeration.ELECTRIC;
            case "ANNET" -> PropulsionTypeEnumeration.OTHER;
            case "DIESEL", "DIESEL_2_TAKTER", "DIESEL_4_TAKTER", "OTTO", "OTTO_2_TAKTER", "OTTO_4_TAKTER", "WANKEL" ->
                    PropulsionTypeEnumeration.COMBUSTION;
            default -> // "INGEN", annet
                    null;
        };
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
