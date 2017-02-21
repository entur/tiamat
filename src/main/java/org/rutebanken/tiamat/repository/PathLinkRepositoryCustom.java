package org.rutebanken.tiamat.repository;

import java.util.Set;

public interface PathLinkRepositoryCustom {

    Long findByKeyValue(String key, Set<String> values);
}
