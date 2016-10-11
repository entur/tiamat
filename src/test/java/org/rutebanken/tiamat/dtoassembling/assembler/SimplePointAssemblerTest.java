package org.rutebanken.tiamat.dtoassembling.assembler;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.rutebanken.tiamat.TiamatApplication;
import org.rutebanken.tiamat.dtoassembling.dto.SimplePointDto;
import org.rutebanken.tiamat.model.LocationStructure;
import org.rutebanken.tiamat.model.SimplePoint;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TiamatApplication.class)
public class SimplePointAssemblerTest {

    @Autowired
    private SimplePointAssembler simplePointAssembler;

    @Autowired
    private GeometryFactory geometryFactory;

    @Test
    public void assembleLongitude() throws Exception {
        double longitude = 10.75885366;

        SimplePoint simplePoint = new SimplePoint();

        simplePoint.setLocation(new LocationStructure(geometryFactory.createPoint(new Coordinate(longitude, 0.0))));

        SimplePointDto simplePointDto = simplePointAssembler.assemble(simplePoint);

        assertThat(simplePointDto.location).isNotNull();
        assertThat(simplePointDto.location.longitude).isEqualTo(longitude);
    }

    @Test
    public void assembleSimplePointIsNull() {
        AssertionsForClassTypes.assertThat(simplePointAssembler.assemble(null)).isNull();
    }

}