package org.rutebanken.tiamat.rest.dto;

import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Path;

@Deprecated
@Path("quay/jbv_code_mapping")
public class LegacyDtoJbvCodeMappingResource extends DtoJbvCodeMappingResource {

    @Autowired
    public LegacyDtoJbvCodeMappingResource(QuayRepository quayRepository, StopPlaceRepository stopPlaceRepository) {
        super(quayRepository, stopPlaceRepository);
    }
}
