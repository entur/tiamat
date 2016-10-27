package org.rutebanken.tiamat.model;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@MappedSuperclass
public abstract class DataManagedObjectStructure
    extends EntityInVersionStructure
{
    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    protected KeyListStructure keyList;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Map<String,ValueList> keyValues = new HashMap<>();

    @Transient
    protected ExtensionsStructure extensions;

    @Transient
    protected BrandingRefStructure brandingRef;

    @Transient
    protected String responsibilitySetRef;

    public KeyListStructure getKeyList() {
        return keyList;
    }

    public void setKeyList(KeyListStructure value) {
        this.keyList = value;
    }

    public ExtensionsStructure getExtensions() {
        return extensions;
    }

    public void setExtensions(ExtensionsStructure value) {
        this.extensions = value;
    }

    public BrandingRefStructure getBrandingRef() {
        return brandingRef;
    }

    public void setBrandingRef(BrandingRefStructure value) {
        this.brandingRef = value;
    }

    public String getResponsibilitySetRef() {
        return responsibilitySetRef;
    }

    public void setResponsibilitySetRef(String value) {
        this.responsibilitySetRef = value;
    }

    public Map<String, ValueList> getKeyValues() {
        return keyValues;
    }
}
