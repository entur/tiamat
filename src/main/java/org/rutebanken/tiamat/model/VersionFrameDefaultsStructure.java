package org.rutebanken.tiamat.model;

public class VersionFrameDefaultsStructure {

    protected CodespaceRefStructure defaultCodespaceRef;
    protected DataSourceRefStructure defaultDataSourceRef;
    protected ResponsibilitySetRefStructure defaultResponsibilitySetRef;
    protected LocaleStructure defaultLocale;
    protected String defaultLocationSystem;
    protected SystemOfUnits defaultSystemOfUnits;
    protected String defaultCurrency;

    public CodespaceRefStructure getDefaultCodespaceRef() {
        return defaultCodespaceRef;
    }

    public void setDefaultCodespaceRef(CodespaceRefStructure value) {
        this.defaultCodespaceRef = value;
    }

    public DataSourceRefStructure getDefaultDataSourceRef() {
        return defaultDataSourceRef;
    }

    public void setDefaultDataSourceRef(DataSourceRefStructure value) {
        this.defaultDataSourceRef = value;
    }

    public ResponsibilitySetRefStructure getDefaultResponsibilitySetRef() {
        return defaultResponsibilitySetRef;
    }

    public void setDefaultResponsibilitySetRef(ResponsibilitySetRefStructure value) {
        this.defaultResponsibilitySetRef = value;
    }

    public LocaleStructure getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(LocaleStructure value) {
        this.defaultLocale = value;
    }

    public String getDefaultLocationSystem() {
        return defaultLocationSystem;
    }

    public void setDefaultLocationSystem(String value) {
        this.defaultLocationSystem = value;
    }

    public SystemOfUnits getDefaultSystemOfUnits() {
        return defaultSystemOfUnits;
    }

    public void setDefaultSystemOfUnits(SystemOfUnits value) {
        this.defaultSystemOfUnits = value;
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(String value) {
        this.defaultCurrency = value;
    }

}
