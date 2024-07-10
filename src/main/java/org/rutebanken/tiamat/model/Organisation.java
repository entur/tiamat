package org.rutebanken.tiamat.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "organisation_netex_id_version_constraint", columnNames = {"netexId", "version"})}
)
public class Organisation extends Organisation_VersionStructure {
}
