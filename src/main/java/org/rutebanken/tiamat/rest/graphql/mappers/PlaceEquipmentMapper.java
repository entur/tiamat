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

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;
import org.rutebanken.tiamat.model.CycleStorageEnumeration;
import org.rutebanken.tiamat.model.CycleStorageEquipment;
import org.rutebanken.tiamat.model.GenderLimitationEnumeration;
import org.rutebanken.tiamat.model.GeneralSign;
import org.rutebanken.tiamat.model.InstalledEquipment_VersionStructure;
import org.rutebanken.tiamat.model.PlaceEquipment;
import org.rutebanken.tiamat.model.SanitaryEquipment;
import org.rutebanken.tiamat.model.ShelterEquipment;
import org.rutebanken.tiamat.model.SignContentEnumeration;
import org.rutebanken.tiamat.model.SiteComponent_VersionStructure;
import org.rutebanken.tiamat.model.SiteElement;
import org.rutebanken.tiamat.model.Site_VersionStructure;
import org.rutebanken.tiamat.model.TicketingEquipment;
import org.rutebanken.tiamat.model.WaitingRoomEquipment;
import org.rutebanken.tiamat.model.hsl.ElectricityTypeEnumeration;
import org.rutebanken.tiamat.model.hsl.ShelterTypeEnumeration;
import org.rutebanken.tiamat.model.hsl.ShelterConditionEnumeration;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.BICYCLE_PARKING;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.CONTENT;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.CYCLE_STORAGE_EQUIPMENT;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.CYCLE_STORAGE_TYPE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ENCLOSED;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.GENDER;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.GENERAL_SIGN;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.HEATED;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ID;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.LEANING_RAIL;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.LINE_SIGNAGE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.MAIN_LINE_SIGN;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.NOTE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.NUMBER_OF_FRAMES;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.NUMBER_OF_MACHINES;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.NUMBER_OF_SPACES;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.NUMBER_OF_TOILETS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.OUTSIDE_BENCH;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PLACE_EQUIPMENTS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PRIVATE_CODE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.REPLACES_RAIL_SIGN;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SANITARY_EQUIPMENT;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SEATS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SHELTER_CONDITION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SHELTER_ELECTRICITY;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SHELTER_EQUIPMENT;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SHELTER_FASCIA_BOARD_TAPING;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SHELTER_HAS_DISPLAY;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SHELTER_LIGHTING;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SHELTER_TYPE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SIGN_CONTENT_TYPE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.STEP_FREE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TICKETING_EQUIPMENT;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TICKET_MACHINES;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TICKET_OFFICE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TIMETABLE_CABINETS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TRASH_CAN;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.WAITING_ROOM_EQUIPMENT;
import static org.rutebanken.tiamat.rest.graphql.mappers.EmbeddableMultilingualStringMapper.getEmbeddableString;
import static org.rutebanken.tiamat.rest.graphql.mappers.PrivateCodeMapper.getPrivateCodeStructure;

@Component(value = "GraphQLPlaceEquipmentMapper")
public class PlaceEquipmentMapper {

    public Optional<PlaceEquipment> map(PlaceEquipment oldEquipments, Map input) {
        if (input.get(PLACE_EQUIPMENTS) != null) {

            Map<String, Object> equipmentInput = (Map) input.get(PLACE_EQUIPMENTS);

            PlaceEquipment equipments = new PlaceEquipment();

            List<SanitaryEquipment> installedSanitaryEquipment = getEquipmentOfClass(oldEquipments, SanitaryEquipment.class);

            if (equipmentInput.get(SANITARY_EQUIPMENT) != null) {

                List equipment = (List) equipmentInput.get(SANITARY_EQUIPMENT);
                for (Object item : equipment) {

                    Map<String, Object> sanitaryEquipment = (Map<String, Object>) item;

                    String netexId = (String)sanitaryEquipment.get(ID);

                    SanitaryEquipment toalett = getByIdOrNew(installedSanitaryEquipment, netexId, SanitaryEquipment.class);

                    toalett.setNumberOfToilets((BigInteger) sanitaryEquipment.get(NUMBER_OF_TOILETS));
                    toalett.setGender((GenderLimitationEnumeration) sanitaryEquipment.get(GENDER));
                    equipments.getInstalledEquipment().add(toalett);
                }
            } else {
                equipments.getInstalledEquipment().addAll(installedSanitaryEquipment);
            }

            List<ShelterEquipment> installedShelters = getEquipmentOfClass(oldEquipments, ShelterEquipment.class);

            if (equipmentInput.get(SHELTER_EQUIPMENT) != null) {

                List equipment = (List) equipmentInput.get(SHELTER_EQUIPMENT);

                for (Object item : equipment) {
                    Map<String, Object> shelterEquipment = (Map<String, Object>) item;

                    String netexId = (String)shelterEquipment.get(ID);

                    ShelterEquipment leskur = getByIdOrNew(installedShelters, netexId, ShelterEquipment.class);

                    leskur.setEnclosed((Boolean) shelterEquipment.get(ENCLOSED));
                    leskur.setSeats((BigInteger) shelterEquipment.get(SEATS));
                    leskur.setStepFree((Boolean) shelterEquipment.get(STEP_FREE));
                    leskur.setShelterType((ShelterTypeEnumeration) shelterEquipment.get(SHELTER_TYPE));
                    leskur.setShelterElectricity((ElectricityTypeEnumeration) shelterEquipment.get(SHELTER_ELECTRICITY));
                    leskur.setShelterLighting((Boolean) shelterEquipment.get(SHELTER_LIGHTING));
                    leskur.setShelterCondition((ShelterConditionEnumeration) shelterEquipment.get(SHELTER_CONDITION));
                    leskur.setTimetableCabinets((Integer) shelterEquipment.get(TIMETABLE_CABINETS));
                    leskur.setTrashCan((Boolean) shelterEquipment.get(TRASH_CAN));
                    leskur.setShelterHasDisplay((Boolean) shelterEquipment.get(SHELTER_HAS_DISPLAY));
                    leskur.setBicycleParking((Boolean) shelterEquipment.get(BICYCLE_PARKING));
                    leskur.setLeaningRail((Boolean) shelterEquipment.get(LEANING_RAIL));
                    leskur.setOutsideBench((Boolean) shelterEquipment.get(OUTSIDE_BENCH));
                    leskur.setShelterFasciaBoardTaping((Boolean) shelterEquipment.get(SHELTER_FASCIA_BOARD_TAPING));
                    equipments.getInstalledEquipment().add(leskur);
                }

            } else {
                equipments.getInstalledEquipment().addAll(installedShelters);
            }

            List<CycleStorageEquipment> existingCycleStorage = getEquipmentOfClass(oldEquipments, CycleStorageEquipment.class);

            if (equipmentInput.get(CYCLE_STORAGE_EQUIPMENT) != null) {

                List equipment = (List) equipmentInput.get(CYCLE_STORAGE_EQUIPMENT);
                for (Object item : equipment) {
                    Map<String, Object> cycleStorageEquipment = (Map<String, Object>) item;

                    String netexId = (String)cycleStorageEquipment.get(ID);

                    CycleStorageEquipment sykkelskur = getByIdOrNew(existingCycleStorage, netexId, CycleStorageEquipment.class);
                    sykkelskur.setNumberOfSpaces((BigInteger) cycleStorageEquipment.get(NUMBER_OF_SPACES));
                    sykkelskur.setCycleStorageType((CycleStorageEnumeration) cycleStorageEquipment.get(CYCLE_STORAGE_TYPE));
                    equipments.getInstalledEquipment().add(sykkelskur);
                }
            } else {
                equipments.getInstalledEquipment().addAll(existingCycleStorage);
            }

            List<WaitingRoomEquipment> existingWaitingRooms = getEquipmentOfClass(oldEquipments, WaitingRoomEquipment.class);

            if (equipmentInput.get(WAITING_ROOM_EQUIPMENT) != null) {

                List equipment = (List) equipmentInput.get(WAITING_ROOM_EQUIPMENT);
                for (Object item : equipment) {
                    Map<String, Object> waitingRoomEquipment = (Map<String, Object>) item;

                    String netexId = (String) waitingRoomEquipment.get(ID);
                    WaitingRoomEquipment venterom = getByIdOrNew(existingWaitingRooms, netexId, WaitingRoomEquipment.class);

                    venterom.setSeats((BigInteger) waitingRoomEquipment.get(SEATS));
                    venterom.setHeated((Boolean) waitingRoomEquipment.get(HEATED));
                    venterom.setStepFree((Boolean) waitingRoomEquipment.get(STEP_FREE));
                    equipments.getInstalledEquipment().add(venterom);
                }
            } else {
                equipments.getInstalledEquipment().addAll(existingWaitingRooms);
            }

            List<TicketingEquipment> existingTicketingEquipment = getEquipmentOfClass(oldEquipments, TicketingEquipment.class);

            if (equipmentInput.get(TICKETING_EQUIPMENT) != null) {

                List equipment = (List) equipmentInput.get(TICKETING_EQUIPMENT);
                for (Object item : equipment) {
                    Map<String, Object> ticketingEquipment = (Map<String, Object>) item;

                    String netexId = (String)ticketingEquipment.get(ID);

                    TicketingEquipment billettAutomat = getByIdOrNew(existingTicketingEquipment, netexId, TicketingEquipment.class);
                    billettAutomat.setTicketOffice((Boolean) ticketingEquipment.get(TICKET_OFFICE));
                    billettAutomat.setTicketMachines((Boolean) ticketingEquipment.get(TICKET_MACHINES));
                    billettAutomat.setNumberOfMachines((BigInteger) ticketingEquipment.get(NUMBER_OF_MACHINES));
                    equipments.getInstalledEquipment().add(billettAutomat);
                }
            } else {
                equipments.getInstalledEquipment().addAll(existingTicketingEquipment);
            }

            List<GeneralSign> existingGeneralSigns = getEquipmentOfClass(oldEquipments, GeneralSign.class);

            if (equipmentInput.get(GENERAL_SIGN) != null) {

                List equipment = (List) equipmentInput.get(GENERAL_SIGN);
                for (Object item : equipment) {

                    Map<String, Object> generalSignEquipment = (Map<String, Object>) item;

                    String netexId = (String)generalSignEquipment.get("ID");

                    GeneralSign skilt = getByIdOrNew(existingGeneralSigns, netexId, GeneralSign.class);
                    skilt.setPrivateCode(getPrivateCodeStructure((Map) generalSignEquipment.get(PRIVATE_CODE)));
                    skilt.setContent(getEmbeddableString((Map) generalSignEquipment.get(CONTENT)));
                    skilt.setSignContentType((SignContentEnumeration) generalSignEquipment.get(SIGN_CONTENT_TYPE));
                    skilt.setLineSignage((Boolean) generalSignEquipment.get(LINE_SIGNAGE));
                    skilt.setReplacesRailSign((Boolean) generalSignEquipment.get(REPLACES_RAIL_SIGN));
                    skilt.setMainLineSign((Boolean) generalSignEquipment.get(MAIN_LINE_SIGN));
                    skilt.setNumberOfFrames((Integer) generalSignEquipment.get(NUMBER_OF_FRAMES));
                    skilt.setNote(getEmbeddableString((Map) generalSignEquipment.get(NOTE)));
                    equipments.getInstalledEquipment().add(skilt);
                }
            } else {
                equipments.getInstalledEquipment().addAll(existingGeneralSigns);
            }

            return Optional.of(equipments);
        }
        return Optional.empty();
    }

    private static <T extends InstalledEquipment_VersionStructure> List<T> getEquipmentOfClass(PlaceEquipment equipment, Class<T> type) {
        if (equipment == null || equipment.getInstalledEquipment() == null) {
            return Collections.emptyList();
        }
        return equipment.getInstalledEquipment().stream()
                .filter(type::isInstance)
                .map(type::cast)
                .toList();
    }

    private static <T extends InstalledEquipment_VersionStructure> T getByIdOrNew(List<T> existing, String netexId, Class<T> type) {
        return existing.stream()
                .filter(shelter -> Objects.equals(shelter.getNetexId() ,netexId))
                .findAny()
                .orElseGet(() -> {
                    try {
                        return type.getDeclaredConstructor().newInstance();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                });

    }
}
