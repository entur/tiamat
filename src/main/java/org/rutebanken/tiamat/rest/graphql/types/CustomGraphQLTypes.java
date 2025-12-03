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

package org.rutebanken.tiamat.rest.graphql.types;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import org.rutebanken.tiamat.diff.generic.SubmodeEnumuration;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.model.AirSubmodeEnumeration;
import org.rutebanken.tiamat.model.AssistanceAvailabilityEnumeration;
import org.rutebanken.tiamat.model.AssistanceFacilityEnumeration;
import org.rutebanken.tiamat.model.BoardingPositionTypeEnumeration;
import org.rutebanken.tiamat.model.BusSubmodeEnumeration;
import org.rutebanken.tiamat.model.CycleStorageEnumeration;
import org.rutebanken.tiamat.model.FunicularSubmodeEnumeration;
import org.rutebanken.tiamat.model.GenderLimitationEnumeration;
import org.rutebanken.tiamat.model.InstalledEquipment_VersionStructure;
import org.rutebanken.tiamat.model.InterchangeWeightingEnumeration;
import org.rutebanken.tiamat.model.LimitationStatusEnumeration;
import org.rutebanken.tiamat.model.LocalService;
import org.rutebanken.tiamat.model.MetroSubmodeEnumeration;
import org.rutebanken.tiamat.model.ModificationEnumeration;
import org.rutebanken.tiamat.model.NameTypeEnumeration;
import org.rutebanken.tiamat.model.ParkingLayoutEnumeration;
import org.rutebanken.tiamat.model.ParkingPaymentProcessEnumeration;
import org.rutebanken.tiamat.model.ParkingReservationEnumeration;
import org.rutebanken.tiamat.model.ParkingStayEnumeration;
import org.rutebanken.tiamat.model.ParkingTypeEnumeration;
import org.rutebanken.tiamat.model.ParkingUserEnumeration;
import org.rutebanken.tiamat.model.ParkingVehicleEnumeration;
import org.rutebanken.tiamat.model.PlaceEquipment;
import org.rutebanken.tiamat.model.RailSubmodeEnumeration;
import org.rutebanken.tiamat.model.ScopingMethodEnumeration;
import org.rutebanken.tiamat.model.SignContentEnumeration;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.model.TelecabinSubmodeEnumeration;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;
import org.rutebanken.tiamat.model.TramSubmodeEnumeration;
import org.rutebanken.tiamat.model.VehicleModeEnumeration;
import org.rutebanken.tiamat.model.WaterSubmodeEnumeration;
import org.rutebanken.tiamat.model.ZoneTopologyEnumeration;
import org.rutebanken.tiamat.rest.graphql.fetchers.PrivateCodeFetcher;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static graphql.Scalars.GraphQLBoolean;
import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.scalars.ExtendedScalars.GraphQLBigInteger;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.scalars.CustomScalars.GraphQLGeoJSONCoordinates;
import static org.rutebanken.tiamat.rest.graphql.scalars.CustomScalars.GraphQLLegacyGeoJSONCoordinates;


public class CustomGraphQLTypes {

    public static GraphQLEnumType geometryTypeEnum = GraphQLEnumType.newEnum()
            .name(GEOMETRY_TYPE_ENUM)
            .value("Point")
            .value("LineString")
            .value("Polygon")
            .value("MultiPoint")
            .value("MultiLineString")
            .value("MultiPolygon")
            .value("GeometryCollection")
            .build();

    public static GraphQLEnumType limitationStatusEnum = GraphQLEnumType.newEnum()
            .name(LIMITATION_STATUS_ENUM)
            .value("FALSE", LimitationStatusEnumeration.FALSE)
            .value("TRUE", LimitationStatusEnumeration.TRUE)
            .value("PARTIAL", LimitationStatusEnumeration.PARTIAL)
            .value("UNKNOWN", LimitationStatusEnumeration.UNKNOWN)
            .build();

    public static GraphQLEnumType parkingVehicleEnum = createCustomEnumType(PARKING_VEHICLE_ENUM, ParkingVehicleEnumeration.class);
    public static GraphQLEnumType parkingLayoutEnum = createCustomEnumType(PARKING_LAYOUT_ENUM, ParkingLayoutEnumeration.class);
    public static GraphQLEnumType parkingUserEnum = createCustomEnumType(PARKING_USER_ENUM, ParkingUserEnumeration.class);
    public static GraphQLEnumType parkingStayEnum = createCustomEnumType(PARKING_STAY_TYPE_ENUM, ParkingStayEnumeration.class);
    public static GraphQLEnumType parkingReservationEnum = createCustomEnumType(PARKING_RESERVATION_ENUM, ParkingReservationEnumeration.class);
    public static GraphQLEnumType parkingPaymentProcessEnum = createCustomEnumType(PARKING_PAYMENT_PROCESS_ENUM, ParkingPaymentProcessEnumeration.class);
    public static GraphQLEnumType parkingTypeEnum = createCustomEnumType(PARKING_TYPE_ENUM, ParkingTypeEnumeration.class);
    public static GraphQLEnumType topographicPlaceTypeEnum = createCustomEnumType(TOPOGRAPHIC_PLACE_TYPE_ENUM, TopographicPlaceTypeEnumeration.class);
    public static GraphQLEnumType stopPlaceTypeEnum = createCustomEnumType(STOP_PLACE_TYPE_ENUM, StopTypeEnumeration.class);
    public static GraphQLEnumType submodeEnum = createCustomEnumType(SUBMODE_ENUM, SubmodeEnumuration.class);
    public static GraphQLEnumType interchangeWeightingEnum = createCustomEnumType(INTERCHANGE_WEIGHTING_TYPE_ENUM, InterchangeWeightingEnumeration.class);
    public static GraphQLEnumType cycleStorageTypeEnum = createCustomEnumType(CYCLE_STORAGE_TYPE, CycleStorageEnumeration.class);
    public static GraphQLEnumType signContentTypeEnum = createCustomEnumType(SIGN_CONTENT_TYPE, SignContentEnumeration.class);
    public static GraphQLEnumType genderTypeEnum = createCustomEnumType(GENDER, GenderLimitationEnumeration.class);
    public static GraphQLEnumType nameTypeEnum = createCustomEnumType(NAME_TYPE, NameTypeEnumeration.class);
    public static GraphQLEnumType boardingPositionTypeEnum = createCustomEnumType("BoardingPositionType", BoardingPositionTypeEnumeration.class);
    public static GraphQLEnumType allVehiclesModesOfTransportationEnum = createCustomEnumType(TRANSPORT_MODE_TYPE, VehicleModeEnumeration.class);
    public static GraphQLEnumType busSubmodeType = createCustomEnumType("BusSubmodeType", BusSubmodeEnumeration.class);
    public static GraphQLEnumType tramSubmodeType = createCustomEnumType("TramSubmodeType", TramSubmodeEnumeration.class);
    public static GraphQLEnumType railSubmodeType = createCustomEnumType("RailSubmodeType", RailSubmodeEnumeration.class);
    public static GraphQLEnumType metroSubmodeType = createCustomEnumType("MetroSubmodeType", MetroSubmodeEnumeration.class);
    public static GraphQLEnumType airSubmodeType = createCustomEnumType("AirSubmodeType", AirSubmodeEnumeration.class);
    public static GraphQLEnumType waterSubmodeType = createCustomEnumType("WaterSubmodeType", WaterSubmodeEnumeration.class);
    public static GraphQLEnumType cablewaySubmodeType = createCustomEnumType("TelecabinSubmodeType", TelecabinSubmodeEnumeration.class);
    public static GraphQLEnumType funicularSubmodeType = createCustomEnumType("FunicularSubmodeType", FunicularSubmodeEnumeration.class);
    public static GraphQLEnumType versionValidityEnumType = createCustomEnumType(ExportParams.VersionValidity.class.getSimpleName(), ExportParams.VersionValidity.class);
    public static GraphQLEnumType modificationEnumerationType = createCustomEnumType("ModificationEnumerationType", ModificationEnumeration.class);
    public static GraphQLEnumType scopingMethodEnumType = createCustomEnumType("ScopingMethodEnumerationType", ScopingMethodEnumeration.class);
    public static GraphQLEnumType zoneTopologyEnumType = createCustomEnumType("ZoneTopologyEnumerationType", ZoneTopologyEnumeration.class);
    public static GraphQLEnumType assistanceFacilityEnumType = createCustomEnumType(ASSISTANCE_FACILITY_TYPE, AssistanceFacilityEnumeration.class);
    public static GraphQLEnumType assistanceAvailabilityEnumType = createCustomEnumType(ASSISTANCE_AVAILABILITY_TYPE, AssistanceAvailabilityEnumeration.class);

    public static GraphQLEnumType createCustomEnumType(String name, Class c) {

        Object[] enumConstants = c.getEnumConstants();

        GraphQLEnumType.Builder builder = GraphQLEnumType.newEnum().name(name);
        for (Object enumObj : enumConstants) {
            boolean valueWasSetFromValueMethod = false;
            Method[] methods = enumObj.getClass().getMethods();
            for (Method method : methods) {

                if (method.getParameterCount() == 0 && "value".equals(method.getName())) {
                    try {
                        builder.value((String) method.invoke(enumObj), enumObj);
                        valueWasSetFromValueMethod = true;
                    } catch (Exception e) {
                        throw new ExceptionInInitializerError(e);
                    }
                }


            }
            if (!valueWasSetFromValueMethod) {
                builder.value(enumObj.toString());
            }
        }
        return builder.build();
    }

    public static GraphQLObjectType geoJsonObjectType = newObject()
            .name(OUTPUT_TYPE_GEO_JSON)
            .description("Geometry-object as specified in the GeoJSON-standard (https://geojson.org/geojson-spec.html).")
            .field(newFieldDefinition()
                    .name(TYPE)
                    .type(geometryTypeEnum))
            .field(newFieldDefinition()
                    .name(LEGACY_COORDINATES)
                    .description("non standard coordinates")
                    .deprecate("no standard coordinates, should be removed in future versions")
                    .type(GraphQLLegacyGeoJSONCoordinates))

            .field(newFieldDefinition()
                    .name(COORDINATES)
                    .description("GeoJSON-standard coordinates")
                    .type(GraphQLGeoJSONCoordinates))


            .build();

    public static GraphQLInputObjectType geoJsonInputType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_GEO_JSON)
            .description("Geometry-object as specified in the GeoJSON-standard (https://geojson.org/geojson-spec.html).")
            .field(newInputObjectField()
                    .name(TYPE)
                    .type(new GraphQLNonNull(geometryTypeEnum))
                    .build())
            .field(newInputObjectField()
                    .name(LEGACY_COORDINATES)
                    .description("non-standard coordinates")
                    .type(GraphQLLegacyGeoJSONCoordinates)
                    .build())
            .field(newInputObjectField()
                    .name(COORDINATES)
                    .description("GeoJSON coordinates")
                    .type(GraphQLGeoJSONCoordinates))
            .build();


    public static GraphQLFieldDefinition geometryFieldDefinition = newFieldDefinition()
            .name(GEOMETRY)
            .type(geoJsonObjectType)
            .build();

    public static GraphQLObjectType embeddableMultilingualStringObjectType = newObject()
            .name(OUTPUT_TYPE_EMBEDDABLE_MULTILINGUAL_STRING)
            .field(newFieldDefinition()
                    .name(VALUE)
                    .type(GraphQLString))
            .field(newFieldDefinition()
                    .name(LANG)
                    .type(GraphQLString))
            .build();


    public static GraphQLInputObjectType embeddableMultiLingualStringInputObjectType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_EMBEDDABLE_MULTILINGUAL_STRING)
            .field(newInputObjectField()
                    .name(VALUE)
                    .type(GraphQLString))
            .field(newInputObjectField()
                    .name(LANG)
                    .type(GraphQLString))
            .build();

    public static GraphQLObjectType keyValuesObjectType = newObject()
            .name(OUTPUT_TYPE_KEY_VALUES)
            .field(newFieldDefinition()
                    .name(KEY)
                    .type(GraphQLString))
            .field(newFieldDefinition()
                    .name(VALUES)
                    .type(new GraphQLList(GraphQLString)))
            .build();


    public static GraphQLInputObjectType keyValuesObjectInputType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_KEY_VALUES)
            .field(newInputObjectField()
                    .name(KEY)
                    .type(GraphQLString))
            .field(newInputObjectField()
                    .name(VALUES)
                    .type(new GraphQLList(GraphQLString)))
            .build();

    public static GraphQLFieldDefinition netexIdFieldDefinition = newFieldDefinition()
            .name(ID)
            .type(GraphQLString)
            .build();


    public static GraphQLObjectType shelterEquipmentType = newObject()
            .name(OUTPUT_TYPE_SHELTER_EQUIPMENT)
            .field(netexIdFieldDefinition)
            .field(newFieldDefinition()
                    .name(SEATS)
                    .type(GraphQLBigInteger))
            .field(newFieldDefinition()
                    .name(STEP_FREE)
                    .type(GraphQLBoolean))
            .field(newFieldDefinition()
                    .name(ENCLOSED)
                    .type(GraphQLBoolean))
            .build();

    public static GraphQLInputObjectType shelterEquipmentInputType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_SHELTER_EQUIPMENT)
            .field(newInputObjectField()
                    .name(SEATS)
                    .type(GraphQLBigInteger))
            .field(newInputObjectField()
                    .name(STEP_FREE)
                    .type(GraphQLBoolean))
            .field(newInputObjectField()
                    .name(ENCLOSED)
                    .type(GraphQLBoolean))
            .build();

    public static GraphQLObjectType ticketingEquipmentType = newObject()
            .name(OUTPUT_TYPE_TICKETING_EQUIPMENT)
            .field(netexIdFieldDefinition)
            .field(newFieldDefinition()
                    .name(TICKET_OFFICE)
                    .type(GraphQLBoolean))
            .field(newFieldDefinition()
                    .name(TICKET_MACHINES)
                    .type(GraphQLBoolean))
            .field(newFieldDefinition()
                    .name(NUMBER_OF_MACHINES)
                    .type(GraphQLBigInteger))
            .field(newFieldDefinition()
                    .name(AUDIO_INTERFACE_AVAILABLE)
                    .type(GraphQLBoolean))
            .field(newFieldDefinition()
                    .name(TACTILE_INTERFACE_AVAILABLE)
                    .type(GraphQLBoolean))
            .field(newFieldDefinition()
                    .name(TICKET_COUNTER)
                    .type(GraphQLBoolean))
            .field(newFieldDefinition()
                    .name(INDUCTION_LOOPS)
                    .type(GraphQLBoolean))
            .field(newFieldDefinition()
                    .name(LOW_COUNTER_ACCESS)
                    .type(GraphQLBoolean))
            .build();


    public static GraphQLInputObjectType ticketingEquipmentInputType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_TICKETING_EQUIPMENT)
            .field(newInputObjectField()
                    .name(TICKET_OFFICE)
                    .type(GraphQLBoolean))
            .field(newInputObjectField()
                    .name(TICKET_MACHINES)
                    .type(GraphQLBoolean))
            .field(newInputObjectField()
                    .name(NUMBER_OF_MACHINES)
                    .type(GraphQLBigInteger))
            .field(newInputObjectField()
                    .name(AUDIO_INTERFACE_AVAILABLE)
                    .type(GraphQLBoolean))
            .field(newInputObjectField()
                    .name(TACTILE_INTERFACE_AVAILABLE)
                    .type(GraphQLBoolean))
            .field(newInputObjectField()
                    .name(TICKET_COUNTER)
                    .type(GraphQLBoolean))
            .field(newInputObjectField()
                    .name(INDUCTION_LOOPS)
                    .type(GraphQLBoolean))
            .field(newInputObjectField()
                    .name(LOW_COUNTER_ACCESS)
                    .type(GraphQLBoolean))
            .build();

    public static GraphQLObjectType cycleStorageEquipmentType = newObject()
            .name(OUTPUT_TYPE_CYCLE_STORAGE_EQUIPMENT)
            .field(netexIdFieldDefinition)
            .field(newFieldDefinition()
                    .name(NUMBER_OF_SPACES)
                    .type(GraphQLBigInteger))
            .field(newFieldDefinition()
                    .name(CYCLE_STORAGE_TYPE)
                    .type(cycleStorageTypeEnum))
            .build();


    public static GraphQLInputObjectType cycleStorageEquipmentInputType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_CYCLE_STORAGE_EQUIPMENT)
            .field(newInputObjectField()
                    .name(NUMBER_OF_SPACES)
                    .type(GraphQLBigInteger))
            .field(newInputObjectField()
                    .name(CYCLE_STORAGE_TYPE)
                    .type(cycleStorageTypeEnum))
            .build();

    /**
     * Not using DI here because everything here is made evil static
     */
    private static PrivateCodeFetcher privateCodeFetcher = new PrivateCodeFetcher();

    public static GraphQLObjectType privateCodeObjectType = newObject()
            .name(OUTPUT_TYPE_PRIVATE_CODE)
            .field(newFieldDefinition()
                    .name(TYPE)
                    .type(GraphQLString))
            .field(newFieldDefinition()
                    .name(VALUE)
                    .type(GraphQLString))
            .build();

    public static GraphQLFieldDefinition privateCodeFieldDefinition = newFieldDefinition()
            .name(PRIVATE_CODE)
            .type(privateCodeObjectType)
            .build();

    public static GraphQLInputObjectType privateCodeInputType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_PRIVATE_CODE)
            .field(newInputObjectField()
                    .name(TYPE)
                    .type(GraphQLString))
            .field(newInputObjectField()
                    .name(VALUE)
                    .type(new GraphQLNonNull(GraphQLString)))
            .build();

    public static GraphQLObjectType generalSignEquipmentType = newObject()
            .name(OUTPUT_TYPE_GENERAL_SIGN_EQUIPMENT)
            .field(netexIdFieldDefinition)
            .field(privateCodeFieldDefinition)
            .field(newFieldDefinition()
                    .name(CONTENT)
                    .type(embeddableMultilingualStringObjectType))
            .field(newFieldDefinition()
                    .name(SIGN_CONTENT_TYPE)
                    .type(signContentTypeEnum))
            .build();


    public static GraphQLInputObjectType generalSignEquipmentInputType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_GENERAL_SIGN_EQUIPMENT)
            .field(newInputObjectField()
                    .name(PRIVATE_CODE)
                    .type(privateCodeInputType))
            .field(newInputObjectField()
                    .name(CONTENT)
                    .type(embeddableMultiLingualStringInputObjectType))
            .field(newInputObjectField()
                    .name(SIGN_CONTENT_TYPE)
                    .type(signContentTypeEnum))
            .build();

    public static GraphQLObjectType waitingRoomEquipmentType = newObject()
            .name(OUTPUT_TYPE_WAITING_ROOM_EQUIPMENT)
            .field(netexIdFieldDefinition)
            .field(newFieldDefinition()
                    .name(SEATS)
                    .type(GraphQLBigInteger))
            .field(newFieldDefinition()
                    .name(HEATED)
                    .type(GraphQLBoolean))
            .field(newFieldDefinition()
                    .name(STEP_FREE)
                    .type(GraphQLBoolean))
            .build();

    public static GraphQLInputObjectType waitingRoomEquipmentInputType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_WAITING_ROOM_EQUIPMENT)
            .field(newInputObjectField()
                    .name(SEATS)
                    .type(GraphQLBigInteger))
            .field(newInputObjectField()
                    .name(HEATED)
                    .type(GraphQLBoolean))
            .field(newInputObjectField()
                    .name(STEP_FREE)
                    .type(GraphQLBoolean))
            .build();

    public static GraphQLObjectType sanitaryEquipmentType = newObject()
            .name(OUTPUT_TYPE_SANITARY_EQUIPMENT)
            .field(netexIdFieldDefinition)
            .field(newFieldDefinition()
                    .name(NUMBER_OF_TOILETS)
                    .type(GraphQLBigInteger))
            .field(newFieldDefinition()
                    .name(GENDER)
                    .type(genderTypeEnum))
            .build();


    public static GraphQLInputObjectType sanitaryEquipmentInputType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_SANITARY_EQUIPMENT)
            .field(newInputObjectField()
                    .name(NUMBER_OF_TOILETS)
                    .type(GraphQLBigInteger))
            .field(newInputObjectField()
                    .name(GENDER)
                    .type(genderTypeEnum))
            .build();


    public static GraphQLObjectType equipmentType = newObject()
            .name(OUTPUT_TYPE_PLACE_EQUIPMENTS)
            .field(netexIdFieldDefinition)
            .field(newFieldDefinition()
                    .name(WAITING_ROOM_EQUIPMENT)
                    .type(new GraphQLList(waitingRoomEquipmentType))
            )
            .field(newFieldDefinition()
                    .name(SANITARY_EQUIPMENT)
                    .type(new GraphQLList(sanitaryEquipmentType))
            )
            .field(newFieldDefinition()
                    .name(TICKETING_EQUIPMENT)
                    .type(new GraphQLList(ticketingEquipmentType))
            )
            .field(newFieldDefinition()
                    .name(SHELTER_EQUIPMENT)
                    .type(new GraphQLList(shelterEquipmentType))
            )
            .field(newFieldDefinition()
                    .name(CYCLE_STORAGE_EQUIPMENT)
                    .type(new GraphQLList(cycleStorageEquipmentType))
            )
            .field(newFieldDefinition()
                    .name(GENERAL_SIGN)
                    .type(new GraphQLList(generalSignEquipmentType))
            )
            .build();

    public static List getEquipmentOfType(Class clazz, DataFetchingEnvironment env) {
        List<InstalledEquipment_VersionStructure> installedEquipment = ((PlaceEquipment) env.getSource()).getInstalledEquipment();
        List equipments = new ArrayList<>();
        for (InstalledEquipment_VersionStructure ie : installedEquipment) {
            if (clazz.isInstance(ie)) {
                equipments.add(ie);
            }
        }

        if (!equipments.isEmpty()) {
            return equipments;
        }
        return null;
    }

    public static GraphQLInputObjectType equipmentInputType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_PLACE_EQUIPMENTS)
            .field(newInputObjectField()
                    .name(WAITING_ROOM_EQUIPMENT)
                    .type(new GraphQLList(waitingRoomEquipmentInputType)))
            .field(newInputObjectField()
                    .name(SANITARY_EQUIPMENT)
                    .type(new GraphQLList(sanitaryEquipmentInputType)))
            .field(newInputObjectField()
                    .name(TICKETING_EQUIPMENT)
                    .type(new GraphQLList(ticketingEquipmentInputType)))
            .field(newInputObjectField()
                    .name(SHELTER_EQUIPMENT)
                    .type(new GraphQLList(shelterEquipmentInputType)))
            .field(newInputObjectField()
                    .name(CYCLE_STORAGE_EQUIPMENT)
                    .type(new GraphQLList(cycleStorageEquipmentInputType)))
            .field(newInputObjectField()
                    .name(GENERAL_SIGN)
                    .type(new GraphQLList(generalSignEquipmentInputType)))
            .build();


    public static GraphQLInputObjectType assistanceServiceInputType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_ASSISTANCE_SERVICE)
            .field(newInputObjectField()
                    .name(ASSISTANCE_FACILITY_LIST)
                    .type(new GraphQLList(assistanceFacilityEnumType)))
            .field(newInputObjectField()
                    .name(ASSISTANCE_AVAILABILITY)
                    .type(assistanceAvailabilityEnumType))
            .build();

    public static GraphQLInputObjectType localServiceInputType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_LOCAL_SERVICES)
            .field(newInputObjectField()
                    .name(ASSISTANCE_SERVICE)
                    .type(new GraphQLList(assistanceServiceInputType)))
            .build();

    public static GraphQLObjectType assistanceServiceOutputType = newObject()
            .name(OUTPUT_TYPE_ASSISTANCE_SERVICE)
            .field(netexIdFieldDefinition)
            .field(newFieldDefinition()
                    .name(ASSISTANCE_FACILITY_LIST)
                    .type(new GraphQLList(assistanceFacilityEnumType)))
            .field(newFieldDefinition()
                    .name(ASSISTANCE_AVAILABILITY)
                    .type(assistanceAvailabilityEnumType))
            .build();

    public static GraphQLObjectType localServiceType = newObject()
            .name(OUTPUT_TYPE_LOCAL_SERVICES)
            .field(newFieldDefinition()
                    .name(ASSISTANCE_SERVICE)
                    .type(new GraphQLList(assistanceServiceOutputType))
            )
            .build();

    public static List getLocalServiceOfType(Class clazz, DataFetchingEnvironment env) {
        List<LocalService> localServices = env.getSource();
        List localServicesOfType = new ArrayList<>();
        for (LocalService ls : localServices) {
            if (clazz.isInstance(ls)) {
                localServicesOfType.add(ls);
            }
        }

        if (!localServicesOfType.isEmpty()) {
            return localServicesOfType;
        }
        return null;
    }

    public static GraphQLObjectType accessibilityLimitationsObjectType = newObject()
            .name(OUTPUT_TYPE_ACCESSIBILITY_LIMITATIONS)
            .field(newFieldDefinition()
                    .name(ID)
                    .type(GraphQLString)
            )
            .field(newFieldDefinition()
                    .name(VERSION)
                    .type(GraphQLString))
            .field(newFieldDefinition()
                    .name(WHEELCHAIR_ACCESS)
                    .type(limitationStatusEnum))
            .field(newFieldDefinition()
                    .name(STEP_FREE_ACCESS)
                    .type(limitationStatusEnum))
            .field(newFieldDefinition()
                    .name(ESCALATOR_FREE_ACCESS)
                    .type(limitationStatusEnum))
            .field(newFieldDefinition()
                    .name(LIFT_FREE_ACCESS)
                    .type(limitationStatusEnum))
            .field(newFieldDefinition()
                    .name(AUDIBLE_SIGNALS_AVAILABLE)
                    .type(limitationStatusEnum))
            .field(newFieldDefinition()
                    .name(VISUAL_SIGNS_AVAILABLE)
                    .type(limitationStatusEnum))
            .build();

    public static GraphQLObjectType accessibilityAssessmentObjectType = newObject()
            .name(OUTPUT_TYPE_ACCESSIBILITY_ASSESSMENT)
            .field(newFieldDefinition()
                    .name(ID)
                    .type(GraphQLString)
            )
            .field(newFieldDefinition()
                    .name(VERSION)
                    .type(GraphQLString))
            .field(newFieldDefinition()
                    .name(LIMITATIONS)
                    .type(accessibilityLimitationsObjectType)
            )
            .field(newFieldDefinition()
                    .name(MOBILITY_IMPAIRED_ACCESS)
                    .type(limitationStatusEnum))
            .build();


    public static GraphQLInputObjectType accessibilityLimitationsInputObjectType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_ACCESSIBILITY_LIMITATIONS)
            .field(newInputObjectField()
                    .name(ID)
                    .type(GraphQLString))
            .field(newInputObjectField()
                    .name(WHEELCHAIR_ACCESS)
                    .type(new GraphQLNonNull(limitationStatusEnum)))
            .field(newInputObjectField()
                    .name(STEP_FREE_ACCESS)
                    .type(new GraphQLNonNull(limitationStatusEnum)))
            .field(newInputObjectField()
                    .name(ESCALATOR_FREE_ACCESS)
                    .type(new GraphQLNonNull(limitationStatusEnum)))
            .field(newInputObjectField()
                    .name(LIFT_FREE_ACCESS)
                    .type(new GraphQLNonNull(limitationStatusEnum)))
            .field(newInputObjectField()
                    .name(AUDIBLE_SIGNALS_AVAILABLE)
                    .type(new GraphQLNonNull(limitationStatusEnum)))
            .field(newInputObjectField()
                    .name(VISUAL_SIGNS_AVAILABLE)
                    .type(new GraphQLNonNull(limitationStatusEnum)))
            .build();

    public static GraphQLInputObjectType accessibilityAssessmentInputObjectType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_ACCESSIBILITY_ASSESSMENT)
            .field(newInputObjectField()
                    .name(ID)
                    .type(GraphQLString))
            .field(newInputObjectField()
                    .name(LIMITATIONS)
                    .type(accessibilityLimitationsInputObjectType))
            .build();

    public static GraphQLObjectType alternativeNameObjectType = newObject()
            .name(OUTPUT_TYPE_ALTERNATIVE_NAME)
            .field(newFieldDefinition()
                    .name(NAME_TYPE)
                    .type(new GraphQLNonNull(nameTypeEnum)))
            .field(newFieldDefinition()
                    .name(NAME)
                    .type(new GraphQLNonNull(embeddableMultilingualStringObjectType)))
            .build();


    public static GraphQLInputObjectType alternativeNameInputObjectType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_ALTERNATIVE_NAME)
            .field(newInputObjectField()
                    .name(NAME_TYPE)
                    .type(nameTypeEnum))
            .field(newInputObjectField()
                    .name(NAME)
                    .type(new GraphQLNonNull(embeddableMultiLingualStringInputObjectType)))
            .build();

    public static GraphQLObjectType boardingPositionsObjectType = GraphQLObjectType.newObject()
            .name(OUTPUT_TYPE_BOARDING_POSITION)
            .field(newFieldDefinition()
                .name(ID)
                .type(GraphQLString)
            )
            .field(newFieldDefinition()
                .name(PUBLIC_CODE)
                .type(GraphQLString))
            .field(geometryFieldDefinition)
            .build();


    public static GraphQLInputObjectType boardingPositionsInputObjectType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_BOARDING_POSITION)
            .field(newInputObjectField()
                    .name(ID)
                    .type(GraphQLString))
            .field(newInputObjectField()
                .name(PUBLIC_CODE)
                .type(GraphQLString))
            .field(newInputObjectField()
                    .name(GEOMETRY)
                    .type(geoJsonInputType))
            .build();



    public static GraphQLInputObjectType topographicPlaceInputObjectType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_TOPOGRAPHIC_PLACE)
            .field(newInputObjectField()
                    .name(ID)
                    .type(GraphQLString))
            .field(newInputObjectField()
                    .name(TOPOGRAPHIC_PLACE_TYPE)
                    .type(topographicPlaceTypeEnum))
            .field(newInputObjectField()
                    .name(NAME)
                    .type(embeddableMultiLingualStringInputObjectType))
            .build();

    public static GraphQLInputObjectType transferDurationInputObjectType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_TRANSFER_DURATION)
            .description(TRANSFER_DURATION_DESCRIPTION)
            .field(newInputObjectField()
                    .name(ID)
                    .type(GraphQLString))
            .field(newInputObjectField()
                    .name(DEFAULT_DURATION)
                    .type(GraphQLInt)
                    .description(DEFAULT_DURATION_DESCRIPTION))
            .field(newInputObjectField()
                    .name(FREQUENT_TRAVELLER_DURATION)
                    .type(GraphQLInt)
                    .description(FREQUENT_TRAVELLER_DURATION_DESCRIPTION))
            .field(newInputObjectField()
                    .name(OCCASIONAL_TRAVELLER_DURATION)
                    .type(GraphQLInt)
                    .description(OCCASIONAL_TRAVELLER_DURATION_DESCRIPTION))
            .field(newInputObjectField()
                    .name(MOBILITY_RESTRICTED_TRAVELLER_DURATION)
                    .type(GraphQLInt)
                    .description(MOBILITY_RESTRICTED_TRAVELLER_DURATION_DESCRIPTION))
            .build();

    public static GraphQLInputObjectType refInputObjectType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_ENTITY_REF)
            .description(ENTITY_REF_DESCRIPTION)
            .field(newInputObjectField()
                    .name(ENTITY_REF_REF)
                    .type(new GraphQLNonNull(GraphQLString))
                    .description(ENTITY_REF_REF_DESCRIPTION))
            .field(newInputObjectField()
                    .name(ENTITY_REF_VERSION)
                    .type(GraphQLString)
                    .description(ENTITY_REF_VERSION_DESCRIPTION))
            .build();

    /**
     * Versionless refInputObjectType
     */
    public static GraphQLInputObjectType versionLessRefInputObjectType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_VERSION_LESS_ENTITY_REF)
            .description(VERSION_LESS_ENTITY_REF_DESCRIPTION)
            .field(newInputObjectField()
                    .name(ENTITY_REF_REF)
                    .type(new GraphQLNonNull(GraphQLString))
                    .description(ENTITY_REF_REF_DESCRIPTION))
            .build();

    public static GraphQLInputType pathLinkEndInputObjectType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_PATH_LINK_END)
            .field(newInputObjectField()
                    .name(PATH_LINK_END_PLACE_REF)
                    .type(refInputObjectType))
            .build();

    public static GraphQLInputObjectType pathLinkObjectInputType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_PATH_LINK)
            .field(newInputObjectField()
                    .name(ID)
                    .type(GraphQLString))
            .field(newInputObjectField()
                    .name(PATH_LINK_FROM)
                    .type(pathLinkEndInputObjectType))
            .field(newInputObjectField()
                    .name(PATH_LINK_TO)
                    .type(pathLinkEndInputObjectType))
            .field(newInputObjectField()
                    .name(TRANSFER_DURATION)
                    .type(transferDurationInputObjectType))
            .field(newInputObjectField()
                    .name(GEOMETRY)
                    .type(geoJsonInputType))
            .description("Transfer durations in seconds")
            .build();


    public static GraphQLObjectType parkingCapacityObjectType = newObject()
            .name(OUTPUT_TYPE_PARKING_CAPACITY)
            .field(newFieldDefinition()
                    .name(PARKING_VEHICLE_TYPE)
                    .type(parkingVehicleEnum))
            .field(newFieldDefinition()
                    .name(PARKING_USER_TYPE)
                    .type(parkingUserEnum))
            .field(newFieldDefinition()
                    .name(PARKING_STAY_TYPE)
                    .type(parkingStayEnum))
            .field(newFieldDefinition()
                    .name(NUMBER_OF_SPACES)
                    .type(GraphQLBigInteger))
            .field(newFieldDefinition()
                    .name(NUMBER_OF_SPACES_WITH_RECHARGE_POINT)
                    .type(GraphQLBigInteger))
            .build();


    public static GraphQLInputObjectType parkingCapacityInputObjectType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_PARKING_CAPACITY)
            .field(newInputObjectField()
                    .name(PARKING_USER_TYPE)
                    .type(parkingUserEnum))
            .field(newInputObjectField()
                    .name(PARKING_VEHICLE_TYPE)
                    .type(parkingVehicleEnum))
            .field(newInputObjectField()
                    .name(PARKING_STAY_TYPE)
                    .type(parkingStayEnum))
            .field(newInputObjectField()
                    .name(NUMBER_OF_SPACES)
                    .type(GraphQLBigInteger))
            .field(newInputObjectField()
                    .name(NUMBER_OF_SPACES_WITH_RECHARGE_POINT)
                    .type(GraphQLBigInteger))
            .build();

    public static GraphQLObjectType parkingPropertiesObjectType = newObject()
            .name(OUTPUT_TYPE_PARKING_PROPERTIES)
            .field(newFieldDefinition()
                    .name(PARKING_USER_TYPES)
                    .type(new GraphQLList(parkingUserEnum)))
            .field(newFieldDefinition()
                    .name(MAXIMUM_STAY)
                    .type(GraphQLBigInteger))
            .field(newFieldDefinition()
                    .name(SPACES)
                    .type(new GraphQLList(parkingCapacityObjectType)))
            .build();

    public static GraphQLInputObjectType parkingPropertiesInputObjectType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_PARKING_PROPERTIES)
            .field(newInputObjectField()
                    .name(PARKING_USER_TYPES)
                    .type(new GraphQLList(parkingUserEnum)))
            .field(newInputObjectField()
                    .name(MAXIMUM_STAY)
                    .type(GraphQLBigInteger))
            .field(newInputObjectField()
                    .name(SPACES)
                    .type(new GraphQLList(parkingCapacityInputObjectType)))
            .build();

    public static GraphQLObjectType parkingAreaObjectType = newObject()
            .name(OUTPUT_TYPE_PARKING_AREA)
            .field(newFieldDefinition()
                    .name(LABEL)
                    .type(embeddableMultilingualStringObjectType))
            .field(newFieldDefinition()
                    .name(TOTAL_CAPACITY)
                    .type(GraphQLBigInteger))
            .field(newFieldDefinition()
                    .name(PARKING_PROPERTIES)
                    .type(parkingPropertiesObjectType))
            .build();

    public static GraphQLInputObjectType parkingAreaInputObjectType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_PARKING_AREA)
            .field(newInputObjectField()
                    .name(LABEL)
                    .type(embeddableMultiLingualStringInputObjectType))
            .field(newInputObjectField()
                    .name(TOTAL_CAPACITY)
                    .type(GraphQLBigInteger))
            .field(newInputObjectField()
                    .name(PARKING_PROPERTIES)
                    .type(parkingPropertiesInputObjectType))
            .build();

    public static GraphQLObjectType postalAddressObjectType = GraphQLObjectType.newObject()
            .name(OUTPUT_TYPE_POSTAL_ADDRESS)
            .field(newFieldDefinition()
                    .name(POSTAL_ADDRESS_ADDRESS_LINE1)
                    .type(embeddableMultilingualStringObjectType))
            .field(newFieldDefinition()
                    .name(POSTAL_ADDRESS_TOWN)
                    .type(embeddableMultilingualStringObjectType))
            .field(newFieldDefinition()
                    .name(POSTAL_ADDRESS_POST_CODE)
                    .type(GraphQLString))
            .build();

    public static GraphQLInputObjectType postalAddressInputObjectType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_POSTAL_ADDRESS)
            .field(newInputObjectField()
                    .name(POSTAL_ADDRESS_ADDRESS_LINE1)
                    .type(embeddableMultiLingualStringInputObjectType))
            .field(newInputObjectField()
                    .name(POSTAL_ADDRESS_TOWN)
                    .type(embeddableMultiLingualStringInputObjectType))
            .field(newInputObjectField()
                    .name(POSTAL_ADDRESS_POST_CODE)
                    .type(GraphQLString))
            .build();

    public static GraphQLObjectType transportModeSubmodeObjectType = newObject()
            .name("TransportModes")
            .field(newFieldDefinition()
                    .name("transportMode")
                    .type(GraphQLString)
            )
            .field(newFieldDefinition()
                    .name("submode")
                    .type(new GraphQLList(GraphQLString))
            )
            .build();

    public static GraphQLObjectType createParkingObjectType(GraphQLObjectType validBetweenObjectType) {
        return newObject()
                .name(OUTPUT_TYPE_PARKING)
                .field(netexIdFieldDefinition)
                .field(newFieldDefinition()
                        .name(VERSION)
                        .type(GraphQLString))
                .field(newFieldDefinition()
                        .name(NAME)
                        .type(embeddableMultilingualStringObjectType))
                .field(newFieldDefinition()
                        .name(VALID_BETWEEN)
                        .type(validBetweenObjectType))
                .field(newFieldDefinition()
                        .name(PARENT_SITE_REF)
                        .type(GraphQLString)
                )
                .field(newFieldDefinition()
                        .name(TOTAL_CAPACITY)
                        .type(GraphQLBigInteger))
                .field(newFieldDefinition()
                        .name(PARKING_TYPE)
                        .type(parkingTypeEnum))
                .field(newFieldDefinition()
                        .name(PARKING_VEHICLE_TYPES)
                        .type(new GraphQLList(parkingVehicleEnum)))
                .field(newFieldDefinition()
                        .name(PARKING_LAYOUT)
                        .type(parkingLayoutEnum))
                .field(newFieldDefinition()
                        .name(PRINCIPAL_CAPACITY)
                        .type(GraphQLBigInteger))
                .field(newFieldDefinition()
                        .name(OVERNIGHT_PARKING_PERMITTED)
                        .type(GraphQLBoolean))
                .field(newFieldDefinition()
                        .name(RECHARGING_AVAILABLE)
                        .type(GraphQLBoolean))
                .field(newFieldDefinition()
                        .name(SECURE)
                        .type(GraphQLBoolean))
                .field(newFieldDefinition()
                        .name(REAL_TIME_OCCUPANCY_AVAILABLE)
                        .type(GraphQLBoolean))
                .field(newFieldDefinition()
                        .name(PARKING_RESERVATION)
                        .type(parkingReservationEnum))
                .field(newFieldDefinition()
                        .name(BOOKING_URL)
                        .type(GraphQLString))
                .field(newFieldDefinition()
                        .name(FREE_PARKING_OUT_OF_HOURS)
                        .type(GraphQLBoolean))
                .field(newFieldDefinition()
                        .name(PARKING_PAYMENT_PROCESS)
                        .type(new GraphQLList(parkingPaymentProcessEnum)))
                .field(newFieldDefinition()
                        .name(PARKING_PROPERTIES)
                        .type(new GraphQLList(parkingPropertiesObjectType)))
                .field(newFieldDefinition()
                        .name(PARKING_AREAS)
                        .type(new GraphQLList(parkingAreaObjectType)))
                .field(geometryFieldDefinition)
                .build();
    }

    public static GraphQLInputObjectType createParkingInputObjectType(GraphQLInputObjectType validBetweenInputObjectType) {
        return GraphQLInputObjectType.newInputObject()
                .name(INPUT_TYPE_PARKING)
                .field(newInputObjectField()
                        .name(ID)
                        .type(GraphQLString))
                .field(newInputObjectField()
                        .name(NAME)
                        .type(embeddableMultiLingualStringInputObjectType))
                .field(newInputObjectField()
                        .name(PARENT_SITE_REF)
                        .type(GraphQLString))
                //todo: it should be removed.
                .field(newInputObjectField()
                        .deprecate("totalCapacity is not updated directly, use ParkingProperties>Spaces>noOfSpaces")
                        .name(TOTAL_CAPACITY)
                        .type(GraphQLBigInteger))
                .field(newInputObjectField()
                        .name(PARKING_TYPE)
                        .type(parkingTypeEnum))
                .field(newInputObjectField()
                        .name(PARKING_VEHICLE_TYPES)
                        .type(new GraphQLList(parkingVehicleEnum)))
                .field(newInputObjectField()
                        .name(PARKING_LAYOUT)
                        .type(parkingLayoutEnum))
                .field(newInputObjectField()
                        .name(PRINCIPAL_CAPACITY)
                        .type(GraphQLBigInteger))
                .field(newInputObjectField()
                        .name(OVERNIGHT_PARKING_PERMITTED)
                        .type(GraphQLBoolean))
                .field(newInputObjectField()
                        .name(RECHARGING_AVAILABLE)
                        .type(GraphQLBoolean))
                .field(newInputObjectField()
                        .name(SECURE)
                        .type(GraphQLBoolean))
                .field(newInputObjectField()
                        .name(REAL_TIME_OCCUPANCY_AVAILABLE)
                        .type(GraphQLBoolean))
                .field(newInputObjectField()
                        .name(PARKING_RESERVATION)
                        .type(parkingReservationEnum))
                .field(newInputObjectField()
                        .name(BOOKING_URL)
                        .type(GraphQLString))
                .field(newInputObjectField()
                        .name(FREE_PARKING_OUT_OF_HOURS)
                        .type(GraphQLBoolean))
                .field(newInputObjectField()
                        .name(PARKING_PAYMENT_PROCESS)
                        .type(new GraphQLList(parkingPaymentProcessEnum)))
                .field(newInputObjectField()
                        .name(PARKING_PROPERTIES)
                        .type(new GraphQLList(parkingPropertiesInputObjectType)))
                .field(newInputObjectField()
                        .name(PARKING_AREAS)
                        .type(new GraphQLList(parkingAreaInputObjectType)))
                .field(newInputObjectField()
                        .name(GEOMETRY)
                        .type(geoJsonInputType))
                .field(newInputObjectField()
                        .name(VALID_BETWEEN)
                        .type(validBetweenInputObjectType))
                .build();
    }
}
