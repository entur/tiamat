package org.rutebanken.tiamat.dtoassembling.assembler;


import org.rutebanken.tiamat.dtoassembling.dto.QuayDto;
import org.rutebanken.tiamat.model.MultilingualString;
import org.junit.Test;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.QuayTypeEnumeration;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;

public class QuayAssemblerTest {

    private QuayAssembler quayAssembler = new QuayAssembler(mock(SimplePointAssembler.class));

    @Test
    public void assembleQuayWithQuayType() {


        Quay quay = new Quay();
        quay.setQuayType(QuayTypeEnumeration.BUS_BAY);

        QuayDto quayDto = quayAssembler.assemble(quay);

        assertThat(quayDto.quayType).isEqualTo("busBay");
    }

    @Test
    public void assembleQuayWithQuayDescription() {

        Quay quay = new Quay();
        quay.setDescription(new MultilingualString("description","no",""));

        QuayDto quayDto = quayAssembler.assemble(quay);

        assertThat(quayDto.description).isEqualTo("description");
    }


}