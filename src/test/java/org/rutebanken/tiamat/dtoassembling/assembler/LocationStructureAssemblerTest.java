package org.rutebanken.tiamat.dtoassembling.assembler;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.rutebanken.tiamat.CommonSpringBootTest;
import org.rutebanken.tiamat.dtoassembling.dto.SimplePointDto;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class LocationStructureAssemblerTest extends CommonSpringBootTest {

    @Autowired
    private PointAssembler pointAssembler;

    @Autowired
    private GeometryFactory geometryFactory;

    @Test
    public void assembleLongitude() throws Exception {
        double longitude = 10.75885366;

        SimplePointDto simplePointDto = pointAssembler.assemble(geometryFactory.createPoint(new Coordinate(longitude, 0.0)));

        assertThat(simplePointDto.location).isNotNull();
        assertThat(simplePointDto.location.longitude).isEqualTo(longitude);
    }

    @Test
    public void assembleSimplePointIsNull() {
        AssertionsForClassTypes.assertThat(pointAssembler.assemble(null)).isNull();
    }

}