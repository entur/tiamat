package org.rutebanken.tiamat.repository;

import org.rutebanken.tiamat.model.DataManagedObjectStructure;

import java.util.Set;

public interface DataManagedObjectStructureRepository<T extends DataManagedObjectStructure> {

    String findByKeyValue(String key, Set<String> originalIds);
}
