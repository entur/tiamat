package no.rutebanken.tiamat.ifopt.transfer.assembler;

import no.rutebanken.tiamat.ifopt.transfer.dto.LocationDTO;
import no.rutebanken.tiamat.ifopt.transfer.dto.SimplePointDTO;
import org.springframework.stereotype.Component;
import uk.org.netex.netex.SimplePoint_VersionStructure;

@Component
public class SimplePointAssembler {

    public SimplePointDTO assemble(SimplePoint_VersionStructure simplePoint) {

        SimplePointDTO simplePointDTO = new SimplePointDTO();
        simplePointDTO.location = new LocationDTO();

        if (simplePoint.getLocation() != null) {
            simplePointDTO.location.latitude = simplePoint.getLocation().getLatitude().toPlainString();
            simplePointDTO.location.longitude = simplePoint.getLocation().getLongitude().toPlainString();
        }
        return simplePointDTO;
    }
}