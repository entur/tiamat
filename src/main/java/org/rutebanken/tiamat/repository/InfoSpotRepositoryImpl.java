package org.rutebanken.tiamat.repository;

import java.util.Set;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class InfoSpotRepositoryImpl implements InfoSpotRepositoryCustom {

    @Override
    public String findFirstByKeyValues(String key, Set<String> originalIds) {
        throw new NotImplementedException("findFirstByKeyValues not implemented for " + this.getClass().getSimpleName());
    }

}
