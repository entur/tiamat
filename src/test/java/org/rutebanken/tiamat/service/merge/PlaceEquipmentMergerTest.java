package org.rutebanken.tiamat.service.merge;

import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PlaceEquipmentMergerTest extends TiamatIntegrationTest {

    @Autowired
    private PlaceEquipmentMerger placeEquipmentMerger;

    @Test
    public void testMergePlaceEquipment() {


        PlaceEquipment fromPlaceEquipment = new PlaceEquipment();
        GeneralSign generalSign = new GeneralSign();
        generalSign.setSignContentType(SignContentEnumeration.TRANSPORT_MODE);
        generalSign.setPublicCode(new PrivateCodeStructure("111", "111111"));
        fromPlaceEquipment.getInstalledEquipment().add(generalSign);


        PlaceEquipment toPlaceEquipment = new PlaceEquipment();
        GeneralSign generalSign2 = new GeneralSign();
        generalSign2.setSignContentType(SignContentEnumeration.TRANSPORT_MODE);
        generalSign2.setPublicCode(new PrivateCodeStructure("222", "222222"));
        toPlaceEquipment.getInstalledEquipment().add(generalSign2);

        placeEquipmentMerger.mergePlaceEquipments(fromPlaceEquipment, toPlaceEquipment);

        List<InstalledEquipment_VersionStructure> equipment = toPlaceEquipment.getInstalledEquipment();
        assertThat(equipment).hasSize(2);
        assertThat(equipment).contains(generalSign2);
        assertThat(equipment).doesNotContain(generalSign);

    }

}