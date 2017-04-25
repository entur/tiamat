package org.rutebanken.tiamat.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class TariffZoneRef extends ZoneRefStructure {

    @Id
    @GeneratedValue(generator="sequence_per_table_generator")
    private Long id;

    public TariffZoneRef(TariffZone tariffZone) {
        this.setRef(tariffZone.getNetexId());
        this.setVersion(String.valueOf(tariffZone.getVersion()));
    }
}
