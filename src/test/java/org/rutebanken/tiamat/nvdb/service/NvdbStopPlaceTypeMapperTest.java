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

package org.rutebanken.tiamat.nvdb.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.nvdb.model.Egenskap;
import org.rutebanken.tiamat.nvdb.model.EnumVerdi;
import org.rutebanken.tiamat.nvdb.model.VegObjekt;
import org.junit.Test;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.QuayTypeEnumeration;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class NvdbStopPlaceTypeMapperTest {


    private final ObjectMapper objectMapper = new ObjectMapper(new JsonFactory());
    private NvdbStopPlaceTypeMapper nvdbStopPlaceTypeMapper = new NvdbStopPlaceTypeMapper();

    @Test
    public void testAugmentFromNvdb() throws Exception {


        VegObjekt vegObjekt = new VegObjekt();


        Egenskap egenskap = new Egenskap();
        egenskap.setId(3956);
        egenskap.setVerdi("Platform og lomme");


        EnumVerdi enumVerdi = new EnumVerdi();
        enumVerdi.setId(NvdbStopPlaceTypeMapper.ENUM_ID_HOLDEPLASS_LOMME);

        egenskap.setEnumVerdi(enumVerdi);

        vegObjekt.getEgenskaper().add(egenskap);


       StopPlace stopPlace = new StopPlace();

        stopPlace = nvdbStopPlaceTypeMapper.augmentFromNvdb(stopPlace, vegObjekt);

        assertThat(stopPlace).isNotNull();
        assertThat(stopPlace.getStopPlaceType()).isEqualTo(StopTypeEnumeration.ONSTREET_BUS);

    }

}