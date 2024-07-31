package org.rutebanken.tiamat.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "organisation_netex_id_version_constraint", columnNames = {"netexId", "version"})}
)
public class Organisation extends Organisation_VersionStructure {
}
