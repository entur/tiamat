package no.rutebanken.tiamat.ifopt.transfer.assembler;


import no.rutebanken.tiamat.ifopt.transfer.dto.QuayDTO;
import org.junit.Test;
import no.rutebanken.tiamat.model.Quay;
import no.rutebanken.tiamat.model.QuayTypeEnumeration;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;

public class QuayAssemblerTest {

    private QuayAssembler quayAssembler = new QuayAssembler(mock(SimplePointAssembler.class));

    @Test
    public void assembleQuayWithQuayType() {


        Quay quay = new Quay();
        quay.setQuayType(QuayTypeEnumeration.BUS_BAY);

        QuayDTO quayDTO = quayAssembler.assemble(quay);


        assertThat(quayDTO.quayType).isEqualTo("busBay");
    }

}