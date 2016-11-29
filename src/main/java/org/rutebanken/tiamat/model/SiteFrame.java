package org.rutebanken.tiamat.model;

import com.google.common.base.MoreObjects;

public class SiteFrame
        extends Common_VersionFrameStructure {

    protected TopographicPlacesInFrame topographicPlaces = new TopographicPlacesInFrame();
    protected AddressesInFrame_RelStructure addresses;
    protected AccessesInFrame_RelStructure accesses;
    protected StopPlacesInFrame_RelStructure stopPlaces;
    protected FlexibleStopPlacesInFrame_RelStructure flexibleStopPlaces;
    protected PointsOfInterestInFrame_RelStructure pointsOfInterest;
    protected ParkingsInFrame_RelStructure parkings;
    protected NavigationPathsInFrame_RelStructure navigationPaths;
    protected PathLinksInFrame_RelStructure pathLinks;
    protected PathJunctionsInFrame_RelStructure pathJunctions;
    protected CheckConstraintInFrame_RelStructure checkConstraints;
    protected CheckConstraintDelaysInFrame_RelStructure checkConstraintDelays;
    protected CheckConstraintThroughputsInFrame_RelStructure checkConstraintThroughputs;
    protected PointOfInterestClassifications pointOfInterestClassifications;
    protected PointOfInterestClassificationHierarchiesInFrame_RelStructure pointOfInterestClassificationHierarchies;
    protected SiteFacilitySetsInFrame_RelStructure siteFacilitySets;

    public TopographicPlacesInFrame getTopographicPlaces() {
        return topographicPlaces;
    }

    public void setTopographicPlaces(TopographicPlacesInFrame value) {
        this.topographicPlaces = value;
    }

    public AddressesInFrame_RelStructure getAddresses() {
        return addresses;
    }

    public void setAddresses(AddressesInFrame_RelStructure value) {
        this.addresses = value;
    }

    public AccessesInFrame_RelStructure getAccesses() {
        return accesses;
    }

    public void setAccesses(AccessesInFrame_RelStructure value) {
        this.accesses = value;
    }

    public StopPlacesInFrame_RelStructure getStopPlaces() {
        return stopPlaces;
    }

    public void setStopPlaces(StopPlacesInFrame_RelStructure value) {
        this.stopPlaces = value;
    }

    public FlexibleStopPlacesInFrame_RelStructure getFlexibleStopPlaces() {
        return flexibleStopPlaces;
    }

    public void setFlexibleStopPlaces(FlexibleStopPlacesInFrame_RelStructure value) {
        this.flexibleStopPlaces = value;
    }

    public PointsOfInterestInFrame_RelStructure getPointsOfInterest() {
        return pointsOfInterest;
    }

    public void setPointsOfInterest(PointsOfInterestInFrame_RelStructure value) {
        this.pointsOfInterest = value;
    }

    public ParkingsInFrame_RelStructure getParkings() {
        return parkings;
    }

    public void setParkings(ParkingsInFrame_RelStructure value) {
        this.parkings = value;
    }

    public NavigationPathsInFrame_RelStructure getNavigationPaths() {
        return navigationPaths;
    }

    public void setNavigationPaths(NavigationPathsInFrame_RelStructure value) {
        this.navigationPaths = value;
    }

    public PathLinksInFrame_RelStructure getPathLinks() {
        return pathLinks;
    }

    public void setPathLinks(PathLinksInFrame_RelStructure value) {
        this.pathLinks = value;
    }

    public PathJunctionsInFrame_RelStructure getPathJunctions() {
        return pathJunctions;
    }

    public void setPathJunctions(PathJunctionsInFrame_RelStructure value) {
        this.pathJunctions = value;
    }

    public CheckConstraintInFrame_RelStructure getCheckConstraints() {
        return checkConstraints;
    }

    public void setCheckConstraints(CheckConstraintInFrame_RelStructure value) {
        this.checkConstraints = value;
    }

    public CheckConstraintDelaysInFrame_RelStructure getCheckConstraintDelays() {
        return checkConstraintDelays;
    }

    public void setCheckConstraintDelays(CheckConstraintDelaysInFrame_RelStructure value) {
        this.checkConstraintDelays = value;
    }

    public CheckConstraintThroughputsInFrame_RelStructure getCheckConstraintThroughputs() {
        return checkConstraintThroughputs;
    }

    public void setCheckConstraintThroughputs(CheckConstraintThroughputsInFrame_RelStructure value) {
        this.checkConstraintThroughputs = value;
    }

    public PointOfInterestClassifications getPointOfInterestClassifications() {
        return pointOfInterestClassifications;
    }

    public void setPointOfInterestClassifications(PointOfInterestClassifications value) {
        this.pointOfInterestClassifications = value;
    }

    public PointOfInterestClassificationHierarchiesInFrame_RelStructure getPointOfInterestClassificationHierarchies() {
        return pointOfInterestClassificationHierarchies;
    }

    public void setPointOfInterestClassificationHierarchies(PointOfInterestClassificationHierarchiesInFrame_RelStructure value) {
        this.pointOfInterestClassificationHierarchies = value;
    }

    public SiteFacilitySetsInFrame_RelStructure getSiteFacilitySets() {
        return siteFacilitySets;
    }

    public void setSiteFacilitySets(SiteFacilitySetsInFrame_RelStructure value) {
        this.siteFacilitySets = value;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("id", id)
                .add("name", name)
                .add("topoGraphicPlaces", getTopographicPlaces() != null && getTopographicPlaces().getTopographicPlace() != null ? getTopographicPlaces().getTopographicPlace().size() : 0)
                .add("stops", getStopPlaces() != null && getStopPlaces().getStopPlace() != null ? getStopPlaces().getStopPlace().size() : 0)
                .add("keyValues", getKeyValues())
                .toString();
    }
}
