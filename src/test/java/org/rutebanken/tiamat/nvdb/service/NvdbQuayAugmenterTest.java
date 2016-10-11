package org.rutebanken.tiamat.nvdb.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.rutebanken.tiamat.nvdb.model.Egenskap;
import org.rutebanken.tiamat.nvdb.model.EnumVerdi;
import org.rutebanken.tiamat.nvdb.model.VegObjekt;
import org.junit.Test;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.QuayTypeEnumeration;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class NvdbQuayAugmenterTest {


    private final ObjectMapper objectMapper = new ObjectMapper(new JsonFactory());
    private NvdbQuayAugmenter quayAugmenter = new NvdbQuayAugmenter();

    @Test
    public void testAugmentFromNvdb() throws Exception {


        VegObjekt vegObjekt = new VegObjekt();


        Egenskap egenskap = new Egenskap();
        egenskap.setId(3956);
        egenskap.setVerdi("Platform og lomme");


        EnumVerdi enumVerdi = new EnumVerdi();
        enumVerdi.setId(NvdbQuayAugmenter.ENUM_ID_HOLDEPLASS_LOMME);

        egenskap.setEnumVerdi(enumVerdi);

        vegObjekt.getEgenskaper().add(egenskap);


        Quay quay = new Quay();

        quay = quayAugmenter.augmentFromNvdb(quay, vegObjekt);

        assertThat(quay).isNotNull();
        assertThat(quay.getQuayType()).isEqualTo(QuayTypeEnumeration.BUS_BAY);

    }

}