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

package org.rutebanken.tiamat.netex.mapping.converter;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeBuilder;
import org.junit.Test;
import org.rutebanken.netex.model.ParkingCapacities_RelStructure;
import org.rutebanken.netex.model.ParkingProperties_RelStructure;
import org.rutebanken.netex.model.ParkingStayEnumeration;
import org.rutebanken.netex.model.ParkingUserEnumeration;
import org.rutebanken.netex.model.ParkingVehicleEnumeration;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.ParkingCapacity;
import org.rutebanken.tiamat.model.ParkingProperties;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Covers the two attributes that identify a NeTEx parking capacity group together with the
 * user type: the vehicle type and the stay type. Both directions dropped them, so a
 * migrated car count and a migrated motorcycle count reached the database as the same
 * unlabelled number.
 */
public class ParkingPropertiesListConverterTest extends TiamatIntegrationTest {

    private final Type<List<ParkingProperties>> parkingPropertiesListType = new TypeBuilder<List<ParkingProperties>>() {
    }.build();

    private final Type<ParkingProperties_RelStructure> relStructureType = new TypeBuilder<ParkingProperties_RelStructure>() {
    }.build();

    private final MappingContext mappingContext = new MappingContext(new HashMap<>());

    @Autowired
    private ParkingPropertiesListConverter parkingPropertiesListConverter;

    @Test
    public void convertToKeepsVehicleTypeAndStayType() {
        ParkingCapacity capacity = tiamatCapacity();
        capacity.setParkingVehicleType(org.rutebanken.tiamat.model.ParkingVehicleEnumeration.MOTORCYCLE);
        capacity.setParkingStayType(org.rutebanken.tiamat.model.ParkingStayEnumeration.LONG_TERM);

        org.rutebanken.netex.model.ParkingCapacity netexCapacity = convertToSingleCapacity(capacity);

        assertThat(netexCapacity.getParkingVehicleType()).isEqualTo(ParkingVehicleEnumeration.MOTORCYCLE);
        assertThat(netexCapacity.getParkingStayType()).isEqualTo(ParkingStayEnumeration.LONG_TERM);
    }

    @Test
    public void convertToWritesNoAttributeWhenTheStoredValueIsNull() {
        org.rutebanken.netex.model.ParkingCapacity netexCapacity = convertToSingleCapacity(tiamatCapacity());

        assertThat(netexCapacity.getParkingVehicleType()).isNull();
        assertThat(netexCapacity.getParkingStayType()).isNull();
        assertThat(netexCapacity.getParkingUserType()).isEqualTo(ParkingUserEnumeration.ALL_USERS);
    }

    @Test
    public void convertFromKeepsVehicleTypeAndStayType() {
        org.rutebanken.netex.model.ParkingCapacity netexCapacity = netexCapacity()
                .withParkingVehicleType(ParkingVehicleEnumeration.PEDAL_CYCLE)
                .withParkingStayType(ParkingStayEnumeration.SHORT_STAY);

        ParkingCapacity capacity = convertFromSingleCapacity(netexCapacity);

        assertThat(capacity.getParkingVehicleType()).isEqualTo(org.rutebanken.tiamat.model.ParkingVehicleEnumeration.PEDAL_CYCLE);
        assertThat(capacity.getParkingStayType()).isEqualTo(org.rutebanken.tiamat.model.ParkingStayEnumeration.SHORT_STAY);
    }

    /**
     * Every capacity group that the Liipi migration writes for a disabled-user place
     * carries no vehicle type, and no migrated group carries a stay type.
     */
    @Test
    public void convertFromAcceptsACapacityThatCarriesNeitherAttribute() {
        ParkingCapacity capacity = convertFromSingleCapacity(netexCapacity());

        assertThat(capacity.getParkingVehicleType()).isNull();
        assertThat(capacity.getParkingStayType()).isNull();
        assertThat(capacity.getNumberOfSpaces()).isEqualTo(BigInteger.valueOf(42));
    }

    /**
     * The NeTEx vehicle enumeration defines seven constants that the Tiamat enumeration
     * does not: CYCLE, E_CYCLE, MICRO_CAR, MINI_CAR, MINIVAN, TRANSPORTER and SNOWMOBILE.
     * Orika maps an enum by its constant name through Enum.valueOf, which throws for a name
     * the target enumeration does not define. Such a file imports cleanly today, because
     * the converter ignores the attribute, so the import must keep working.
     */
    @Test
    public void convertFromDropsAVehicleTypeThatTiamatDoesNotDefine() {
        org.rutebanken.netex.model.ParkingCapacity netexCapacity = netexCapacity()
                .withParkingVehicleType(ParkingVehicleEnumeration.SNOWMOBILE);

        ParkingCapacity capacity = convertFromSingleCapacity(netexCapacity);

        assertThat(capacity.getParkingVehicleType()).isNull();
        assertThat(capacity.getNumberOfSpaces()).isEqualTo(BigInteger.valueOf(42));
    }

    /**
     * Each parking properties group owns its capacities. The converter built one relation
     * structure for the whole list until DPO-5046, and attached that one structure to every
     * emitted group, so each group claimed the capacities of all the other groups. A vehicle
     * type makes the defect visible: a car group and a bicycle group both listed both counts.
     */
    @Test
    public void convertToGivesEachGroupOnlyItsOwnCapacities() {
        ParkingCapacity carSpaces = tiamatCapacity();
        carSpaces.setParkingVehicleType(org.rutebanken.tiamat.model.ParkingVehicleEnumeration.CAR);

        ParkingCapacity bicycleSpaces = new ParkingCapacity();
        bicycleSpaces.setNetexId("NSR:ParkingCapacity:2");
        bicycleSpaces.setVersion(1L);
        bicycleSpaces.setNumberOfSpaces(BigInteger.valueOf(150));
        bicycleSpaces.setParkingVehicleType(org.rutebanken.tiamat.model.ParkingVehicleEnumeration.PEDAL_CYCLE);

        ParkingProperties carGroup = new ParkingProperties();
        carGroup.setNetexId("NSR:ParkingProperties:1");
        carGroup.setVersion(1L);
        carGroup.setSpaces(List.of(carSpaces));

        ParkingProperties bicycleGroup = new ParkingProperties();
        bicycleGroup.setNetexId("NSR:ParkingProperties:2");
        bicycleGroup.setVersion(1L);
        bicycleGroup.setSpaces(List.of(bicycleSpaces));

        ParkingProperties_RelStructure relStructure = parkingPropertiesListConverter.convertTo(
                List.of(carGroup, bicycleGroup), relStructureType, mappingContext);

        assertThat(relStructure.getParkingProperties()).hasSize(2);
        assertThat(vehicleTypesOf(relStructure, 0)).containsExactly(ParkingVehicleEnumeration.CAR);
        assertThat(vehicleTypesOf(relStructure, 1)).containsExactly(ParkingVehicleEnumeration.PEDAL_CYCLE);
    }

    /**
     * The recharge point count has a NeTEx attribute and a database column, and the Liipi
     * migration writes it for every electric car group.
     */
    @Test
    public void bothDirectionsKeepTheRechargePointCount() {
        ParkingCapacity capacity = tiamatCapacity();
        capacity.setNumberOfSpacesWithRechargePoint(BigInteger.valueOf(8));

        assertThat(convertToSingleCapacity(capacity).getNumberOfSpacesWithRechargePoint())
                .isEqualTo(BigInteger.valueOf(8));

        ParkingCapacity imported = convertFromSingleCapacity(
                netexCapacity().withNumberOfSpacesWithRechargePoint(BigInteger.valueOf(8)));

        assertThat(imported.getNumberOfSpacesWithRechargePoint()).isEqualTo(BigInteger.valueOf(8));
    }

    @Test
    public void convertFromKeepsTheUserType() {
        ParkingCapacity capacity = convertFromSingleCapacity(
                netexCapacity().withParkingUserType(ParkingUserEnumeration.REGISTERED_DISABLED));

        assertThat(capacity.getParkingUserType())
                .isEqualTo(org.rutebanken.tiamat.model.ParkingUserEnumeration.REGISTERED_DISABLED);
    }

    private List<ParkingVehicleEnumeration> vehicleTypesOf(ParkingProperties_RelStructure relStructure, int groupIndex) {
        return relStructure.getParkingProperties().get(groupIndex).getSpaces()
                .getParkingCapacityRefOrParkingCapacity().stream()
                .map(org.rutebanken.netex.model.ParkingCapacity.class::cast)
                .map(org.rutebanken.netex.model.ParkingCapacity::getParkingVehicleType)
                .toList();
    }

    private ParkingCapacity tiamatCapacity() {
        ParkingCapacity capacity = new ParkingCapacity();
        capacity.setNetexId("NSR:ParkingCapacity:1");
        capacity.setVersion(1L);
        capacity.setParkingUserType(org.rutebanken.tiamat.model.ParkingUserEnumeration.ALL_USERS);
        capacity.setNumberOfSpaces(BigInteger.valueOf(42));
        return capacity;
    }

    private org.rutebanken.netex.model.ParkingCapacity netexCapacity() {
        return new org.rutebanken.netex.model.ParkingCapacity()
                .withId("NSR:ParkingCapacity:1")
                .withVersion("1")
                .withParkingUserType(ParkingUserEnumeration.ALL_USERS)
                .withNumberOfSpaces(BigInteger.valueOf(42));
    }

    private org.rutebanken.netex.model.ParkingCapacity convertToSingleCapacity(ParkingCapacity capacity) {
        ParkingProperties parkingProperties = new ParkingProperties();
        parkingProperties.setNetexId("NSR:ParkingProperties:1");
        parkingProperties.setVersion(1L);
        parkingProperties.setSpaces(List.of(capacity));

        ParkingProperties_RelStructure relStructure = parkingPropertiesListConverter.convertTo(
                List.of(parkingProperties), relStructureType, mappingContext);

        assertThat(relStructure).isNotNull();
        assertThat(relStructure.getParkingProperties()).hasSize(1);
        List<Object> spaces = relStructure.getParkingProperties().get(0).getSpaces()
                .getParkingCapacityRefOrParkingCapacity();
        assertThat(spaces).hasSize(1);
        return (org.rutebanken.netex.model.ParkingCapacity) spaces.get(0);
    }

    private ParkingCapacity convertFromSingleCapacity(org.rutebanken.netex.model.ParkingCapacity netexCapacity) {
        org.rutebanken.netex.model.ParkingProperties netexParkingProperties = new org.rutebanken.netex.model.ParkingProperties()
                .withId("NSR:ParkingProperties:1")
                .withVersion("1")
                .withSpaces(new ParkingCapacities_RelStructure()
                        .withParkingCapacityRefOrParkingCapacity(netexCapacity));

        ParkingProperties_RelStructure relStructure = new ParkingProperties_RelStructure()
                .withParkingProperties(netexParkingProperties);

        List<ParkingProperties> converted = parkingPropertiesListConverter.convertFrom(
                relStructure, parkingPropertiesListType, mappingContext);

        assertThat(converted).hasSize(1);
        assertThat(converted.get(0).getSpaces()).hasSize(1);
        return converted.get(0).getSpaces().get(0);
    }
}
