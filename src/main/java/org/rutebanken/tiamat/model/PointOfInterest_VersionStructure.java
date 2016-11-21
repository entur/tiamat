

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class PointOfInterest_VersionStructure
    extends Site_VersionStructure
{

    protected PointOfInterestClassificationsViews_RelStructure classifications;
    protected PointOfInterestSpaces_RelStructure spaces;
    protected TopographicPlaceRefs_RelStructure nearTopographicPlaces;
    protected SitePathLinks_RelStructure pathLinks;
    protected PathJunctions_RelStructure pathJunctions;
    protected Accesses_RelStructure accesses;
    protected NavigationPaths_RelStructure navigationPaths;

    public PointOfInterestClassificationsViews_RelStructure getClassifications() {
        return classifications;
    }

    public void setClassifications(PointOfInterestClassificationsViews_RelStructure value) {
    }

    public PointOfInterestSpaces_RelStructure getSpaces() {
        return spaces;
    }

    public void setSpaces(PointOfInterestSpaces_RelStructure value) {
        this.spaces = value;
    }

    public TopographicPlaceRefs_RelStructure getNearTopographicPlaces() {
        return nearTopographicPlaces;
    }

    public void setNearTopographicPlaces(TopographicPlaceRefs_RelStructure value) {
        this.nearTopographicPlaces = value;
    }

    public SitePathLinks_RelStructure getPathLinks() {
        return pathLinks;
    }

    public void setPathLinks(SitePathLinks_RelStructure value) {
        this.pathLinks = value;
    }

    public PathJunctions_RelStructure getPathJunctions() {
        return pathJunctions;
    }

    public void setPathJunctions(PathJunctions_RelStructure value) {
        this.pathJunctions = value;
    }

    public Accesses_RelStructure getAccesses() {
        return accesses;
    }

    public void setAccesses(Accesses_RelStructure value) {
        this.accesses = value;
    }

    public NavigationPaths_RelStructure getNavigationPaths() {
        return navigationPaths;
    }

    public void setNavigationPaths(NavigationPaths_RelStructure value) {
        this.navigationPaths = value;
    }

}
