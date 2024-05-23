package org.rutebanken.tiamat.rest.graphql.mappers;

import org.rutebanken.tiamat.model.hsl.GuidanceTypeEnumeration;
import org.rutebanken.tiamat.model.hsl.HslAccessibilityProperties;
import org.rutebanken.tiamat.model.hsl.HslStopTypeEnumeration;
import org.rutebanken.tiamat.model.hsl.MapTypeEnumeration;
import org.rutebanken.tiamat.model.hsl.PedestrianCrossingRampTypeEnumeration;
import org.rutebanken.tiamat.model.hsl.ShelterWidthTypeEnumeration;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.CURB_BACK_OF_RAIL_DISTANCE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.CURB_DRIVE_SIDE_OF_RAIL_DISTANCE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.CURVED_STOP;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.END_RAMP_SLOPE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.GUIDANCE_TILES;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.GUIDANCE_STRIPE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.GUIDANCE_TYPE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.LOWER_CLEAT_HEIGHT;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.MAP_TYPE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PEDESTRIAN_CROSSING_RAMP_TYPE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PLATFORM_EDGE_WARNING_AREA;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SERVICE_AREA_LENGTH;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SERVICE_AREA_STRIPES;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SERVICE_AREA_WIDTH;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SHELTER_LANE_DISTANCE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SHELTER_TYPE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SIDEWALK_ACCESSIBLE_CONNECTION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.STOP_AREA_LENGTHWISE_SLOPE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.STOP_AREA_SIDE_SLOPE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.STOP_AREA_SURROUNDINGS_ACCESSIBLE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.STOP_ELEVATION_FROM_RAIL_TOP;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.STOP_ELEVATION_FROM_SIDEWALK;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.STOP_TYPE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.STRUCTURE_LANE_DISTANCE;

@Component
public class HslAccessibilityPropertiesMapper {
    public HslAccessibilityProperties mapHslAccessibilityProperties(Map inputMap) {
        HslAccessibilityProperties hslAccessibilityProperties = new HslAccessibilityProperties();

        if (inputMap.containsKey(STOP_AREA_SIDE_SLOPE)) {
            hslAccessibilityProperties.setStopAreaSideSlope((Double) inputMap.get(STOP_AREA_SIDE_SLOPE));
        }
        if (inputMap.containsKey(STOP_AREA_LENGTHWISE_SLOPE)) {
            hslAccessibilityProperties.setStopAreaLengthwiseSlope((Double) inputMap.get(STOP_AREA_LENGTHWISE_SLOPE));
        }
        if (inputMap.containsKey(END_RAMP_SLOPE)) {
            hslAccessibilityProperties.setEndRampSlope((Double) inputMap.get(END_RAMP_SLOPE));
        }
        if (inputMap.containsKey(SHELTER_LANE_DISTANCE)) {
            hslAccessibilityProperties.setShelterLaneDistance((Double) inputMap.get(SHELTER_LANE_DISTANCE));
        }
        if (inputMap.containsKey(CURB_BACK_OF_RAIL_DISTANCE)) {
            hslAccessibilityProperties.setCurbBackOfRailDistance((Double) inputMap.get(CURB_BACK_OF_RAIL_DISTANCE));
        }
        if (inputMap.containsKey(CURB_DRIVE_SIDE_OF_RAIL_DISTANCE)) {
            hslAccessibilityProperties.setCurbDriveSideOfRailDistance((Double) inputMap.get(CURB_DRIVE_SIDE_OF_RAIL_DISTANCE));
        }
        if (inputMap.containsKey(STRUCTURE_LANE_DISTANCE)) {
            hslAccessibilityProperties.setStructureLaneDistance((Double) inputMap.get(STRUCTURE_LANE_DISTANCE));
        }
        if (inputMap.containsKey(STOP_ELEVATION_FROM_RAIL_TOP)) {
            hslAccessibilityProperties.setStopElevationFromRailTop((Double) inputMap.get(STOP_ELEVATION_FROM_RAIL_TOP));
        }
        if (inputMap.containsKey(STOP_ELEVATION_FROM_SIDEWALK)) {
            hslAccessibilityProperties.setStopElevationFromSidewalk((Double) inputMap.get(STOP_ELEVATION_FROM_SIDEWALK));
        }
        if (inputMap.containsKey(LOWER_CLEAT_HEIGHT)) {
            hslAccessibilityProperties.setLowerCleatHeight((Double) inputMap.get(LOWER_CLEAT_HEIGHT));
        }
        if (inputMap.containsKey(SERVICE_AREA_WIDTH)) {
            hslAccessibilityProperties.setServiceAreaWidth((Double) inputMap.get(SERVICE_AREA_WIDTH));
        }
        if (inputMap.containsKey(SERVICE_AREA_LENGTH)) {
            hslAccessibilityProperties.setServiceAreaLength((Double) inputMap.get(SERVICE_AREA_LENGTH));
        }
        if (inputMap.containsKey(PLATFORM_EDGE_WARNING_AREA)) {
            hslAccessibilityProperties.setPlatformEdgeWarningArea((Boolean) inputMap.get(PLATFORM_EDGE_WARNING_AREA));
        }
        if (inputMap.containsKey(GUIDANCE_TILES)) {
            hslAccessibilityProperties.setGuidanceTiles((Boolean) inputMap.get(GUIDANCE_TILES));
        }
        if (inputMap.containsKey(GUIDANCE_STRIPE)) {
            hslAccessibilityProperties.setGuidanceStripe((Boolean) inputMap.get(GUIDANCE_STRIPE));
        }
        if (inputMap.containsKey(SERVICE_AREA_STRIPES)) {
            hslAccessibilityProperties.setServiceAreaStripes((Boolean) inputMap.get(SERVICE_AREA_STRIPES));
        }
        if (inputMap.containsKey(SIDEWALK_ACCESSIBLE_CONNECTION)) {
            hslAccessibilityProperties.setSidewalkAccessibleConnection((Boolean) inputMap.get(SIDEWALK_ACCESSIBLE_CONNECTION));
        }
        if (inputMap.containsKey(STOP_AREA_SURROUNDINGS_ACCESSIBLE)) {
            hslAccessibilityProperties.setStopAreaSurroundingsAccessible((Boolean) inputMap.get(STOP_AREA_SURROUNDINGS_ACCESSIBLE));
        }
        if (inputMap.containsKey(CURVED_STOP)) {
            hslAccessibilityProperties.setCurvedStop((Boolean) inputMap.get(CURVED_STOP));
        }
        if (inputMap.containsKey(STOP_TYPE)) {
            hslAccessibilityProperties.setStopType((HslStopTypeEnumeration) inputMap.get(STOP_TYPE));
        }
        if (inputMap.containsKey(SHELTER_TYPE)) {
            hslAccessibilityProperties.setShelterType((ShelterWidthTypeEnumeration) inputMap.get(SHELTER_TYPE));
        }
        if (inputMap.containsKey(GUIDANCE_TYPE)) {
            hslAccessibilityProperties.setGuidanceType((GuidanceTypeEnumeration) inputMap.get(GUIDANCE_TYPE));
        }
        if (inputMap.containsKey(MAP_TYPE)) {
            hslAccessibilityProperties.setMapType((MapTypeEnumeration) inputMap.get(MAP_TYPE));
        }
        if (inputMap.containsKey(PEDESTRIAN_CROSSING_RAMP_TYPE)) {
            hslAccessibilityProperties.setPedestrianCrossingRampType((PedestrianCrossingRampTypeEnumeration) inputMap.get(PEDESTRIAN_CROSSING_RAMP_TYPE));
        }

        return hslAccessibilityProperties;
    }

}
