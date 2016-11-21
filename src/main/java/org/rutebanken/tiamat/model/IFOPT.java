

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


    "organisations",
    "responsibilitySets",
    "sites",
    "stopAssignments",
    "connections",
    "navigationPaths",
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
