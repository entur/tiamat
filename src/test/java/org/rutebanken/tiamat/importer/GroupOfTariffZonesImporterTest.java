package org.rutebanken.tiamat.importer;

import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.GroupOfTariffZones;
import org.rutebanken.tiamat.model.TariffZoneRef;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GroupOfTariffZonesImporterTest extends TiamatIntegrationTest {
    @Autowired
    private GroupOfTariffZonesImporter groupOfTariffZonesImporter;

    @Test
    public void importGroupOfTariffZones() {
        var groupOfTariffZones = new GroupOfTariffZones();
        groupOfTariffZones.setNetexId("NSR:GroupOfTariffZones:1");
        groupOfTariffZones.setVersion(1);
        Collection<TariffZoneRef> members = new ArrayList<>();
        var tariffZoneRef1 = new TariffZoneRef("RUT:FareZone:1");
        tariffZoneRef1.setVersion("1");
        var tariffZoneRef2 = new TariffZoneRef("RUT:FareZone:2");
        tariffZoneRef1.setVersion("1");
        members.add(tariffZoneRef1);
        members.add(tariffZoneRef2);
        groupOfTariffZones.getMembers().addAll(members);


        final List<org.rutebanken.netex.model.GroupOfTariffZones> imported = groupOfTariffZonesImporter.importGroupOfTariffZones(List.of(groupOfTariffZones));

        assertThat(imported).isNotNull().hasSize(1);


        final List<GroupOfTariffZones> result = groupOfTariffZonesRepository.findByNetexId(groupOfTariffZones.getNetexId());

        assertThat(result)
                .as("Imported group of tariff zones")
                .hasSize(1);

        assertThat(result.getFirst().getMembers().size()).isEqualTo(2);


    }

}
