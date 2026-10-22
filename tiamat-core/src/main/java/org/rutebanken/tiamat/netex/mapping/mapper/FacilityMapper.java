package org.rutebanken.tiamat.netex.mapping.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.entur.netex2.model.ModificationEnumeration;
import org.entur.netex2.model.SiteFacilitySet;
import org.rutebanken.tiamat.model.MobilityFacilityEnumeration;
import org.rutebanken.tiamat.model.PassengerInformationEquipmentEnumeration;
import org.rutebanken.tiamat.model.PassengerInformationFacilityEnumeration;

import java.util.ArrayList;
import java.util.List;

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

        if (netexSiteFacilitySet.getPassengerInformationEquipmentList() != null) {
            List<PassengerInformationEquipmentEnumeration> tiamatPassengerInformationEquipmentList = new ArrayList<>();
            netexSiteFacilitySet.getPassengerInformationEquipmentList().forEach(netexPassengerInformationEquipmentVal ->
                    tiamatPassengerInformationEquipmentList.add(PassengerInformationEquipmentEnumeration.fromValue(netexPassengerInformationEquipmentVal.value())));

            tiamatSiteFacilitySet.setPassengerInformationEquipmentList(tiamatPassengerInformationEquipmentList);
        }
    }

    @Override
    public void mapBtoA(org.rutebanken.tiamat.model.SiteFacilitySet tiamatSiteFacilitySet, SiteFacilitySet netexSiteFacilitySet, MappingContext context) {
        super.mapBtoA(tiamatSiteFacilitySet, netexSiteFacilitySet, context);

        netexSiteFacilitySet.setVersion(String.valueOf(tiamatSiteFacilitySet.getVersion()));
        netexSiteFacilitySet.setModification(ModificationEnumeration.fromValue(tiamatSiteFacilitySet.getModification().value()));

        if (tiamatSiteFacilitySet.getMobilityFacilityList() != null && !tiamatSiteFacilitySet.getMobilityFacilityList().isEmpty()) {
            List<org.entur.netex2.model.MobilityFacilityEnumeration> netexMobilityFacilityList = new ArrayList<>();
            tiamatSiteFacilitySet.getMobilityFacilityList().forEach(tiamatMobilityFacilityVal ->
                    netexMobilityFacilityList.add(org.entur.netex2.model.MobilityFacilityEnumeration.fromValue(tiamatMobilityFacilityVal.value())));
            netexSiteFacilitySet.withMobilityFacilityList(netexMobilityFacilityList);
        }

        if (tiamatSiteFacilitySet.getPassengerInformationEquipmentList() != null && !tiamatSiteFacilitySet.getPassengerInformationEquipmentList().isEmpty()) {
            List<org.entur.netex2.model.PassengerInformationEquipmentEnumeration> netexPassengerInformationEquipmentList = new ArrayList<>();
            tiamatSiteFacilitySet.getPassengerInformationEquipmentList().forEach(netexPassengerInformationEquipmentVal ->
                    netexPassengerInformationEquipmentList.add(org.entur.netex2.model.PassengerInformationEquipmentEnumeration.fromValue(netexPassengerInformationEquipmentVal.value())));
            netexSiteFacilitySet.withPassengerInformationEquipmentList(netexPassengerInformationEquipmentList);
        }

        if (tiamatSiteFacilitySet.getPassengerInformationFacilityList() != null && !tiamatSiteFacilitySet.getPassengerInformationFacilityList().isEmpty()) {
            List<org.entur.netex2.model.PassengerInformationFacilityEnumeration> netexPassengerInformationFacilityList = new ArrayList<>();
            tiamatSiteFacilitySet.getPassengerInformationFacilityList().forEach(netexPassengerInformationFacilityVal ->
                    netexPassengerInformationFacilityList.add(org.entur.netex2.model.PassengerInformationFacilityEnumeration.fromValue(netexPassengerInformationFacilityVal.value())));
            netexSiteFacilitySet.withPassengerInformationFacilityList(netexPassengerInformationFacilityList);
        }
    }
}