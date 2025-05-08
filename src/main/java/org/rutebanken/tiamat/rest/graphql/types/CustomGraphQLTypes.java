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
import org.rutebanken.tiamat.model.BoardingPositionTypeEnumeration;
import org.rutebanken.tiamat.model.BusSubmodeEnumeration;
import org.rutebanken.tiamat.model.CycleStorageEnumeration;
import org.rutebanken.tiamat.model.FunicularSubmodeEnumeration;
import org.rutebanken.tiamat.model.GenderLimitationEnumeration;
import org.rutebanken.tiamat.model.OrganisationTypeEnumeration;
import org.rutebanken.tiamat.model.StopPlaceOrganisationRelationshipEnumeration;
import org.rutebanken.tiamat.model.hsl.AccessibilityLevelEnumeration;
import org.rutebanken.tiamat.model.hsl.ElectricityTypeEnumeration;
import org.rutebanken.tiamat.model.hsl.GuidanceTypeEnumeration;
import org.rutebanken.tiamat.model.hsl.ShelterTypeEnumeration;
import org.rutebanken.tiamat.model.hsl.HslStopTypeEnumeration;
import org.rutebanken.tiamat.model.hsl.MapTypeEnumeration;
import org.rutebanken.tiamat.model.hsl.PedestrianCrossingRampTypeEnumeration;
import org.rutebanken.tiamat.model.hsl.ShelterConditionEnumeration;
import org.rutebanken.tiamat.model.hsl.ShelterWidthTypeEnumeration;
import org.rutebanken.tiamat.model.InstalledEquipment_VersionStructure;
import org.rutebanken.tiamat.model.InterchangeWeightingEnumeration;
import org.rutebanken.tiamat.model.LimitationStatusEnumeration;
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
import static graphql.Scalars.GraphQLFloat;
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
    public static GraphQLEnumType hslStopTypeEnum = createCustomEnumType(STOP_TYPE, HslStopTypeEnumeration.class);
    public static GraphQLEnumType shelterWidthTypeEnum = createCustomEnumType(SHELTER_WIDTH_TYPE, ShelterWidthTypeEnumeration.class);
    public static GraphQLEnumType guidanceTypeEnum = createCustomEnumType(GUIDANCE_TYPE, GuidanceTypeEnumeration.class);
    public static GraphQLEnumType mapTypeEnum = createCustomEnumType(MAP_TYPE, MapTypeEnumeration.class);
    public static GraphQLEnumType pedestrianCrossingRampTypeEnum = createCustomEnumType(PEDESTRIAN_CROSSING_RAMP_TYPE, PedestrianCrossingRampTypeEnumeration.class);
    public static GraphQLEnumType accessibilityLevelEnum = createCustomEnumType(ACCESSIBILITY_LEVEL, AccessibilityLevelEnumeration.class);
    public static GraphQLEnumType shelterTypeEnum = createCustomEnumType(SHELTER_TYPE, ShelterTypeEnumeration.class);
    public static GraphQLEnumType electricityTypeEnum = createCustomEnumType(SHELTER_ELECTRICITY, ElectricityTypeEnumeration.class);
    public static GraphQLEnumType shelterConditionTypeEnum = createCustomEnumType(SHELTER_CONDITION, ShelterConditionEnumeration.class);
    public static GraphQLEnumType organisationTypeEnum = createCustomEnumType(ORGANISATION_TYPE, OrganisationTypeEnumeration.class);
    public static GraphQLEnumType stopPlaceOrganisationRelationshipTypeEnum = createCustomEnumType(STOP_PLACE_ORGANISATION_RELATIONSHIP_TYPE, StopPlaceOrganisationRelationshipEnumeration.class);


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
                    .name(VERSION)
                    .type(GraphQLString))
            .field(newFieldDefinition()
                    .name(SEATS)
                    .type(GraphQLBigInteger))
            .field(newFieldDefinition()
                    .name(STEP_FREE)
                    .type(GraphQLBoolean))
            .field(newFieldDefinition()
                    .name(ENCLOSED)
                    .type(GraphQLBoolean))
            .field(newFieldDefinition()
                    .name(SHELTER_TYPE)
                    .description("Katoksen tyyppi: Lasikatos (glass) / Teräskatos (steel) / Tolppa (post) / Urbaanikatos (urban) / Betonikatos (concrete) / Puukatos (wooden) / Virtuaali (virtual)")
                    .type(shelterTypeEnum))
            .field(newFieldDefinition()
                    .name(SHELTER_ELECTRICITY)
                    .description("Katoksen sähköt: Jatkuva sähkö (continuous) / Valosähkö (light) / Jatkuva rakenteilla (continuousUnderConstruction) / Jatkuva suunniteltu (continuousPlanned) / Tilapäisesti pois (temporarilyOff) / Ei sähköä (none)")
                    .type(electricityTypeEnum))
            .field(newFieldDefinition()
                    .name(SHELTER_LIGHTING)
                    .description("Katoksessa valot")
                    .type(GraphQLBoolean))
            .field(newFieldDefinition()
                    .name(SHELTER_CONDITION)
                    .description("Katoksen kunto: Hyvä (good), Välttävä (mediocre), Huono (bad)")
                    .type(shelterConditionTypeEnum))
            .field(newFieldDefinition()
                    .name(TIMETABLE_CABINETS)
                    .description("Aikataulukaappien lukumäärä")
                    .type(GraphQLInt))
            .field(newFieldDefinition()
                    .name(TRASH_CAN)
                    .description("Katoksessa roska-astia")
                    .type(GraphQLBoolean))
            .field(newFieldDefinition()
                    .name(SHELTER_HAS_DISPLAY)
                    .description("Katoksesssa näyttö")
                    .type(GraphQLBoolean))
            .field(newFieldDefinition()
                    .name(BICYCLE_PARKING)
                    .description("Pyöräpysäköinti")
                    .type(GraphQLBoolean))
            .field(newFieldDefinition()
                    .name(LEANING_RAIL)
                    .description("Nojailutanko")
                    .type(GraphQLBoolean))
            .field(newFieldDefinition()
                    .name(OUTSIDE_BENCH)
                    .description("Ulkopenkki")
                    .type(GraphQLBoolean))
            .field(newFieldDefinition()
                    .name(SHELTER_FASCIA_BOARD_TAPING)
                    .description("Pysäkkikatoksen otsalaudan teippaus")
                    .type(GraphQLBoolean))
            .field(newFieldDefinition()
                    .name(SHELTER_NUMBER)
                    .description("Pysäkkikatoksen numero")
                    .type(GraphQLInt))
            .field(newFieldDefinition()
                    .name(SHELTER_EXTERNAL_ID)
                    .description("Pysäkkikatoksen varustenumero")
                    .type(GraphQLString))
            .build();

    public static GraphQLInputObjectType shelterEquipmentInputType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_SHELTER_EQUIPMENT)
            .field(newInputObjectField()
                    .name(ID)
                    .type(GraphQLString))
            .field(newInputObjectField()
                    .name(SEATS)
                    .type(GraphQLBigInteger))
            .field(newInputObjectField()
                    .name(STEP_FREE)
                    .type(GraphQLBoolean))
            .field(newInputObjectField()
                    .name(ENCLOSED)
                    .type(GraphQLBoolean))
            .field(newInputObjectField()
                    .name(SHELTER_TYPE)
                    .description("Katoksen tyyppi: Lasikatos (glass) / Teräskatos (steel) / Tolppa (post) / Urbaanikatos (urban) / Betonikatos (concrete) / Puukatos (wooden) / Virtuaali (virtual)")
                    .type(shelterTypeEnum))
            .field(newInputObjectField()
                    .name(SHELTER_ELECTRICITY)
                    .description("Katoksen sähköt: Jatkuva sähkö (continuous) / Valosähkö (light) / Jatkuva rakenteilla (continuousUnderConstruction) / Jatkuva suunniteltu (continuousPlanned) / Tilapäisesti pois (temporarilyOff) / Ei sähköä (none)")
                    .type(electricityTypeEnum))
            .field(newInputObjectField()
                    .name(SHELTER_LIGHTING)
                    .description("Katoksessa valot")
                    .type(GraphQLBoolean))
            .field(newInputObjectField()
                    .name(SHELTER_CONDITION)
                    .description("Katoksen kunto: Hyvä (good), Välttävä (mediocre), Huono (bad)")
                    .type(shelterConditionTypeEnum))
            .field(newInputObjectField()
                    .name(TIMETABLE_CABINETS)
                    .description("Aikataulukaappien lukumäärä")
                    .type(GraphQLInt))
            .field(newInputObjectField()
                    .name(TRASH_CAN)
                    .description("Katoksessa roska-astia")
                    .type(GraphQLBoolean))
            .field(newInputObjectField()
                    .name(SHELTER_HAS_DISPLAY)
                    .description("Katoksesssa näyttö")
                    .type(GraphQLBoolean))
            .field(newInputObjectField()
                    .name(BICYCLE_PARKING)
                    .description("Pyöräpysäköinti")
                    .type(GraphQLBoolean))
            .field(newInputObjectField()
                    .name(LEANING_RAIL)
                    .description("Nojailutanko")
                    .type(GraphQLBoolean))
            .field(newInputObjectField()
                    .name(OUTSIDE_BENCH)
                    .description("Ulkopenkki")
                    .type(GraphQLBoolean))
            .field(newInputObjectField()
                    .name(SHELTER_FASCIA_BOARD_TAPING)
                    .description("Pysäkkikatoksen otsalaudan teippaus")
                    .type(GraphQLBoolean))
            .field(newInputObjectField()
                    .name(SHELTER_NUMBER)
                    .description("Pysäkkikatoksen numero")
                    .type(GraphQLInt))
            .field(newInputObjectField()
                    .name(SHELTER_EXTERNAL_ID)
                    .description("Pysäkkikatoksen varustenumero")
                    .type(GraphQLString))
            .build();

    public static GraphQLObjectType ticketingEquipmentType = newObject()
            .name(OUTPUT_TYPE_TICKETING_EQUIPMENT)
            .field(netexIdFieldDefinition)
            .field(newFieldDefinition()
                    .name(VERSION)
                    .type(GraphQLString))
            .field(newFieldDefinition()
                    .name(TICKET_OFFICE)
                    .type(GraphQLBoolean))
            .field(newFieldDefinition()
                    .name(TICKET_MACHINES)
                    .type(GraphQLBoolean))
            .field(newFieldDefinition()
                    .name(NUMBER_OF_MACHINES)
                    .type(GraphQLBigInteger))
            .build();


    public static GraphQLInputObjectType ticketingEquipmentInputType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_TICKETING_EQUIPMENT)
            .field(newInputObjectField()
                    .name(ID)
                    .type(GraphQLString))
            .field(newInputObjectField()
                    .name(TICKET_OFFICE)
                    .type(GraphQLBoolean))
            .field(newInputObjectField()
                    .name(TICKET_MACHINES)
                    .type(GraphQLBoolean))
            .field(newInputObjectField()
                    .name(NUMBER_OF_MACHINES)
                    .type(GraphQLBigInteger))
            .build();

    public static GraphQLObjectType cycleStorageEquipmentType = newObject()
            .name(OUTPUT_TYPE_CYCLE_STORAGE_EQUIPMENT)
            .field(netexIdFieldDefinition)
            .field(newFieldDefinition()
                    .name(VERSION)
                    .type(GraphQLString))
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
                    .name(ID)
                    .type(GraphQLString))
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
            .field(newFieldDefinition()
                    .name(VERSION)
                    .type(GraphQLString))
            .field(privateCodeFieldDefinition)
            .field(newFieldDefinition()
                    .name(CONTENT)
                    .type(embeddableMultilingualStringObjectType))
            .field(newFieldDefinition()
                    .name(SIGN_CONTENT_TYPE)
                    .type(signContentTypeEnum))
            .field(newFieldDefinition()
                    .name(NOTE)
                    .type(embeddableMultilingualStringObjectType))
            .field(newFieldDefinition()
                    .name(NUMBER_OF_FRAMES)
                    .type(GraphQLInt))
            .field(newFieldDefinition()
                    .name(LINE_SIGNAGE)
                    .type(GraphQLBoolean))
            .field(newFieldDefinition()
                    .name(MAIN_LINE_SIGN)
                    .type(GraphQLBoolean))
            .field(newFieldDefinition()
                    .name(REPLACES_RAIL_SIGN)
                    .type(GraphQLBoolean))
            .build();


    public static GraphQLInputObjectType generalSignEquipmentInputType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_GENERAL_SIGN_EQUIPMENT)
            .field(newInputObjectField()
                    .name(ID)
                    .type(GraphQLString))
            .field(newInputObjectField()
                    .name(PRIVATE_CODE)
                    .type(privateCodeInputType))
            .field(newInputObjectField()
                    .name(CONTENT)
                    .type(embeddableMultiLingualStringInputObjectType))
            .field(newInputObjectField()
                    .name(SIGN_CONTENT_TYPE)
                    .type(signContentTypeEnum))
            .field(newInputObjectField()
                    .name(NOTE)
                    .type(embeddableMultiLingualStringInputObjectType))
            .field(newInputObjectField()
                    .name(NUMBER_OF_FRAMES)
                    .type(GraphQLInt))
            .field(newInputObjectField()
                    .name(LINE_SIGNAGE)
                    .defaultValue(Boolean.FALSE)
                    .type(GraphQLBoolean))
            .field(newInputObjectField()
                    .name(MAIN_LINE_SIGN)
                    .defaultValue(Boolean.FALSE)
                    .type(GraphQLBoolean))
            .field(newInputObjectField()
                    .name(REPLACES_RAIL_SIGN)
                    .defaultValue(Boolean.FALSE)
                    .type(GraphQLBoolean))
            .build();

    public static GraphQLObjectType waitingRoomEquipmentType = newObject()
            .name(OUTPUT_TYPE_WAITING_ROOM_EQUIPMENT)
            .field(netexIdFieldDefinition)
            .field(newFieldDefinition()
                    .name(VERSION)
                    .type(GraphQLString))
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
                    .name(ID)
                    .type(GraphQLString))
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
                    .name(VERSION)
                    .type(GraphQLString))
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
                    .name(ID)
                    .type(GraphQLString))
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
            .build();

    public static GraphQLObjectType hslAccessibilityPropertiesObjectType = newObject()
            .name(OUTPUT_TYPE_HSL_ACCESSIBILITY_PROPERTIES)
            .field(netexIdFieldDefinition)
            .field(newFieldDefinition()
                    .name(VERSION)
                    .type(GraphQLString))
            .field(newFieldDefinition()
                    .name(STOP_AREA_SIDE_SLOPE)
                    .description("Pysäkkialueen sivukaltevuus (%)")
                    .type(GraphQLFloat))
            .field(newFieldDefinition()
                    .name(STOP_AREA_LENGTHWISE_SLOPE)
                    .description("Pysäkkialueen pituuskaltevuus (%)")
                    .type(GraphQLFloat))
            .field(newFieldDefinition()
                    .name(END_RAMP_SLOPE)
                    .description("Päätyluiskan kaltevuus (%)")
                    .type(GraphQLFloat))
            .field(newFieldDefinition()
                    .name(SHELTER_LANE_DISTANCE)
                    .description("Katoksen ja ajoradan välinen leveys (cm)")
                    .type(GraphQLFloat))
            .field(newFieldDefinition()
                    .name(CURB_BACK_OF_RAIL_DISTANCE)
                    .description("Reunakiven etäisyys kiskon selästä (cm)")
                    .type(GraphQLFloat))
            .field(newFieldDefinition()
                    .name(CURB_DRIVE_SIDE_OF_RAIL_DISTANCE)
                    .description("Reunakiven etäisyys kiskon ajoreunasta (cm)")
                    .type(GraphQLFloat))
            .field(newFieldDefinition()
                    .name(STRUCTURE_LANE_DISTANCE)
                    .description("Rakenteiden ja ajoradan välinen pienin leveys (cm)")
                    .type(GraphQLFloat))
            .field(newFieldDefinition()
                    .name(STOP_ELEVATION_FROM_RAIL_TOP)
                    .description("Pysäkin korotus kiskon ajopintaan nähden (cm)")
                    .type(GraphQLFloat))
            .field(newFieldDefinition()
                    .name(STOP_ELEVATION_FROM_SIDEWALK)
                    .description("Pysäkin korotus jalkakäytävään nähden (cm)")
                    .type(GraphQLFloat))
            .field(newFieldDefinition()
                    .name(LOWER_CLEAT_HEIGHT)
                    .description("Alapienan korkeus (cm)")
                    .type(GraphQLFloat))
            .field(newFieldDefinition()
                    .name(SERVICE_AREA_WIDTH)
                    .description("Palvelualueen leveys (m)")
                    .type(GraphQLFloat))
            .field(newFieldDefinition()
                    .name(SERVICE_AREA_LENGTH)
                    .description("Palvelualueen pituus (m)")
                    .type(GraphQLFloat))
            .field(newFieldDefinition()
                    .name(PLATFORM_EDGE_WARNING_AREA)
                    .description("Pysäkkialueen varoitusalue")
                    .type(GraphQLBoolean))
            .field(newFieldDefinition()
                    .name(GUIDANCE_TILES)
                    .description("Opaslaatat")
                    .type(GraphQLBoolean))
            .field(newFieldDefinition()
                    .name(GUIDANCE_STRIPE)
                    .description("Opasteraita")
                    .type(GraphQLBoolean))
            .field(newFieldDefinition()
                    .name(SERVICE_AREA_STRIPES)
                    .description("Palvelualueen raidoitus")
                    .type(GraphQLBoolean))
            .field(newFieldDefinition()
                    .name(SIDEWALK_ACCESSIBLE_CONNECTION)
                    .description("Esteetön yhteys jalkakäytävältä pysäkille")
                    .type(GraphQLBoolean))
            .field(newFieldDefinition()
                    .name(STOP_AREA_SURROUNDINGS_ACCESSIBLE)
                    .description("Pysäkin ympäristo: Esteellinen / Esteetön")
                    .type(GraphQLBoolean))
            .field(newFieldDefinition()
                    .name(CURVED_STOP)
                    .description("Kaareva pysäkki")
                    .type(GraphQLBoolean))
            .field(newFieldDefinition()
                    .name(STOP_TYPE)
                    .description("Pysäkin tyyppi: Syvennys (pullOut) / Uloke (busBulb) / Ajoradalla (inLane) / Muu (other)")
                    .type(hslStopTypeEnum))
            .field(newFieldDefinition()
                    .name(SHELTER_TYPE)
                    .description("Katoksen tyyppi: Leveä (wide) / Kapea (narrow) / Muu (other)")
                    .type(shelterWidthTypeEnum))
            .field(newFieldDefinition()
                    .name(GUIDANCE_TYPE)
                    .description("Opasteiden tyyppi: Pisteopaste (braille) / Ei opastetta (none) / Muu opastus (other)")
                    .type(guidanceTypeEnum))
            .field(newFieldDefinition()
                    .name(MAP_TYPE)
                    .description("Kartan tyyppi: Kohokartta (tactile) / Ei karttaa (none) / Muu kartta (other)")
                    .type(mapTypeEnum))
            .field(newFieldDefinition()
                    .name(PEDESTRIAN_CROSSING_RAMP_TYPE)
                    .description("Suojatien luiskaus")
                    .type(pedestrianCrossingRampTypeEnum))
            .field(newFieldDefinition()
                    .name(ACCESSIBILITY_LEVEL)
                    .description("Esteettömyystaso: Täysin esteetön (fullyAccessible) / Vähäisiä esteitä (mostlyAccessible) / Osittain esteellinen (partiallyInaccessible) / Esteellinen (inaccessible) / Esteettömyystietoja puuttuu (unknown)")
                    .type(accessibilityLevelEnum))
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
            .field(newFieldDefinition()
                    .name(HSL_ACCESSIBILITY_PROPERTIES)
                    .type(hslAccessibilityPropertiesObjectType))
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
            .build();

    public static GraphQLInputObjectType hslAccessibilityPropertiesInputObjectType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_HSL_ACCESSIBILITY_PROPERTIES)
            .field(newInputObjectField()
                    .name(ID)
                    .type(GraphQLString))
            .field(newInputObjectField()
                    .name(STOP_AREA_SIDE_SLOPE)
                    .type(GraphQLFloat))
            .field(newInputObjectField()
                    .name(STOP_AREA_LENGTHWISE_SLOPE)
                    .type(GraphQLFloat))
            .field(newInputObjectField()
                    .name(END_RAMP_SLOPE)
                    .type(GraphQLFloat))
            .field(newInputObjectField()
                    .name(SHELTER_LANE_DISTANCE)
                    .type(GraphQLFloat))
            .field(newInputObjectField()
                    .name(CURB_BACK_OF_RAIL_DISTANCE)
                    .type(GraphQLFloat))
            .field(newInputObjectField()
                    .name(CURB_DRIVE_SIDE_OF_RAIL_DISTANCE)
                    .type(GraphQLFloat))
            .field(newInputObjectField()
                    .name(STRUCTURE_LANE_DISTANCE)
                    .type(GraphQLFloat))
            .field(newInputObjectField()
                    .name(STOP_ELEVATION_FROM_RAIL_TOP)
                    .type(GraphQLFloat))
            .field(newInputObjectField()
                    .name(STOP_ELEVATION_FROM_SIDEWALK)
                    .type(GraphQLFloat))
            .field(newInputObjectField()
                    .name(LOWER_CLEAT_HEIGHT)
                    .type(GraphQLFloat))
            .field(newInputObjectField()
                    .name(SERVICE_AREA_WIDTH)
                    .type(GraphQLFloat))
            .field(newInputObjectField()
                    .name(SERVICE_AREA_LENGTH)
                    .type(GraphQLFloat))
            .field(newInputObjectField()
                    .name(PLATFORM_EDGE_WARNING_AREA)
                    .type(GraphQLBoolean))
            .field(newInputObjectField()
                    .name(GUIDANCE_TILES)
                    .type(GraphQLBoolean))
            .field(newInputObjectField()
                    .name(GUIDANCE_STRIPE)
                    .type(GraphQLBoolean))
            .field(newInputObjectField()
                    .name(SERVICE_AREA_STRIPES)
                    .type(GraphQLBoolean))
            .field(newInputObjectField()
                    .name(SIDEWALK_ACCESSIBLE_CONNECTION)
                    .type(GraphQLBoolean))
            .field(newInputObjectField()
                    .name(STOP_AREA_SURROUNDINGS_ACCESSIBLE)
                    .type(GraphQLBoolean))
            .field(newInputObjectField()
                    .name(CURVED_STOP)
                    .type(GraphQLBoolean))
            .field(newInputObjectField()
                    .name(STOP_TYPE)
                    .type(hslStopTypeEnum))
            .field(newInputObjectField()
                    .name(SHELTER_TYPE)
                    .type(shelterWidthTypeEnum))
            .field(newInputObjectField()
                    .name(GUIDANCE_TYPE)
                    .type(guidanceTypeEnum))
            .field(newInputObjectField()
                    .name(MAP_TYPE)
                    .type(mapTypeEnum))
            .field(newInputObjectField()
                    .name(PEDESTRIAN_CROSSING_RAMP_TYPE)
                    .type(pedestrianCrossingRampTypeEnum))
            .field(newInputObjectField()
                    .name(ACCESSIBILITY_LEVEL)
                    .type(accessibilityLevelEnum))
            .build();

    public static GraphQLInputObjectType accessibilityAssessmentInputObjectType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_ACCESSIBILITY_ASSESSMENT)
            .field(newInputObjectField()
                    .name(ID)
                    .type(GraphQLString))
            .field(newInputObjectField()
                    .name(LIMITATIONS)
                    .type(accessibilityLimitationsInputObjectType))
            .field(newInputObjectField()
                    .name(HSL_ACCESSIBILITY_PROPERTIES)
                    .type(hslAccessibilityPropertiesInputObjectType))
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

    public static GraphQLInputObjectType stopPlaceOrganisationRefInputObjectType = GraphQLInputObjectType.newInputObject()
            .name(INPUT_TYPE_STOP_PLACE_ORGANISATION_REF)
            .field(newInputObjectField()
                    .name(ORGANISATION_REF)
                    .type(new GraphQLNonNull(GraphQLString))
                    .description("Id of the referenced organisation"))
            .field(newInputObjectField()
                    .name(RELATIONSHIP_TYPE)
                    .type(stopPlaceOrganisationRelationshipTypeEnum))
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

    public static GraphQLObjectType createOrganisationObjectType(GraphQLObjectType validBetweenObjectType) {
        GraphQLObjectType contactObjectType = newObject()
                .name(OUTPUT_TYPE_CONTACT)
                .field(netexIdFieldDefinition)
                .field(newFieldDefinition()
                        .name(VERSION)
                        .type(GraphQLString))
                .field(newFieldDefinition()
                        .name(CONTACT_PERSON)
                        .type(GraphQLString))
                .field(newFieldDefinition()
                        .name(EMAIL)
                        .type(GraphQLString))
                .field(newFieldDefinition()
                        .name(PHONE)
                        .type(GraphQLString))
                .field(newFieldDefinition()
                        .name(FAX)
                        .type(GraphQLString))
                .field(newFieldDefinition()
                        .name(URL)
                        .type(GraphQLString))
                .field(newFieldDefinition()
                        .name(FURTHER_DETAILS)
                        .type(GraphQLString))
                .build();

        return newObject()
                .name(OUTPUT_TYPE_ORGANISATION)
                .field(netexIdFieldDefinition)
                .field(newFieldDefinition()
                        .name(VERSION)
                        .type(GraphQLString))
                .field(newFieldDefinition()
                        .name(VALID_BETWEEN)
                        .type(validBetweenObjectType))
                .field(newFieldDefinition()
                        .name(PRIVATE_CODE)
                        .type(GraphQLString))
                .field(newFieldDefinition()
                        .name(COMPANY_NUMBER)
                        .type(GraphQLString))
                .field(newFieldDefinition()
                        .name(NAME)
                        .type(GraphQLString))
                .field(newFieldDefinition()
                        .name(ORGANISATION_TYPE)
                        .type(organisationTypeEnum))
                .field(newFieldDefinition()
                        .name(LEGAL_NAME)
                        .type(embeddableMultilingualStringObjectType))
                .field(newFieldDefinition()
                        .name(CONTACT_DETAILS)
                        .type(contactObjectType))
                .field(newFieldDefinition()
                        .name(PRIVATE_CONTACT_DETAILS)
                        .type(contactObjectType))
                .build();
    }

    public static GraphQLInputObjectType createOrganisationInputObjectType(GraphQLInputObjectType validBetweenInputObjectType) {
        GraphQLInputObjectType contactInputObjectType = GraphQLInputObjectType.newInputObject()
                .name(INPUT_TYPE_CONTACT)
                .field(newInputObjectField()
                        .name(ID)
                        .type(GraphQLString))
                .field(newInputObjectField()
                        .name(CONTACT_PERSON)
                        .type(GraphQLString))
                .field(newInputObjectField()
                        .name(EMAIL)
                        .type(GraphQLString))
                .field(newInputObjectField()
                        .name(PHONE)
                        .type(GraphQLString))
                .field(newInputObjectField()
                        .name(FAX)
                        .type(GraphQLString))
                .field(newInputObjectField()
                        .name(URL)
                        .type(GraphQLString))
                .field(newInputObjectField()
                        .name(FURTHER_DETAILS)
                        .type(GraphQLString))
                .build();

        return GraphQLInputObjectType.newInputObject()
                .name(INPUT_TYPE_ORGANISATION)
                .field(newInputObjectField()
                        .name(ID)
                        .type(GraphQLString))
                .field(newInputObjectField()
                        .name(VALID_BETWEEN)
                        .type(validBetweenInputObjectType))
                .field(newInputObjectField()
                        .name(PRIVATE_CODE)
                        .type(GraphQLString))
                .field(newInputObjectField()
                        .name(COMPANY_NUMBER)
                        .type(GraphQLString))
                .field(newInputObjectField()
                        .name(NAME)
                        .type(GraphQLString))
                .field(newInputObjectField()
                        .name(ORGANISATION_TYPE)
                        .type(organisationTypeEnum))
                .field(newInputObjectField()
                        .name(LEGAL_NAME)
                        .type(embeddableMultiLingualStringInputObjectType))
                .field(newInputObjectField()
                        .name(CONTACT_DETAILS)
                        .type(contactInputObjectType))
                .field(newInputObjectField()
                        .name(PRIVATE_CONTACT_DETAILS)
                        .type(contactInputObjectType))
                .build();
    }
}
