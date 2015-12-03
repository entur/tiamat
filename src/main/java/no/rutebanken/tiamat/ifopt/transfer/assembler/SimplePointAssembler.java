package no.rutebanken.tiamat.ifopt.transfer.assembler;

import no.rutebanken.tiamat.ifopt.transfer.dto.LocationDTO;
import no.rutebanken.tiamat.ifopt.transfer.dto.SimplePointDTO;
import org.springframework.stereotype.Component;
import uk.org.netex.netex.SimplePoint_VersionStructure;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class SimplePointAssembler {

    private static final int DECIMAL_PLACES = 8;

    public SimplePointDTO assemble(SimplePoint_VersionStructure simplePoint) {

        SimplePointDTO simplePointDTO = new SimplePointDTO();
        simplePointDTO.location = new LocationDTO();

        if (simplePoint.getLocation() != null) {
            simplePointDTO.location.latitude = withEightDecimalPlaces(simplePoint.getLocation().getLatitude());
            simplePointDTO.location.longitude = withEightDecimalPlaces(simplePoint.getLocation().getLongitude());
        }
        return simplePointDTO;
    }

    public String withEightDecimalPlaces(BigDecimal coordinate) {
        if(coordinate != null) {
            return coordinate.setScale(DECIMAL_PLACES, RoundingMode.HALF_UP).toPlainString();
        }
        return null;
    }
}