package org.rutebanken.tiamat.dtoassembling.disassembler;

import org.rutebanken.tiamat.dtoassembling.dto.QuayDto;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.junit.Test;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.QuayTypeEnumeration;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QuayDisassemblerTest {

    QuayRepository quayRepository = mock(QuayRepository.class);
    PointDisassembler pointDisassembler = mock(PointDisassembler.class);


    @Test
    public void disassembledQuayNotNull() {
        QuayDisassembler quayDisassembler = new QuayDisassembler(quayRepository, pointDisassembler);

        QuayDto quayDto = new QuayDto();

        Quay quay = quayDisassembler.disassemble(quayDto);

        assertThat(quay).isNotNull();
    }

    @Test
    public void disassembledQuayNName() {
        QuayDisassembler quayDisassembler = new QuayDisassembler(quayRepository, pointDisassembler);

        QuayDto quayDto = new QuayDto();
        quayDto.name = "name";
        Quay quay = quayDisassembler.disassemble(quayDto);

        assertThat(quay.getName().getValue()).isEqualTo(quayDto.name);
    }

    @Test
    public void disassembledQuayDescription() {
        QuayDisassembler quayDisassembler = new QuayDisassembler(quayRepository, pointDisassembler);

        QuayDto quayDto = new QuayDto();
        quayDto.description = "description";
        Quay quay = quayDisassembler.disassemble(quayDto);

        assertThat(quay.getDescription().getValue()).isEqualTo(quayDto.description);
    }


    @Test
    public void disassembledExistingQuay() {
        QuayDisassembler quayDisassembler = new QuayDisassembler(quayRepository, pointDisassembler);

        QuayDto quayDto = new QuayDto();
        quayDto.id = "12333";

        Quay quay = new Quay();
        quay.setId(Long.valueOf(quayDto.id));

        when(quayRepository.findOne(Long.valueOf(quayDto.id))).thenReturn(quay);

        Quay actualQuay = quayDisassembler.disassemble(quayDto);

        assertThat(actualQuay.getId().toString()).isEqualTo(quayDto.id);
    }

}