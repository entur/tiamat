package org.rutebanken.tiamat.model;

public class VersionFrame_VersionStructure
        extends DataManagedObjectStructure {

    protected MultilingualStringEntity name;
    protected MultilingualStringEntity description;
    protected TypeOfFrameRefStructure typeOfFrameRef;
    protected VersionRefStructure baselineVersionFrameRef;
    protected Codespaces_RelStructure codespaces;
    protected VersionFrameDefaultsStructure frameDefaults;
    protected Versions_RelStructure versions;
    protected Traces_RelStructure traces;
    protected ValidityConditions_RelStructure contentValidityConditions;

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public MultilingualStringEntity getDescription() {
        return description;
    }

    public void setDescription(MultilingualStringEntity value) {
        this.description = value;
    }

    public TypeOfFrameRefStructure getTypeOfFrameRef() {
        return typeOfFrameRef;
    }

    public void setTypeOfFrameRef(TypeOfFrameRefStructure value) {
        this.typeOfFrameRef = value;
    }

    public VersionRefStructure getBaselineVersionFrameRef() {
        return baselineVersionFrameRef;
    }

    public void setBaselineVersionFrameRef(VersionRefStructure value) {
        this.baselineVersionFrameRef = value;
    }

    public Codespaces_RelStructure getCodespaces() {
        return codespaces;
    }

    public void setCodespaces(Codespaces_RelStructure value) {
        this.codespaces = value;
    }

    public VersionFrameDefaultsStructure getFrameDefaults() {
        return frameDefaults;
    }

    public void setFrameDefaults(VersionFrameDefaultsStructure value) {
        this.frameDefaults = value;
    }

    public Versions_RelStructure getVersions() {
        return versions;
    }

    public void setVersions(Versions_RelStructure value) {
        this.versions = value;
    }

    public Traces_RelStructure getTraces() {
        return traces;
    }

    public void setTraces(Traces_RelStructure value) {
        this.traces = value;
    }

    public ValidityConditions_RelStructure getContentValidityConditions() {
        return contentValidityConditions;
    }

    public void setContentValidityConditions(ValidityConditions_RelStructure value) {
        this.contentValidityConditions = value;
    }

}
