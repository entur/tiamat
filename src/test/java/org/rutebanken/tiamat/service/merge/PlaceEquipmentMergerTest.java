/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

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