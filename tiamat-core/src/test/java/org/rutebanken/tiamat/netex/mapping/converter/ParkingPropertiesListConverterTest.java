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
 * user type: the vehicle type and the stay type.
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
     * the target enumeration does not define. The converter drops such a name instead, so
     * the rest of the capacity still imports.
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
     * A name that the target enumeration does not define must produce null rather than an
     * exception, so that an unrecognized name cannot fail a whole import. The helper is
     * asserted against a self-contained pair of enumerations, so that the contract holds
     * whatever the parking enumerations later gain or lose.
     */
    @Test
    public void mapEnumByNameReturnsNullForAnUndefinedName() {
        assertThat(ParkingPropertiesListConverter.mapEnumByName(
                WideEnum.ONLY_IN_THE_SOURCE, NarrowEnum.class, "NSR:ParkingCapacity:1")).isNull();
    }

    @Test
    public void mapEnumByNameReturnsNullForANullSource() {
        assertThat(ParkingPropertiesListConverter.mapEnumByName(
                null, NarrowEnum.class, "NSR:ParkingCapacity:1")).isNull();
    }

    @Test
    public void mapEnumByNameKeepsADefinedName() {
        assertThat(ParkingPropertiesListConverter.mapEnumByName(
                NarrowEnum.SHARED, WideEnum.class, "NSR:ParkingCapacity:1"))
                .isEqualTo(WideEnum.SHARED);
    }

    @Test
    public void mapEnumListByNameDropsAnUndefinedName() {
        assertThat(ParkingPropertiesListConverter.mapEnumListByName(
                List.of(WideEnum.SHARED, WideEnum.ONLY_IN_THE_SOURCE), NarrowEnum.class, "NSR:ParkingProperties:1"))
                .containsExactly(NarrowEnum.SHARED);
    }

    @Test
    public void mapEnumListByNameReturnsAnEmptyListForANullSource() {
        assertThat(ParkingPropertiesListConverter.mapEnumListByName(
                null, NarrowEnum.class, "NSR:ParkingProperties:1")).isEmpty();
    }

    private enum NarrowEnum {
        SHARED
    }

    private enum WideEnum {
        SHARED, ONLY_IN_THE_SOURCE
    }

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
