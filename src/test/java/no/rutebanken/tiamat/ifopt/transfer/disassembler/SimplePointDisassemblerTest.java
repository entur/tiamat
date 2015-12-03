package no.rutebanken.tiamat.ifopt.transfer.disassembler;

import no.rutebanken.tiamat.ifopt.transfer.dto.LocationDTO;
import no.rutebanken.tiamat.ifopt.transfer.dto.SimplePointDTO;
import org.junit.Test;
import uk.org.netex.netex.SimplePoint_VersionStructure;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class SimplePointDisassemblerTest {


    @Test
    public void disasembleLongitudeAndLatitude() throws Exception {
        SimplePointDTO simplePointDTO = new SimplePointDTO();
        simplePointDTO.location = new LocationDTO();
        simplePointDTO.location.latitude = "10.123123";
        simplePointDTO.location.longitude = "59.123123";

        SimplePoint_VersionStructure simplePoint = new SimplePointDisassembler().disassemble(new SimplePoint_VersionStructure(), simplePointDTO);

        assertThat(simplePoint).isNotNull();
        assertThat(simplePoint.getLocation()).isNotNull();
        assertThat(simplePoint.getLocation().getLatitude()).isEqualTo(new BigDecimal(simplePointDTO.location.latitude));
        assertThat(simplePoint.getLocation().getLongitude()).isEqualTo(new BigDecimal(simplePointDTO.location.longitude));
    }
}