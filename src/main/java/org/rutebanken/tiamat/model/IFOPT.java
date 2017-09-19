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

public class IFOPT {

    protected Organisations organisations;
    protected ResponsibilitySets responsibilitySets;
    protected Sites sites;
    protected StopAssignments stopAssignments;
    protected Connections connections;
    protected NavigationPaths navigationPaths;
    protected SchematicMaps schematicMaps;
    protected String version;

    public Organisations getOrganisations() {
        return organisations;
    }

    public void setOrganisations(Organisations value) {
        this.organisations = value;
    }

    public ResponsibilitySets getResponsibilitySets() {
        return responsibilitySets;
    }

    public void setResponsibilitySets(ResponsibilitySets value) {
        this.responsibilitySets = value;
    }

    public Sites getSites() {
        return sites;
    }

    public void setSites(Sites value) {
        this.sites = value;
    }

    public StopAssignments getStopAssignments() {
        return stopAssignments;
    }

    public void setStopAssignments(StopAssignments value) {
        this.stopAssignments = value;
    }

    public Connections getConnections() {
        return connections;
    }

    public void setConnections(Connections value) {
        this.connections = value;
    }

    public NavigationPaths getNavigationPaths() {
        return navigationPaths;
    }

    public void setNavigationPaths(NavigationPaths value) {
        this.navigationPaths = value;
    }

    public SchematicMaps getSchematicMaps() {
        return schematicMaps;
    }

    public void setSchematicMaps(SchematicMaps value) {
        this.schematicMaps = value;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String value) {
        this.version = value;
    }

}
