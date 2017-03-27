package org.rutebanken.tiamat.repository;

import com.google.common.collect.Sets;
import org.junit.Test;
import org.rutebanken.tiamat.CommonSpringBootTest;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.versioning.VersionCreator;
import org.springframework.beans.factory.annotation.Autowired;

public class QuayRepositoryImplTest extends CommonSpringBootTest {

    @Autowired
    private QuayRepository quayRepository;

    @Autowired
    private VersionCreator versionCreator;

    @Test
    public void findByKeyValue() throws Exception {

        Quay version1 = new Quay();
        version1.getOrCreateValues("test").add("value");

        quayRepository.save(version1);

        Quay version2 = versionCreator.createNextVersion(version1, Quay.class);

        quayRepository.save(version2);

        quayRepository.flush();

        quayRepository.findByKeyValue("test", Sets.newHashSet("value"));

    }

}