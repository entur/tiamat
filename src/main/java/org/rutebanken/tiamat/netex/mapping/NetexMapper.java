/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.netex.mapping;

import ma.glasnost.orika.Converter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.rutebanken.netex.model.*;
import org.rutebanken.tiamat.netex.mapping.mapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NetexMapper {
    private static final Logger logger = LoggerFactory.getLogger(NetexMapper.class);
    private final MapperFacade facade;

    @Autowired
    public NetexMapper(List<Converter> converters, KeyListToKeyValuesMapMapper keyListToKeyValuesMapMapper,
                       DataManagedObjectStructureMapper dataManagedObjectStructureMapper,
                       PublicationDeliveryHelper publicationDeliveryHelper,
                       AccessibilityAssessmentMapper accessibilityAssessmentMapper) {

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

        mapperFactory.classMap(MultilingualString.class, org.rutebanken.tiamat.model.EmbeddableMultilingualString.class)
                .customize(new MultilingualStringMapper())
                .byDefault()
                .register();

        mapperFactory.classMap(GroupOfStopPlaces.class, org.rutebanken.tiamat.model.GroupOfStopPlaces.class)
                .byDefault()
                .fieldBToA("purposeOfGrouping", "purposeOfGroupingRef")
                .fieldAToB("purposeOfGroupingRef.ref", "purposeOfGrouping.name")
                .customize(new GroupOfStopPlacesMapper())
                .register();

        mapperFactory.classMap(PurposeOfGrouping.class, org.rutebanken.tiamat.model.PurposeOfGrouping.class)
                .byDefault()
                .register();

        mapperFactory.classMap(GroupOfTariffZones.class, org.rutebanken.tiamat.model.GroupOfTariffZones.class)
                .byDefault()
                .customize(new GroupOfTariffZonesMapper())
                .register();


        mapperFactory.classMap(StopPlace.class, org.rutebanken.tiamat.model.StopPlace.class)
                .fieldBToA("topographicPlace", "topographicPlaceRef")
                .fieldAToB("topographicPlaceRef.ref", "topographicPlace.netexId")
                .fieldAToB("topographicPlaceRef.version", "topographicPlace.version")
                .exclude("localServices")
                .exclude("postalAddress")
                .exclude("roadAddress")
                .customize(new StopPlaceMapper(publicationDeliveryHelper))
                .byDefault()
                .register();

        mapperFactory.classMap(Quay.class, org.rutebanken.tiamat.model.Quay.class)
                .exclude("localServices")
                .exclude("postalAddress")
                .exclude("roadAddress")
                .customize(new QuayMapper())
                .byDefault()
                .register();

        mapperFactory.classMap(TariffZone.class, org.rutebanken.tiamat.model.TariffZone.class)
                .byDefault()
                .register();

        mapperFactory.classMap(FareZone.class, org.rutebanken.tiamat.model.FareZone.class)
                .exclude("transportOrganisationRef")
                .exclude("neighbours")
                .exclude("members")
                .customize(new FareZoneMapper())
                .byDefault()
                .register();


        mapperFactory.classMap(Parking.class, org.rutebanken.tiamat.model.Parking.class)
                .exclude("paymentMethods")
                .exclude("cardsAccepted")
                .exclude("currenciesAccepted")
                .exclude("accessModes")
                .fieldBToA("netexId", "id")
                .customize(new ParkingMapper())
                .byDefault()
                .register();

        mapperFactory.classMap(Vehicle.class, org.rutebanken.tiamat.model.vehicle.Vehicle.class)
                .exclude("transportTypeRef")
                .exclude("vehicleTypeRef")
                .customize(new VehicleMapper())
                .byDefault()
                .register();

        mapperFactory.classMap(VehicleType.class, org.rutebanken.tiamat.model.vehicle.VehicleType.class)
                .customize(new VehicleTypeMapper())
                .byDefault()
                .register();

        mapperFactory.classMap(VehicleModel.class, org.rutebanken.tiamat.model.vehicle.VehicleModel.class)
                .exclude("transportTypeRef")
                .customize(new VehicleModelMapper())
                .byDefault()
                .register();

        mapperFactory.classMap(CompositeFrame.class, org.rutebanken.tiamat.model.vehicle.CompositeFrame.class)
                .byDefault()
                .register();

        mapperFactory.classMap(PassengerCapacityStructure.class, org.rutebanken.tiamat.model.vehicle.PassengerCapacity.class)
                .customize(new PassengerCapacityStructureMapper())
                .byDefault()
                .register();

        mapperFactory.classMap(DeckPlan.class, org.rutebanken.tiamat.model.vehicle.DeckPlan.class)
                .customize(new DeckPlanMapper())
                .byDefault()
                .register();

        mapperFactory.classMap(Deck.class, org.rutebanken.tiamat.model.vehicle.Deck.class)
                .customize(new DeckMapper())
                .byDefault()
                .register();

        mapperFactory.classMap(PassengerSpace.class, org.rutebanken.tiamat.model.vehicle.PassengerSpace.class)
                .customize(new PassengerSpaceMapper())
                .byDefault()
                .register();

        mapperFactory.classMap(PassengerEntrance.class, org.rutebanken.tiamat.model.vehicle.PassengerEntrance.class)
                .customize(new PassengerEntranceMapper())
                .byDefault()
                .register();

        mapperFactory.classMap(PassengerSpot.class, org.rutebanken.tiamat.model.vehicle.PassengerSpot.class)
                .exclude("spotRowRef")
                .customize(new PassengerSpotMapper())
                .byDefault()
                .register();

        mapperFactory.classMap(SpotRow.class, org.rutebanken.tiamat.model.vehicle.SpotRow.class)
                .customize(new SpotRowMapper())
                .byDefault()
                .register();

        mapperFactory.classMap(VehicleEquipmentProfile.class, org.rutebanken.tiamat.model.vehicle.VehicleEquipmentProfile.class)
                .byDefault()
                .register();

        mapperFactory.classMap(VehicleEquipmentProfileMember.class, org.rutebanken.tiamat.model.vehicle.VehicleEquipmentProfileMember.class)
                .exclude("equipmentRef")
                .customize(new VehicleEquipmentProfileMemberMapper())
                .byDefault()
                .register();

        mapperFactory.classMap(PathLinkEndStructure.class, org.rutebanken.tiamat.model.PathLinkEnd.class)
                .byDefault()
                .register();

        mapperFactory.classMap(PathLink.class, org.rutebanken.tiamat.model.PathLink.class)
                .byDefault()
                .register();

        mapperFactory.classMap(InstalledEquipment_VersionStructure.class, org.rutebanken.tiamat.model.InstalledEquipment_VersionStructure.class)
                .fieldBToA("netexId", "id")
                .fieldAToB("id", "netexId")
                .byDefault()
                .register();

        mapperFactory.classMap(WaitingRoomEquipment.class, org.rutebanken.tiamat.model.WaitingRoomEquipment.class)
                .byDefault()
                .register();

        mapperFactory.classMap(SanitaryEquipment.class, org.rutebanken.tiamat.model.SanitaryEquipment.class)
                .fieldBToA("netexId", "id")
                .fieldAToB("id", "netexId")
                .byDefault()
                .register();

        mapperFactory.classMap(TicketingEquipment.class, org.rutebanken.tiamat.model.TicketingEquipment.class)
                .byDefault()
                .register();

        mapperFactory.classMap(ShelterEquipment.class, org.rutebanken.tiamat.model.ShelterEquipment.class)
                .byDefault()
                .register();

        mapperFactory.classMap(CycleStorageEquipment.class, org.rutebanken.tiamat.model.CycleStorageEquipment.class)
                .byDefault()
                .register();

        mapperFactory.classMap(GeneralSign.class, org.rutebanken.tiamat.model.GeneralSign.class)
                .byDefault()
                .register();

        mapperFactory.classMap(PlaceEquipments_RelStructure.class, org.rutebanken.tiamat.model.PlaceEquipment.class)
                .fieldBToA("netexId", "id")
                .customize(new PlaceEquipmentMapper())
                .byDefault()
                .register();

        mapperFactory.classMap(AccessibilityAssessment.class, org.rutebanken.tiamat.model.AccessibilityAssessment.class)
                .customize(accessibilityAssessmentMapper)
                .exclude("id")
                .byDefault()
                .register();

        mapperFactory.classMap(DataManagedObjectStructure.class, org.rutebanken.tiamat.model.DataManagedObjectStructure.class)
                .fieldBToA("keyValues", "keyList")
                .field("validBetween[0]", "validBetween")
                .customize(dataManagedObjectStructureMapper)
                .exclude("id")
                .exclude("keyList")
                .exclude("keyValues")
                .exclude("version")
                .byDefault()
                .register();

        facade = mapperFactory.getMapperFacade();
    }

    public TopographicPlace mapToNetexModel(org.rutebanken.tiamat.model.TopographicPlace topographicPlace) {
        return facade.map(topographicPlace, TopographicPlace.class);
    }

    public MultilingualString mapToNetexModel(org.rutebanken.tiamat.model.MultilingualString multilingualString) {
        return facade.map(multilingualString, MultilingualString.class);
    }

    public org.rutebanken.tiamat.model.EmbeddableMultilingualString mapToTiamatModel(MultilingualString multilingualString) {
        return facade.map(multilingualString, org.rutebanken.tiamat.model.EmbeddableMultilingualString.class);
    }

    public TariffZone mapToNetexModel(org.rutebanken.tiamat.model.TariffZone tariffZone) {
        return facade.map(tariffZone, TariffZone.class);
    }

    public FareZone mapToNetexModel(org.rutebanken.tiamat.model.FareZone fareZone) {
        return facade.map(fareZone, FareZone.class);
    }

    public SiteFrame mapToNetexModel(org.rutebanken.tiamat.model.SiteFrame tiamatSiteFrame) {
        SiteFrame siteFrame = facade.map(tiamatSiteFrame, SiteFrame.class);
        return siteFrame;
    }

    public ServiceFrame mapToNetexModel(org.rutebanken.tiamat.model.ServiceFrame tiamatServiceFrame) {
        ServiceFrame serviceFrame = facade.map(tiamatServiceFrame, ServiceFrame.class);
        return serviceFrame;
    }

    public FareFrame mapToNetexModel(org.rutebanken.tiamat.model.FareFrame tiamatFareFrame) {
        FareFrame fareFrame = facade.map(tiamatFareFrame, FareFrame.class);
        return fareFrame;
    }

    public ResourceFrame mapToNetexModel(org.rutebanken.tiamat.model.ResourceFrame tiamatResourceFrame){
         ResourceFrame resourceFrame = facade.map(tiamatResourceFrame, ResourceFrame.class);
         return resourceFrame;
    }

    public CompositeFrame mapToNetexModel(org.rutebanken.tiamat.model.vehicle.CompositeFrame tiamatCompositeFrame) {
        return facade.map(tiamatCompositeFrame, CompositeFrame.class);
    }

    public StopPlace mapToNetexModel(org.rutebanken.tiamat.model.StopPlace tiamatStopPlace) {
        return facade.map(tiamatStopPlace, StopPlace.class);
    }


    public Parking mapToNetexModel(org.rutebanken.tiamat.model.Parking tiamatParking) {
        return facade.map(tiamatParking, Parking.class);
    }

    public Vehicle mapToNetexModel(org.rutebanken.tiamat.model.vehicle.Vehicle tiamatVehicle) {
        return facade.map(tiamatVehicle, Vehicle.class);
    }

    public org.rutebanken.tiamat.model.vehicle.Vehicle mapToTiamatModel(Vehicle tiamatVehicle) {
        return facade.map(tiamatVehicle, org.rutebanken.tiamat.model.vehicle.Vehicle.class);
    }

    public VehicleType mapToNetexModel(org.rutebanken.tiamat.model.vehicle.VehicleType tiamatVehicleType) {
        return facade.map(tiamatVehicleType, VehicleType.class);
    }

    public org.rutebanken.tiamat.model.vehicle.VehicleType mapToTiamatModel(VehicleType netexVehicleType) {
        return facade.map(netexVehicleType, org.rutebanken.tiamat.model.vehicle.VehicleType.class);
    }

    public org.rutebanken.tiamat.model.TopographicPlace mapToTiamatModel(TopographicPlace topographicPlace) {
        return facade.map(topographicPlace, org.rutebanken.tiamat.model.TopographicPlace.class);
    }

    public org.rutebanken.tiamat.model.TariffZone mapToTiamatModel(TariffZone tariffZone) {
        return facade.map(tariffZone, org.rutebanken.tiamat.model.TariffZone.class);
    }

    public org.rutebanken.tiamat.model.FareZone mapToTiamatModel(FareZone fareZone) {
        return facade.map(fareZone,org.rutebanken.tiamat.model.FareZone.class);
    }

    public List<org.rutebanken.tiamat.model.StopPlace> mapStopsToTiamatModel(List<StopPlace> stopPlaces) {
        return facade.mapAsList(stopPlaces, org.rutebanken.tiamat.model.StopPlace.class);
    }

    public List<org.rutebanken.tiamat.model.Parking> mapParkingsToTiamatModel(List<Parking> parking) {
        return facade.mapAsList(parking, org.rutebanken.tiamat.model.Parking.class);
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


    public org.rutebanken.tiamat.model.Parking mapToTiamatModel(Parking netexParking) {
        return facade.map(netexParking, org.rutebanken.tiamat.model.Parking.class);
    }

    public Quay mapToNetexModel(org.rutebanken.tiamat.model.Quay tiamatQuay) {
        return facade.map(tiamatQuay, Quay.class);
    }

    public PathLink mapToNetexModel(org.rutebanken.tiamat.model.PathLink pathLink) {
        return facade.map(pathLink, PathLink.class);
    }

    public DeckPlan mapToNetexModel(org.rutebanken.tiamat.model.vehicle.DeckPlan deckPlan) {
        return facade.map(deckPlan, DeckPlan.class);
    }

    public org.rutebanken.tiamat.model.vehicle.DeckPlan mapToTiamatModel(DeckPlan deckPlan) {
        return facade.map(deckPlan, org.rutebanken.tiamat.model.vehicle.DeckPlan.class);
    }

    public VehicleModel mapToNetexModel(org.rutebanken.tiamat.model.vehicle.VehicleModel vehicleModel) {
        return facade.map(vehicleModel, VehicleModel.class);
    }

    public org.rutebanken.tiamat.model.vehicle.VehicleModel mapToTiamatModel(VehicleModel vehicleModel) {
        return facade.map(vehicleModel, org.rutebanken.tiamat.model.vehicle.VehicleModel.class);
    }

    public MapperFacade getFacade() {
        return facade;
    }

    public org.rutebanken.tiamat.model.GroupOfTariffZones mapToTiamatModel(GroupOfTariffZones netexGroupOfTariffZones) {
        final org.rutebanken.tiamat.model.GroupOfTariffZones groupOfTariffZones = facade.map(netexGroupOfTariffZones, org.rutebanken.tiamat.model.GroupOfTariffZones.class);
        return groupOfTariffZones;
    }
}
