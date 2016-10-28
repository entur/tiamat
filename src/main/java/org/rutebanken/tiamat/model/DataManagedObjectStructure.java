package org.rutebanken.tiamat.model;

import org.rutebanken.tiamat.netexmapping.NetexIdMapper;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@MappedSuperclass
public abstract class DataManagedObjectStructure
    extends EntityInVersionStructure
{
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Map<String,Value> keyValues = new HashMap<>();

    @Transient
    protected ExtensionsStructure extensions;

    @Transient
    protected BrandingRefStructure brandingRef;

    @Transient
    protected String responsibilitySetRef;

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

    public Map<String, Value> getKeyValues() {
        return keyValues;
    }

    public List<String> getOrCreateValues(String key) {
        if (getKeyValues().get(key) == null) {
            keyValues.put(key, new Value());
        }

        return keyValues.get(key).getItems();
    }
}
