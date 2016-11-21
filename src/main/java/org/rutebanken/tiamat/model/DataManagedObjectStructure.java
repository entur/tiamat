package org.rutebanken.tiamat.model;

import javax.persistence.CascadeType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@MappedSuperclass
public abstract class DataManagedObjectStructure
        extends EntityInVersionStructure {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private final Map<String, Value> keyValues = new HashMap<>();

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

    public Set<String> getOrCreateValues(String key) {
        if (keyValues.get(key) == null) {
            keyValues.put(key, new Value());
        }

        return keyValues.get(key).getItems();
    }
}
