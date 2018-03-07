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

package org.rutebanken.tiamat.model;

public class PointOfInterest_VersionStructure
        extends Site_VersionStructure {

    protected PointOfInterestClassificationsViews_RelStructure classifications;
    protected PointOfInterestSpaces_RelStructure spaces;
    protected TopographicPlaceRefs_RelStructure nearTopographicPlaces;
    protected SitePathLinks_RelStructure pathLinks;
    protected PathJunctions_RelStructure pathJunctions;
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

    public NavigationPaths_RelStructure getNavigationPaths() {
        return navigationPaths;
    }

    public void setNavigationPaths(NavigationPaths_RelStructure value) {
        this.navigationPaths = value;
    }

}
