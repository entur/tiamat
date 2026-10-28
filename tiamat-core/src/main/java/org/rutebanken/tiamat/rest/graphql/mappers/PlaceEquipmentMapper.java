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

package org.rutebanken.tiamat.rest.graphql.mappers;

import org.rutebanken.tiamat.model.CycleStorageEnumeration;
import org.rutebanken.tiamat.model.CycleStorageEquipment;
import org.rutebanken.tiamat.model.GenderLimitationEnumeration;
import org.rutebanken.tiamat.model.GeneralSign;
import org.rutebanken.tiamat.model.InstalledEquipment_VersionStructure;
import org.rutebanken.tiamat.model.PlaceEquipment;
import org.rutebanken.tiamat.model.SanitaryEquipment;
import org.rutebanken.tiamat.model.SanitaryFacilityEnumeration;
import org.rutebanken.tiamat.model.ShelterEquipment;
import org.rutebanken.tiamat.model.SignContentEnumeration;
import org.rutebanken.tiamat.model.TicketingEquipment;
import org.rutebanken.tiamat.model.WaitingRoomEquipment;
import org.rutebanken.tiamat.service.ChildEntityReconciler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.AUDIO_INTERFACE_AVAILABLE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.CONTENT;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.CYCLE_STORAGE_EQUIPMENT;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.CYCLE_STORAGE_TYPE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ENCLOSED;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.GENDER;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.GENERAL_SIGN;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.HEATED;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ID;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SANITARY_FACILITY_LIST;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TICKET_COUNTER;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.INDUCTION_LOOPS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.LOW_COUNTER_ACCESS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.NUMBER_OF_MACHINES;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.NUMBER_OF_SPACES;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.NUMBER_OF_TOILETS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PLACE_EQUIPMENTS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PRIVATE_CODE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SANITARY_EQUIPMENT;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SEATS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SHELTER_EQUIPMENT;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SIGN_CONTENT_TYPE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.STEP_FREE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TACTILE_INTERFACE_AVAILABLE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TICKETING_EQUIPMENT;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TICKET_MACHINES;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TICKET_OFFICE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.WAITING_ROOM_EQUIPMENT;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.WHEELCHAIR_SUITABLE;
import static org.rutebanken.tiamat.rest.graphql.mappers.EmbeddableMultilingualStringMapper.getEmbeddableString;
import static org.rutebanken.tiamat.rest.graphql.mappers.PrivateCodeMapper.getPrivateCodeStructure;

@Component(value = "GraphQLPlaceEquipmentMapper")
public class PlaceEquipmentMapper {

    @Autowired
    private ChildEntityReconciler childEntityReconciler;

    /**
     * Maps the place equipment of an input. An equipment that the input resends with its id
     * keeps its netexId. An equipment without an id is new, and a stored equipment that the
     * input omits is removed.
     *
     * @param existing the stored place equipment of the parent, or null
     * @param parentId the id of the parent, named in error messages
     */
    public Optional<PlaceEquipment> map(Map input, PlaceEquipment existing, String parentId) {
        if (input.get(PLACE_EQUIPMENTS) == null) {
            return Optional.empty();
        }
        Map<String, Object> equipmentInput = (Map) input.get(PLACE_EQUIPMENTS);
        List<InstalledEquipment_VersionStructure> stored = existing == null ? List.of() : List.copyOf(existing.getInstalledEquipment());

        PlaceEquipment equipments = containerFor(equipmentInput, existing, parentId);
        equipments.getInstalledEquipment().clear();

        reconcileType(equipmentInput, SANITARY_EQUIPMENT, SanitaryEquipment.class, SanitaryEquipment::new,
                this::populateSanitary, stored, equipments, parentId);
        reconcileType(equipmentInput, SHELTER_EQUIPMENT, ShelterEquipment.class, ShelterEquipment::new,
                this::populateShelter, stored, equipments, parentId);
        reconcileType(equipmentInput, CYCLE_STORAGE_EQUIPMENT, CycleStorageEquipment.class, CycleStorageEquipment::new,
                this::populateCycleStorage, stored, equipments, parentId);
        reconcileType(equipmentInput, WAITING_ROOM_EQUIPMENT, WaitingRoomEquipment.class, WaitingRoomEquipment::new,
                this::populateWaitingRoom, stored, equipments, parentId);
        reconcileType(equipmentInput, TICKETING_EQUIPMENT, TicketingEquipment.class, TicketingEquipment::new,
                this::populateTicketing, stored, equipments, parentId);
        reconcileType(equipmentInput, GENERAL_SIGN, GeneralSign.class, GeneralSign::new,
                this::populateGeneralSign, stored, equipments, parentId);
        return Optional.of(equipments);
    }

    private PlaceEquipment containerFor(Map<String, Object> equipmentInput, PlaceEquipment existing, String parentId) {
        Object id = equipmentInput.get(ID);
        if (id == null) {
            return new PlaceEquipment();
        }
        if (existing == null || !id.equals(existing.getNetexId())) {
            throw new IllegalArgumentException(
                    "Attempting to update child [id = " + id + "] of parent [id = " + parentId + "], but the parent has no such child");
        }
        return existing;
    }

    private <T extends InstalledEquipment_VersionStructure> void reconcileType(
            Map<String, Object> equipmentInput, String key, Class<T> type, Supplier<T> factory,
            BiConsumer<Map<String, Object>, T> populate, List<InstalledEquipment_VersionStructure> stored,
            PlaceEquipment target, String parentId) {
        List<Map<String, Object>> inputs = (List<Map<String, Object>>) equipmentInput.get(key);
        if (inputs == null) {
            return;
        }
        List<T> storedOfType = stored.stream().filter(type::isInstance).map(type::cast).toList();
        target.getInstalledEquipment().addAll(
                childEntityReconciler.reconcileAll(storedOfType, inputs, parentId, factory, populate));
    }

    private void populateSanitary(Map<String, Object> input, SanitaryEquipment toalett) {
        toalett.setNumberOfToilets((BigInteger) input.get(NUMBER_OF_TOILETS));
        toalett.setGender((GenderLimitationEnumeration) input.get(GENDER));
        toalett.setSanitaryFacilityList((List<SanitaryFacilityEnumeration>) input.get(SANITARY_FACILITY_LIST));
    }

    private void populateShelter(Map<String, Object> input, ShelterEquipment leskur) {
        leskur.setEnclosed((Boolean) input.get(ENCLOSED));
        leskur.setSeats((BigInteger) input.get(SEATS));
        leskur.setStepFree((Boolean) input.get(STEP_FREE));
    }

    private void populateCycleStorage(Map<String, Object> input, CycleStorageEquipment sykkelskur) {
        sykkelskur.setNumberOfSpaces((BigInteger) input.get(NUMBER_OF_SPACES));
        sykkelskur.setCycleStorageType((CycleStorageEnumeration) input.get(CYCLE_STORAGE_TYPE));
    }

    private void populateWaitingRoom(Map<String, Object> input, WaitingRoomEquipment venterom) {
        venterom.setSeats((BigInteger) input.get(SEATS));
        venterom.setHeated((Boolean) input.get(HEATED));
        venterom.setStepFree((Boolean) input.get(STEP_FREE));
    }

    private void populateTicketing(Map<String, Object> input, TicketingEquipment billettAutomat) {
        billettAutomat.setTicketOffice((Boolean) input.get(TICKET_OFFICE));
        billettAutomat.setTicketMachines((Boolean) input.get(TICKET_MACHINES));
        billettAutomat.setNumberOfMachines((BigInteger) input.get(NUMBER_OF_MACHINES));
        billettAutomat.setAudioInterfaceAvailable((Boolean) input.get(AUDIO_INTERFACE_AVAILABLE));
        billettAutomat.setTactileInterfaceAvailable((Boolean) input.get(TACTILE_INTERFACE_AVAILABLE));
        billettAutomat.setTicketCounter((Boolean) input.get(TICKET_COUNTER));
        billettAutomat.setInductionLoops((Boolean) input.get(INDUCTION_LOOPS));
        billettAutomat.setLowCounterAccess((Boolean) input.get(LOW_COUNTER_ACCESS));
        billettAutomat.setWheelchairSuitable((Boolean) input.get(WHEELCHAIR_SUITABLE));
    }

    private void populateGeneralSign(Map<String, Object> input, GeneralSign skilt) {
        skilt.setPrivateCode(getPrivateCodeStructure((Map) input.get(PRIVATE_CODE)));
        skilt.setContent(getEmbeddableString((Map) input.get(CONTENT)));
        skilt.setSignContentType((SignContentEnumeration) input.get(SIGN_CONTENT_TYPE));
    }
}
