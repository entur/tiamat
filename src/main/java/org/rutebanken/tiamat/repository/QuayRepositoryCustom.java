package org.rutebanken.tiamat.repository;

import org.rutebanken.tiamat.dtoassembling.dto.IdMappingDto;
import org.rutebanken.tiamat.dtoassembling.dto.JbvCodeMappingDto;
import org.rutebanken.tiamat.model.Quay;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

public interface QuayRepositoryCustom extends DataManagedObjectStructureRepository<Quay> {

    List<IdMappingDto> findKeyValueMappingsForQuay(Instant pointInTime, int recordPosition, int recordsPerRoundTrip);

    List<JbvCodeMappingDto> findJbvCodeMappingsForQuay();

}
