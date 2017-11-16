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

import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.service.AlternativeNameUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SIGN_CONTENT_TYPE;
import static org.rutebanken.tiamat.rest.graphql.mappers.EmbeddableMultilingualStringMapper.getEmbeddableString;
import static org.rutebanken.tiamat.rest.graphql.mappers.PrivateCodeMapper.getPrivateCodeStructure;

@Component
public class SiteElementMapper {

    private static final Logger logger = LoggerFactory.getLogger(SiteElementMapper.class);

    @Autowired
    private AlternativeNameMapper alternativeNameMapper;

    @Autowired
    private AlternativeNameUpdater alternativeNameUpdater;

    @Autowired
    private AccessibilityLimitationMapper accessibilityLimitationMapper;

    @Autowired
    private GeometryMapper geometryMapper;

    public boolean populate(Map input, SiteElement siteElement) {

        boolean isUpdated = false;

        if (input.get(ALTERNATIVE_NAMES) != null) {
            List alternativeNames = (List) input.get(ALTERNATIVE_NAMES);
            List<AlternativeName> mappedAlternativeNames = alternativeNameMapper.mapAlternativeNames(alternativeNames);

            if(alternativeNameUpdater.updateAlternativeNames(siteElement, mappedAlternativeNames)) {
                isUpdated = true;
            } else {
                logger.info("AlternativeName not changed");
            }
        }

        if (input.get(GEOMETRY) != null) {

            siteElement.setCentroid(geometryMapper.createGeoJsonPoint((Map) input.get(GEOMETRY)));
            isUpdated = true;
        }

        if (input.get(ACCESSIBILITY_ASSESSMENT) != null) {
            AccessibilityAssessment accessibilityAssessment = siteElement.getAccessibilityAssessment();
            if (accessibilityAssessment == null) {
                accessibilityAssessment = new AccessibilityAssessment();
            }

            Map<String, Object> accessibilityAssessmentInput = (Map) input.get(ACCESSIBILITY_ASSESSMENT);
            List<AccessibilityLimitation> limitations = accessibilityAssessment.getLimitations();
            AccessibilityLimitation limitation;
            if (limitations == null || limitations.isEmpty()) {
                limitations = new ArrayList<>();
                limitation = new AccessibilityLimitation();
            } else {
                limitation = limitations.get(0);
            }

            AccessibilityLimitation limitationFromInput = accessibilityLimitationMapper.map((Map<String, LimitationStatusEnumeration>) accessibilityAssessmentInput.get("limitations"));

            //Only flag as updated if limitations are updated
            if (limitationFromInput.getWheelchairAccess() != limitation.getWheelchairAccess() |
                    limitationFromInput.getAudibleSignalsAvailable() != limitation.getAudibleSignalsAvailable() |
                    limitationFromInput.getStepFreeAccess() != limitation.getStepFreeAccess() |
                    limitationFromInput.getLiftFreeAccess() != limitation.getLiftFreeAccess() |
                    limitationFromInput.getEscalatorFreeAccess() != limitation.getEscalatorFreeAccess()) {

                limitation.setWheelchairAccess(limitationFromInput.getWheelchairAccess());
                limitation.setAudibleSignalsAvailable(limitationFromInput.getAudibleSignalsAvailable());
                limitation.setStepFreeAccess(limitationFromInput.getStepFreeAccess());
                limitation.setLiftFreeAccess(limitationFromInput.getLiftFreeAccess());
                limitation.setEscalatorFreeAccess(limitationFromInput.getEscalatorFreeAccess());


                limitations.clear();
                limitations.add(limitation);
                accessibilityAssessment.setLimitations(limitations);

                siteElement.setAccessibilityAssessment(accessibilityAssessment);

                isUpdated = true;
            }
        }

        if (input.get(PLACE_EQUIPMENTS) != null && siteElement instanceof Site_VersionStructure) {
            PlaceEquipment equipments = new PlaceEquipment();

            Map<String, Object> equipmentInput = (Map) input.get(PLACE_EQUIPMENTS);

            if (equipmentInput.get(SANITARY_EQUIPMENT) != null) {

                List equipment = (List) equipmentInput.get(SANITARY_EQUIPMENT);
                for (Object item : equipment) {
                    Map<String, Object> sanitaryEquipment = (Map<String, Object>) item;

                    SanitaryEquipment toalett = new SanitaryEquipment();
                    toalett.setNumberOfToilets((BigInteger) sanitaryEquipment.get(NUMBER_OF_TOILETS));
                    toalett.setGender((GenderLimitationEnumeration) sanitaryEquipment.get(GENDER));
                    equipments.getInstalledEquipment().add(toalett);
                }
            }

            if (equipmentInput.get(SHELTER_EQUIPMENT) != null) {

                List equipment = (List) equipmentInput.get(SHELTER_EQUIPMENT);
                for (Object item : equipment) {
                    Map<String, Object> shelterEquipment = (Map<String, Object>) item;
                    ShelterEquipment leskur = new ShelterEquipment();
                    leskur.setEnclosed((Boolean) shelterEquipment.get(ENCLOSED));
                    leskur.setSeats((BigInteger) shelterEquipment.get(SEATS));
                    leskur.setStepFree((Boolean) shelterEquipment.get(STEP_FREE));
                    equipments.getInstalledEquipment().add(leskur);
                }
            }

            if (equipmentInput.get(CYCLE_STORAGE_EQUIPMENT) != null) {

                List equipment = (List) equipmentInput.get(CYCLE_STORAGE_EQUIPMENT);
                for (Object item : equipment) {
                    Map<String, Object> cycleStorageEquipment = (Map<String, Object>) item;
                    CycleStorageEquipment sykkelskur = new CycleStorageEquipment();
                    sykkelskur.setNumberOfSpaces((BigInteger) cycleStorageEquipment.get(NUMBER_OF_SPACES));
                    sykkelskur.setCycleStorageType((CycleStorageEnumeration) cycleStorageEquipment.get(CYCLE_STORAGE_TYPE));
                    equipments.getInstalledEquipment().add(sykkelskur);
                }
            }

            if (equipmentInput.get(WAITING_ROOM_EQUIPMENT) != null) {

                List equipment = (List) equipmentInput.get(WAITING_ROOM_EQUIPMENT);
                for (Object item : equipment) {
                    Map<String, Object> waitingRoomEquipment = (Map<String, Object>) item;
                    WaitingRoomEquipment venterom = new WaitingRoomEquipment();
                    venterom.setSeats((BigInteger) waitingRoomEquipment.get(SEATS));
                    venterom.setHeated((Boolean) waitingRoomEquipment.get(HEATED));
                    venterom.setStepFree((Boolean) waitingRoomEquipment.get(STEP_FREE));
                    equipments.getInstalledEquipment().add(venterom);
                }
            }

            if (equipmentInput.get(TICKETING_EQUIPMENT) != null) {

                List equipment = (List) equipmentInput.get(TICKETING_EQUIPMENT);
                for (Object item : equipment) {
                    Map<String, Object> ticketingEquipment = (Map<String, Object>) item;
                    TicketingEquipment billettAutomat = new TicketingEquipment();
                    billettAutomat.setTicketOffice((Boolean) ticketingEquipment.get(TICKET_OFFICE));
                    billettAutomat.setTicketMachines((Boolean) ticketingEquipment.get(TICKET_MACHINES));
                    billettAutomat.setNumberOfMachines((BigInteger) ticketingEquipment.get(NUMBER_OF_MACHINES));
                    equipments.getInstalledEquipment().add(billettAutomat);
                }
            }

            if (equipmentInput.get(GENERAL_SIGN) != null) {

                List equipment = (List) equipmentInput.get(GENERAL_SIGN);
                for (Object item : equipment) {

                    Map<String, Object> generalSignEquipment = (Map<String, Object>) item;

                    GeneralSign skilt = new GeneralSign();
                    skilt.setPrivateCode(getPrivateCodeStructure((Map) generalSignEquipment.get(PRIVATE_CODE)));
                    skilt.setContent(getEmbeddableString((Map) generalSignEquipment.get(CONTENT)));
                    skilt.setSignContentType((SignContentEnumeration) generalSignEquipment.get(SIGN_CONTENT_TYPE));
                    equipments.getInstalledEquipment().add(skilt);
                }
            }


            ((Site_VersionStructure) siteElement).setPlaceEquipments(equipments);

            isUpdated = true;
        }

        return isUpdated;
    }
}
