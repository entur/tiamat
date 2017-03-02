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
    private QuayRepository quayRepository = mock(QuayRepository.class);
    private PointDisassembler pointDisassembler = mock(PointDisassembler.class);
    private QuayDisassembler quayDisassembler = new QuayDisassembler(quayRepository, pointDisassembler);


    @Test
    public void disassembleQuayNotNull() {
        QuayDto quayDto = new QuayDto();
        Quay quay = quayDisassembler.disassemble(quayDto);

        assertThat(quay).isNotNull();
    }

    @Test
    public void disassembleQuayNName() {
        QuayDto quayDto = new QuayDto();
        quayDto.name = "name";
        Quay quay = quayDisassembler.disassemble(quayDto);

        assertThat(quay.getName().getValue()).isEqualTo(quayDto.name);
    }

    @Test
    public void disassembleQuayDescription() {
        QuayDto quayDto = new QuayDto();
        quayDto.description = "description";
        Quay quay = quayDisassembler.disassemble(quayDto);

        assertThat(quay.getDescription().getValue()).isEqualTo(quayDto.description);
    }


    @Test
    public void disassembleExistingQuay() {
        QuayDto quayDto = new QuayDto();
        quayDto.id = "12333";

        Quay quay = new Quay();
        quay.setNetexId(quayDto.id);

        when(quayRepository.findByNetexId(quayDto.id)).thenReturn(quay);

        Quay actualQuay = quayDisassembler.disassemble(quayDto);

        assertThat(actualQuay.getNetexId().toString()).isEqualTo(quayDto.id);
    }

    @Test
    public void disassembleCompassBearing() {
        QuayDto quayDto = new QuayDto();
        quayDto.compassBearing = 250;

        Quay quay = quayDisassembler.disassemble(quayDto);

        assertThat(quay.getCompassBearing()).isEqualTo(250f);
    }

    @Test
    public void disassembledNullCompassBearing() {
        QuayDto quayDto = new QuayDto();
        quayDto.compassBearing = null;

        Quay quay = quayDisassembler.disassemble(quayDto);

        assertThat(quay.getCompassBearing()).isNull();
    }

}