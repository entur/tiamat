package no.rutebanken.tiamat.ifopt.transfer.disassembler;

import com.vividsolutions.jts.geom.GeometryFactory;
import no.rutebanken.tiamat.config.GeometryFactoryConfig;
import no.rutebanken.tiamat.ifopt.transfer.dto.LocationDTO;
import no.rutebanken.tiamat.ifopt.transfer.dto.SimplePointDTO;
import org.junit.Test;
import no.rutebanken.tiamat.model.SimplePoint;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class SimplePointDisassemblerTest {

    private GeometryFactory geometryFactory = new GeometryFactoryConfig().geometryFactory();

    @Test
    public void dissasembleLongitudeAndLatitude() throws Exception {
        SimplePointDTO simplePointDTO = new SimplePointDTO();
        simplePointDTO.location = new LocationDTO();
        simplePointDTO.location.latitude = 10.123123;
        simplePointDTO.location.longitude = 59.123123;

        SimplePoint simplePoint = new SimplePointDisassembler(geometryFactory).disassemble(simplePointDTO);

        assertThat(simplePoint).isNotNull();
        assertThat(simplePoint.getLocation()).isNotNull();
        assertThat(simplePoint.getLocation().getGeometryPoint().getX()).isEqualTo(simplePointDTO.location.longitude);
        assertThat(simplePoint.getLocation().getGeometryPoint().getY()).isEqualTo(simplePointDTO.location.latitude);
    }
}