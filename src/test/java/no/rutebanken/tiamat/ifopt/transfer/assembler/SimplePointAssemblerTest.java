package no.rutebanken.tiamat.ifopt.transfer.assembler;

import no.rutebanken.tiamat.TiamatApplication;
import no.rutebanken.tiamat.ifopt.transfer.dto.SimplePointDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.org.netex.netex.Location;
import uk.org.netex.netex.SimplePoint_VersionStructure;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TiamatApplication.class)
public class SimplePointAssemblerTest {

    @Autowired
    private SimplePointAssembler simplePointAssembler;

    @Test
    public void assmebleLongitudeWithEightDecimalPlaces() throws Exception {
        String longitudeString = "10.75885366";

        BigDecimal longitude = new BigDecimal(longitudeString).setScale(10, RoundingMode.HALF_UP);

        SimplePoint_VersionStructure simplePoint = new SimplePoint_VersionStructure();

        Location location = new Location();
        location.setLongitude(longitude);
        location.setLatitude(BigDecimal.ZERO);

        simplePoint.setLocation(location);

        SimplePointDTO simplePointDTO = simplePointAssembler.assemble(simplePoint);

        assertThat(simplePointDTO.location).isNotNull();
        assertThat(simplePointDTO.location.longitude).isEqualTo(longitudeString);
    }
}