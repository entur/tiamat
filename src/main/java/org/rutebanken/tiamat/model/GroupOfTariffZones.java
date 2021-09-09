package org.rutebanken.tiamat.model;

import com.google.common.base.MoreObjects;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import java.util.HashSet;
import java.util.Set;

@Entity
public class GroupOfTariffZones extends GroupOfEntities_VersionStructure {

    @ElementCollection(targetClass = TariffZoneRef.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "group_of_tariff_zones_members")
    private Set<TariffZoneRef> members = new HashSet<>();

    @AttributeOverrides({
            @AttributeOverride(name = "ref", column = @Column(name = "purpose_of_grouping_ref")),
            @AttributeOverride(name = "version", column = @Column(name = "purpose_of_grouping_ref_version"))
    })
    @Embedded
    protected PurposeOfGroupingRefStructure purposeOfGroupingRef;

    public Set<TariffZoneRef> getMembers() {
        return members;
    }

    public PurposeOfGroupingRefStructure getPurposeOfGroupingRef() {
        return purposeOfGroupingRef;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("netexId", netexId)
                .add("version", version)
                .add("name", name)
                .add("members", members)
                .toString();
    }
}
