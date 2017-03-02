package org.rutebanken.tiamat.dtoassembling.assembler;


import org.rutebanken.tiamat.dtoassembling.dto.QuayDto;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.MultilingualStringEntity;
import org.junit.Test;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.QuayTypeEnumeration;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;

public class QuayAssemblerTest {

    private QuayAssembler quayAssembler = new QuayAssembler(mock(PointAssembler.class));

    @Test
    public void assembleQuayWithQuayDescription() {

        Quay quay = new Quay();
        quay.setDescription(new EmbeddableMultilingualString("description","no"));

        QuayDto quayDto = quayAssembler.assemble(quay);

        assertThat(quayDto.description).isEqualTo("description");
    }

    /**
     * https://rutebanken.atlassian.net/browse/NRP-677
     */
    @Test
    public void assembleQuayAndVerifyIdInReturn() {
        Quay quay = new Quay();
        quay.setNetexId("123");

        QuayDto quayDto = quayAssembler.assemble(quay);

        assertThat(quayDto.id).isNotEmpty();
        assertThat(quayDto.id).contains(quay.getNetexId().toString());
    }

    @Test
    public void assembleQuayWithBearing() {
        Quay quay = new Quay();
        quay.setCompassBearing(250f);

        QuayDto quayDto = quayAssembler.assemble(quay);

        assertThat(quayDto.compassBearing).isEqualTo(250);
    }

}