package org.rutebanken.tiamat.model;

import java.io.Serial;
import java.io.Serializable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "info_spot_netex_id_version_constraint", columnNames = {"netexId", "version"})}
)
public class InfoSpot extends InfoSpot_VersionStructure implements Serializable {


    @Serial
    private static final long serialVersionUID = 2459437438760752116L;
}
