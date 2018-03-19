/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.importer.matching;

import org.junit.Test;
import org.rutebanken.tiamat.model.DataManagedObjectStructure;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.netex.id.ValidPrefixList;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;


public class OriginalIdMatcherTest {

    private NetexIdHelper netexIdHelper = new NetexIdHelper(new ValidPrefixList("NSR", new HashMap<>()));
    private final OriginalIdMatcher originalIdMatcher = new OriginalIdMatcher(netexIdHelper);

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