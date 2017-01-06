package org.rutebanken.tiamat.dtoassembling.disassembler;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.rutebanken.tiamat.config.GeometryFactoryConfig;
import org.rutebanken.tiamat.dtoassembling.dto.LocationDto;
import org.rutebanken.tiamat.dtoassembling.dto.SimplePointDto;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class PointDisassemblerTest {

    private GeometryFactory geometryFactory = new GeometryFactoryConfig().geometryFactory();

    @Test
    public void dissasembleLongitudeAndLatitude() throws Exception {
        SimplePointDto simplePointDto = new SimplePointDto();
        simplePointDto.location = new LocationDto();
        simplePointDto.location.latitude = 10.123123;
        simplePointDto.location.longitude = 59.123123;

        Point point = new PointDisassembler(geometryFactory).disassemble(simplePointDto);

        assertThat(point).isNotNull();
        assertThat(point.getX()).isEqualTo(simplePointDto.location.longitude);
        assertThat(point.getY()).isEqualTo(simplePointDto.location.latitude);
    }
}