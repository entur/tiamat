package no.rutebanken.tiamat.ifopt.transfer.disassembler;

import no.rutebanken.tiamat.ifopt.transfer.dto.QuayDTO;
import no.rutebanken.tiamat.repository.ifopt.QuayRepository;
import org.junit.Test;
import uk.org.netex.netex.Quay;
import uk.org.netex.netex.QuayTypeEnumeration;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QuayDisassemblerTest {

    QuayRepository quayRepository = mock(QuayRepository.class);
    SimplePointDisassembler simplePointDisassembler = mock(SimplePointDisassembler.class);


    @Test
    public void disassembledQuayNotNull() {
        QuayDisassembler quayDisassembler = new QuayDisassembler(quayRepository, simplePointDisassembler);

        QuayDTO quayDTO = new QuayDTO();

        Quay quay = quayDisassembler.disassemble(quayDTO);

        assertThat(quay).isNotNull();
    }

    @Test
    public void disassembledQuayNName() {
        QuayDisassembler quayDisassembler = new QuayDisassembler(quayRepository, simplePointDisassembler);

        QuayDTO quayDTO = new QuayDTO();
        quayDTO.name = "name";
        Quay quay = quayDisassembler.disassemble(quayDTO);

        assertThat(quay.getName().getValue()).isEqualTo(quayDTO.name);
    }


    @Test
    public void disassembledExistingQuay() {
        QuayDisassembler quayDisassembler = new QuayDisassembler(quayRepository, simplePointDisassembler);

        QuayDTO quayDTO = new QuayDTO();
        quayDTO.id = "xyz";

        Quay quay = new Quay();
        quay.setId(quayDTO.id);

        when(quayRepository.findOne(quayDTO.id)).thenReturn(quay);

        Quay actualQuay = quayDisassembler.disassemble(quayDTO);

        assertThat(actualQuay.getId()).isEqualTo(quayDTO.id);
    }

    @Test
    public void dissasembleQuayWithQuayType() {
        QuayDisassembler quayDisassembler = new QuayDisassembler(quayRepository, simplePointDisassembler);

        QuayDTO quayDTO = new QuayDTO();
        quayDTO.quayType = "vehicleLoadingPlace";

        Quay actualQuay = quayDisassembler.disassemble(quayDTO);

        assertThat(actualQuay.getQuayType()).isEqualTo(QuayTypeEnumeration.VEHICLE_LOADING_PLACE);
    }

}