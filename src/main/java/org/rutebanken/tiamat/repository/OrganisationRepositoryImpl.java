package org.rutebanken.tiamat.repository;

import org.apache.commons.lang3.NotImplementedException;

import java.util.Set;

public class OrganisationRepositoryImpl implements OrganisationRepositoryCustom {
    @Override
    public String findFirstByKeyValues(String key, Set<String> originalIds) {
        throw new NotImplementedException("findFirstByKeyValues not implemented for " + this.getClass().getSimpleName());
    }
}
