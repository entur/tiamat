package org.rutebanken.tiamat.netex.mapping.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.rutebanken.netex.model.ModificationEnumeration;
import org.rutebanken.netex.model.SiteFacilitySet;
import org.rutebanken.tiamat.model.MobilityFacilityEnumeration;
import org.rutebanken.tiamat.model.PassengerInformationEquipmentEnumeration;
import org.rutebanken.tiamat.model.PassengerInformationFacilityEnumeration;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: this is to be removed once netex model library is updated to the latest
 */
public class FacilityMapper extends CustomMapper<SiteFacilitySet, org.rutebanken.tiamat.model.SiteFacilitySet> {

    @Override
    public void mapAtoB(SiteFacilitySet netexSiteFacilitySet, org.rutebanken.tiamat.model.SiteFacilitySet tiamatSiteFacilitySet, MappingContext context) {
        super.mapAtoB(netexSiteFacilitySet, tiamatSiteFacilitySet, context);

        if (netexSiteFacilitySet.getMobilityFacilityList() != null) {
            List<MobilityFacilityEnumeration> tiamatMobilityFacilityList = new ArrayList<>();
            netexSiteFacilitySet.getMobilityFacilityList().forEach(netexMobilityFacilityVal ->
                    tiamatMobilityFacilityList.add(MobilityFacilityEnumeration.fromValue(netexMobilityFacilityVal.value())));
            tiamatSiteFacilitySet.setMobilityFacilityList(tiamatMobilityFacilityList);
        }

        if (netexSiteFacilitySet.getPassengerInformationFacilityList() != null) {
            List<PassengerInformationFacilityEnumeration> tiamatPassengerInformationFacilityList = new ArrayList<>();
            netexSiteFacilitySet.getPassengerInformationFacilityList().forEach(netexPassengerInformationFacilityVal ->
                    tiamatPassengerInformationFacilityList.add(PassengerInformationFacilityEnumeration.fromValue(netexPassengerInformationFacilityVal.value())));
            tiamatSiteFacilitySet.setPassengerInformationFacilityList(tiamatPassengerInformationFacilityList);
        }

        // At the moment only a single value can be handled as part of the passengerInformationEquipmentList:
        if (netexSiteFacilitySet.getPassengerInformationEquipmentList() != null) {
            PassengerInformationEquipmentEnumeration tiamatPassengerEquipmentFacility = PassengerInformationEquipmentEnumeration.fromValue(netexSiteFacilitySet.getPassengerInformationEquipmentList().value());
            tiamatSiteFacilitySet.setPassengerInformationEquipmentList(List.of(tiamatPassengerEquipmentFacility));
        }
    }

    @Override
    public void mapBtoA(org.rutebanken.tiamat.model.SiteFacilitySet tiamatSiteFacilitySet, SiteFacilitySet netexSiteFacilitySet, MappingContext context) {
        super.mapBtoA(tiamatSiteFacilitySet, netexSiteFacilitySet, context);

        netexSiteFacilitySet.setVersion(String.valueOf(tiamatSiteFacilitySet.getVersion()));
        netexSiteFacilitySet.setModification(ModificationEnumeration.fromValue(tiamatSiteFacilitySet.getModification().value()));

        if (tiamatSiteFacilitySet.getMobilityFacilityList() != null && !tiamatSiteFacilitySet.getMobilityFacilityList().isEmpty()) {
            List<org.rutebanken.netex.model.MobilityFacilityEnumeration> netexMobilityFacilityList = new ArrayList<>();
            tiamatSiteFacilitySet.getMobilityFacilityList().forEach(tiamatMobilityFacilityVal ->
                    netexMobilityFacilityList.add(org.rutebanken.netex.model.MobilityFacilityEnumeration.fromValue(tiamatMobilityFacilityVal.value())));
            netexSiteFacilitySet.withMobilityFacilityList(netexMobilityFacilityList);
        }

        // At the moment only a single value can be handled as part of the passengerInformationEquipmentList:
        if (tiamatSiteFacilitySet.getPassengerInformationEquipmentList() != null && !tiamatSiteFacilitySet.getPassengerInformationEquipmentList().isEmpty()) {
            org.rutebanken.netex.model.PassengerInformationEquipmentEnumeration netexPassengerEquipmentFacility = org.rutebanken.netex.model.PassengerInformationEquipmentEnumeration
                    .fromValue(tiamatSiteFacilitySet.getPassengerInformationEquipmentList().getFirst().value());
            netexSiteFacilitySet.setPassengerInformationEquipmentList(netexPassengerEquipmentFacility);
        }

        if (tiamatSiteFacilitySet.getPassengerInformationFacilityList() != null && !tiamatSiteFacilitySet.getPassengerInformationFacilityList().isEmpty()) {
            List<org.rutebanken.netex.model.PassengerInformationFacilityEnumeration> netexPassengerInformationFacilityList = new ArrayList<>();
            tiamatSiteFacilitySet.getPassengerInformationFacilityList().forEach(netexPassengerInformationFacilityVal ->
                    netexPassengerInformationFacilityList.add(org.rutebanken.netex.model.PassengerInformationFacilityEnumeration.fromValue(netexPassengerInformationFacilityVal.value())));
            netexSiteFacilitySet.withPassengerInformationFacilityList(netexPassengerInformationFacilityList);
        }
    }
}
