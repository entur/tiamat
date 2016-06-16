package no.rutebanken.tiamat.ifopt.transfer.assembler;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import no.rutebanken.tiamat.TiamatApplication;
import no.rutebanken.tiamat.ifopt.transfer.dto.SimplePointDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import no.rutebanken.tiamat.model.LocationStructure;
import no.rutebanken.tiamat.model.SimplePoint;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TiamatApplication.class)
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
        assertThat(simplePointAssembler.assemble(null)).isNull();
    }

}