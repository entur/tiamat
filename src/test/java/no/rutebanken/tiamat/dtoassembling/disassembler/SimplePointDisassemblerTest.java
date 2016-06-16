package no.rutebanken.tiamat.dtoassembling.disassembler;

import com.vividsolutions.jts.geom.GeometryFactory;
import no.rutebanken.tiamat.config.GeometryFactoryConfig;
import no.rutebanken.tiamat.dtoassembling.dto.LocationDto;
import no.rutebanken.tiamat.dtoassembling.dto.SimplePointDto;
import org.junit.Test;
import no.rutebanken.tiamat.model.SimplePoint;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class SimplePointDisassemblerTest {

    private GeometryFactory geometryFactory = new GeometryFactoryConfig().geometryFactory();

    @Test
    public void dissasembleLongitudeAndLatitude() throws Exception {
        SimplePointDto simplePointDto = new SimplePointDto();
        simplePointDto.location = new LocationDto();
        simplePointDto.location.latitude = 10.123123;
        simplePointDto.location.longitude = 59.123123;

        SimplePoint simplePoint = new SimplePointDisassembler(geometryFactory).disassemble(simplePointDto);

        assertThat(simplePoint).isNotNull();
        assertThat(simplePoint.getLocation()).isNotNull();
        assertThat(simplePoint.getLocation().getGeometryPoint().getX()).isEqualTo(simplePointDto.location.longitude);
        assertThat(simplePoint.getLocation().getGeometryPoint().getY()).isEqualTo(simplePointDto.location.latitude);
    }
}