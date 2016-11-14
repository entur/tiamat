package org.rutebanken.tiamat.dtoassembling.assembler;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.rutebanken.tiamat.TiamatApplication;
import org.rutebanken.tiamat.dtoassembling.dto.SimplePointDto;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TiamatApplication.class)
public class LocationStructureAssemblerTest {

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