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
