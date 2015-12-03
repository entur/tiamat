package no.rutebanken.tiamat.ifopt.transfer.disassembler;

import no.rutebanken.tiamat.ifopt.transfer.dto.LocationDTO;
import no.rutebanken.tiamat.ifopt.transfer.dto.SimplePointDTO;
import org.springframework.stereotype.Component;
import uk.org.netex.netex.Location;
import uk.org.netex.netex.LocationStructure;
import uk.org.netex.netex.SimplePoint_VersionStructure;

import java.math.BigDecimal;

@Component
public class SimplePointDisassembler {

    public SimplePoint_VersionStructure disassemble(SimplePoint_VersionStructure destination, SimplePointDTO simplePointDTO) {

        if(simplePointDTO.location == null) return null;

        LocationDTO locationDTO = simplePointDTO.location;

        if(destination.getLocation() == null) {
            destination.setLocation(new Location());
        }

        LocationStructure location = destination.getLocation();

        location.setLatitude(new BigDecimal(locationDTO.latitude));
        location.setLongitude(new BigDecimal(locationDTO.longitude));

        return destination;
    }
}
