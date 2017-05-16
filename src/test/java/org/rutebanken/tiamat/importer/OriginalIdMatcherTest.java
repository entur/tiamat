package org.rutebanken.tiamat.importer;

import org.junit.Test;
import org.rutebanken.tiamat.model.DataManagedObjectStructure;
import org.rutebanken.tiamat.model.Quay;

import static org.assertj.core.api.Assertions.assertThat;


public class OriginalIdMatcherTest {

    private final OriginalIdMatcher originalIdMatcher = new OriginalIdMatcher();

    @Test
    public void matchesOnOriginalId() throws Exception {
        DataManagedObjectStructure dataManagedObject = new Quay();
        dataManagedObject.getOriginalIds().add("RUT:Quay:0124");

        DataManagedObjectStructure otherDataManagedObject = new Quay();
        otherDataManagedObject.getOriginalIds().add("BRA:Quay:124");

        assertThat(originalIdMatcher.matchesOnOriginalId(dataManagedObject, otherDataManagedObject)).isTrue();
    }

    @Test
    public void handleLongValues() throws Exception {
        DataManagedObjectStructure dataManagedObject = new Quay();
        dataManagedObject.getOriginalIds().add("RUT:Quay:0124000000000000");

        DataManagedObjectStructure otherDataManagedObject = new Quay();
        otherDataManagedObject.getOriginalIds().add("BRA:Quay:124000000000000");

        assertThat(originalIdMatcher.matchesOnOriginalId(dataManagedObject, otherDataManagedObject)).isTrue();
    }
}