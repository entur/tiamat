package org.rutebanken.tiamat.netex.mapping.converter;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.VehicleEquipmentProfileMembers_RelStructure;
import org.rutebanken.tiamat.model.vehicle.VehicleEquipmentProfileMember;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class VehicleEquipmentProfileMemberListConverter extends BidirectionalConverter<List<VehicleEquipmentProfileMember>, VehicleEquipmentProfileMembers_RelStructure> {

    @Override
    public VehicleEquipmentProfileMembers_RelStructure convertTo(List<VehicleEquipmentProfileMember> members, Type<VehicleEquipmentProfileMembers_RelStructure> type, MappingContext mappingContext) {

        if(members == null || members.isEmpty()) {
            return null;
        }

        return new VehicleEquipmentProfileMembers_RelStructure()
                .withVehicleEquipmentProfileMember(members.stream()
                        .map(ds -> mapperFacade.map(ds, org.rutebanken.netex.model.VehicleEquipmentProfileMember.class))
                        .collect(Collectors.toList()));
    }

    @Override
    public List<VehicleEquipmentProfileMember> convertFrom(VehicleEquipmentProfileMembers_RelStructure vehicleEquipmentProfileMembersRelStructure, Type<List<VehicleEquipmentProfileMember>> type, MappingContext mappingContext) {
        return null;
    }
}

