package org.rutebanken.tiamat.model.hsl;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.rutebanken.tiamat.model.VersionedChildStructure;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.Objects;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HslAccessibilityProperties extends VersionedChildStructure {
    protected Double stopAreaSideSlope;
    protected Double stopAreaLengthwiseSlope;
    protected Double endRampSlope;
    protected Double shelterLaneDistance;
    protected Double curbBackOfRailDistance;
    protected Double curbDriveSideOfRailDistance;
    protected Double structureLaneDistance;
    protected Double stopElevationFromRailTop;
    protected Double stopElevationFromSidewalk;
    protected Double lowerCleatHeight;
    protected Double serviceAreaWidth;
    protected Double serviceAreaLength;
    protected Boolean platformEdgeWarningArea;
    protected Boolean guidanceTiles;
    protected Boolean guidanceStripe;
    protected Boolean serviceAreaStripes;
    protected Boolean sidewalkAccessibleConnection;
    protected Boolean stopAreaSurroundingsAccessible;
    protected Boolean curvedStop;
    @Enumerated(EnumType.STRING)
    protected HslStopTypeEnumeration stopType;
    @Enumerated(EnumType.STRING)
    protected ShelterWidthTypeEnumeration shelterType;
    @Enumerated(EnumType.STRING)
    protected GuidanceTypeEnumeration guidanceType;
    @Enumerated(EnumType.STRING)
    protected MapTypeEnumeration mapType;
    @Enumerated(EnumType.STRING)
    protected PedestrianCrossingRampTypeEnumeration pedestrianCrossingRampType;
    @Enumerated(EnumType.STRING)
    protected AccessibilityLevelEnumeration accessibilityLevel = AccessibilityLevelEnumeration.UNKNOWN;

    public void copyPropertiesFrom(HslAccessibilityProperties base) {
        this.stopAreaSideSlope = base.stopAreaSideSlope;
        this.stopAreaLengthwiseSlope = base.stopAreaLengthwiseSlope;
        this.endRampSlope = base.endRampSlope;
        this.shelterLaneDistance = base.shelterLaneDistance;
        this.curbBackOfRailDistance = base.curbBackOfRailDistance;
        this.curbDriveSideOfRailDistance = base.curbDriveSideOfRailDistance;
        this.structureLaneDistance = base.structureLaneDistance;
        this.stopElevationFromRailTop = base.stopElevationFromRailTop;
        this.stopElevationFromSidewalk = base.stopElevationFromSidewalk;
        this.lowerCleatHeight = base.lowerCleatHeight;
        this.serviceAreaWidth = base.serviceAreaWidth;
        this.serviceAreaLength = base.serviceAreaLength;
        this.platformEdgeWarningArea = base.platformEdgeWarningArea;
        this.guidanceTiles = base.guidanceTiles;
        this.guidanceStripe = base.guidanceStripe;
        this.serviceAreaStripes = base.serviceAreaStripes;
        this.sidewalkAccessibleConnection = base.sidewalkAccessibleConnection;
        this.stopAreaSurroundingsAccessible = base.stopAreaSurroundingsAccessible;
        this.curvedStop = base.curvedStop;
        this.stopType = base.stopType;
        this.shelterType = base.shelterType;
        this.guidanceType = base.guidanceType;
        this.mapType = base.mapType;
        this.pedestrianCrossingRampType = base.pedestrianCrossingRampType;
        this.accessibilityLevel = base.accessibilityLevel;
    }

    public HslAccessibilityProperties copy() {
        HslAccessibilityProperties copy = new HslAccessibilityProperties();
        copy.copyPropertiesFrom(this);
        return copy;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof HslAccessibilityProperties)) {
            return false;
        }

        HslAccessibilityProperties other = (HslAccessibilityProperties) object;

        return Objects.equals(this.stopAreaSideSlope, other.stopAreaSideSlope)
                && Objects.equals(this.stopAreaLengthwiseSlope, other.stopAreaLengthwiseSlope)
                && Objects.equals(this.endRampSlope, other.endRampSlope)
                && Objects.equals(this.shelterLaneDistance, other.shelterLaneDistance)
                && Objects.equals(this.curbBackOfRailDistance, other.curbBackOfRailDistance)
                && Objects.equals(this.curbDriveSideOfRailDistance, other.curbDriveSideOfRailDistance)
                && Objects.equals(this.structureLaneDistance, other.structureLaneDistance)
                && Objects.equals(this.stopElevationFromRailTop, other.stopElevationFromRailTop)
                && Objects.equals(this.stopElevationFromSidewalk, other.stopElevationFromSidewalk)
                && Objects.equals(this.lowerCleatHeight, other.lowerCleatHeight)
                && Objects.equals(this.serviceAreaWidth, other.serviceAreaWidth)
                && Objects.equals(this.serviceAreaLength, other.serviceAreaLength)
                && Objects.equals(this.platformEdgeWarningArea, other.platformEdgeWarningArea)
                && Objects.equals(this.guidanceTiles, other.guidanceTiles)
                && Objects.equals(this.guidanceStripe, other.guidanceStripe)
                && Objects.equals(this.serviceAreaStripes, other.serviceAreaStripes)
                && Objects.equals(this.sidewalkAccessibleConnection, other.sidewalkAccessibleConnection)
                && Objects.equals(this.stopAreaSurroundingsAccessible, other.stopAreaSurroundingsAccessible)
                && Objects.equals(this.curvedStop, other.curvedStop)
                && Objects.equals(this.stopType, other.stopType)
                && Objects.equals(this.shelterType, other.shelterType)
                && Objects.equals(this.guidanceType, other.guidanceType)
                && Objects.equals(this.mapType, other.mapType)
                && Objects.equals(this.pedestrianCrossingRampType, other.pedestrianCrossingRampType)
                && Objects.equals(this.accessibilityLevel, other.accessibilityLevel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            stopAreaSideSlope, stopAreaLengthwiseSlope, endRampSlope, shelterLaneDistance, curbBackOfRailDistance,
            curbDriveSideOfRailDistance, structureLaneDistance, stopElevationFromRailTop, stopElevationFromSidewalk,
            lowerCleatHeight, serviceAreaWidth, serviceAreaLength, platformEdgeWarningArea,
            guidanceTiles, guidanceStripe, serviceAreaStripes, sidewalkAccessibleConnection,  stopAreaSurroundingsAccessible,
            curvedStop, stopType, shelterType, guidanceType, mapType, pedestrianCrossingRampType, accessibilityLevel
        );
    }

    public Double getStopAreaSideSlope() {
        return stopAreaSideSlope;
    }

    public void setStopAreaSideSlope(Double stopAreaSideSlope) {
        this.stopAreaSideSlope = stopAreaSideSlope;
    }

    public Double getStopAreaLengthwiseSlope() {
        return stopAreaLengthwiseSlope;
    }

    public void setStopAreaLengthwiseSlope(Double stopAreaLengthwiseSlope) {
        this.stopAreaLengthwiseSlope = stopAreaLengthwiseSlope;
    }

    public Double getEndRampSlope() {
        return endRampSlope;
    }

    public void setEndRampSlope(Double endRampSlope) {
        this.endRampSlope = endRampSlope;
    }

    public Double getShelterLaneDistance() {
        return shelterLaneDistance;
    }

    public void setShelterLaneDistance(Double shelterRoadwayDistance) {
        this.shelterLaneDistance = shelterRoadwayDistance;
    }

    public Double getCurbBackOfRailDistance() {
        return curbBackOfRailDistance;
    }

    public void setCurbBackOfRailDistance(Double curbBackOfRailDistance) {
        this.curbBackOfRailDistance = curbBackOfRailDistance;
    }

    public Double getCurbDriveSideOfRailDistance() {
        return curbDriveSideOfRailDistance;
    }

    public void setCurbDriveSideOfRailDistance(Double curbDriveSideOfRailDistance) {
        this.curbDriveSideOfRailDistance = curbDriveSideOfRailDistance;
    }

    public Double getStructureLaneDistance() {
        return structureLaneDistance;
    }

    public void setStructureLaneDistance(Double structureRoadwayDistance) {
        this.structureLaneDistance = structureRoadwayDistance;
    }

    public Double getStopElevationFromRailTop() {
        return stopElevationFromRailTop;
    }

    public void setStopElevationFromRailTop(Double stopElevationFromRailTop) {
        this.stopElevationFromRailTop = stopElevationFromRailTop;
    }

    public Double getStopElevationFromSidewalk() {
        return stopElevationFromSidewalk;
    }

    public void setStopElevationFromSidewalk(Double stopElevationFromSidewalk) {
        this.stopElevationFromSidewalk = stopElevationFromSidewalk;
    }

    public Double getLowerCleatHeight() {
        return lowerCleatHeight;
    }

    public void setLowerCleatHeight(Double lowerCleatHeight) {
        this.lowerCleatHeight = lowerCleatHeight;
    }

    public Double getServiceAreaWidth() {
        return serviceAreaWidth;
    }

    public void setServiceAreaWidth(Double serviceAreaWidth) {
        this.serviceAreaWidth = serviceAreaWidth;
    }

    public Double getServiceAreaLength() {
        return serviceAreaLength;
    }

    public void setServiceAreaLength(Double serviceAreaLength) {
        this.serviceAreaLength = serviceAreaLength;
    }

    public Boolean isPlatformEdgeWarningArea() {
        return platformEdgeWarningArea;
    }

    public void setPlatformEdgeWarningArea(Boolean platformEdgeWarningArea) {
        this.platformEdgeWarningArea = platformEdgeWarningArea;
    }

    public Boolean isGuidanceTiles() {
        return guidanceTiles;
    }

    public void setGuidanceTiles(Boolean guideTiles) {
        this.guidanceTiles = guideTiles;
    }

    public Boolean isGuidanceStripe() {
        return guidanceStripe;
    }

    public void setGuidanceStripe(Boolean guideStripe) {
        this.guidanceStripe = guideStripe;
    }

    public Boolean isServiceAreaStripes() {
        return serviceAreaStripes;
    }

    public void setServiceAreaStripes(Boolean serviceAreaStripes) {
        this.serviceAreaStripes = serviceAreaStripes;
    }

    public Boolean isSidewalkAccessibleConnection() {
        return sidewalkAccessibleConnection;
    }

    public void setSidewalkAccessibleConnection(Boolean sidewalkAccessibleConnection) {
        this.sidewalkAccessibleConnection = sidewalkAccessibleConnection;
    }

    public Boolean isStopAreaSurroundingsAccessible() {
        return stopAreaSurroundingsAccessible;
    }

    public void setStopAreaSurroundingsAccessible(Boolean stopAreaSurroundingsAccessible) {
        this.stopAreaSurroundingsAccessible = stopAreaSurroundingsAccessible;
    }

    public Boolean isCurvedStop() {
        return curvedStop;
    }

    public void setCurvedStop(Boolean curvedStop) {
        this.curvedStop = curvedStop;
    }

    public HslStopTypeEnumeration getStopType() {
        return stopType;
    }

    public void setStopType(HslStopTypeEnumeration stopType) {
        this.stopType = stopType;
    }

    public ShelterWidthTypeEnumeration getShelterType() {
        return shelterType;
    }

    public void setShelterType(ShelterWidthTypeEnumeration shelterType) {
        this.shelterType = shelterType;
    }

    public GuidanceTypeEnumeration getGuidanceType() {
        return guidanceType;
    }

    public void setGuidanceType(GuidanceTypeEnumeration guidanceType) {
        this.guidanceType = guidanceType;
    }

    public MapTypeEnumeration getMapType() {
        return mapType;
    }

    public void setMapType(MapTypeEnumeration mapType) {
        this.mapType = mapType;
    }

    public PedestrianCrossingRampTypeEnumeration getPedestrianCrossingRampType() {
        return pedestrianCrossingRampType;
    }

    public void setPedestrianCrossingRampType(PedestrianCrossingRampTypeEnumeration pedestrianCrossingRampType) {
        this.pedestrianCrossingRampType = pedestrianCrossingRampType;
    }

    public AccessibilityLevelEnumeration getAccessibilityLevel() {
        return accessibilityLevel;
    }

    public void setAccessibilityLevel(AccessibilityLevelEnumeration accessibilityLevel) {
        this.accessibilityLevel = accessibilityLevel;
    }
}
