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

package org.rutebanken.tiamat.importer;

import org.junit.Test;
import org.rutebanken.tiamat.importer.KeyValueListAppender;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.Value;

import static org.assertj.core.api.Assertions.assertThat;

public class KeyValueListAppenderTest {
    
    @Test
    public void twoValuesMustNotBeAddedTwice() {
        StopPlace newStopPlace = new StopPlace();
        newStopPlace.getKeyValues().put("key", new Value("value"));

        StopPlace existingStopPlace = new StopPlace();
        existingStopPlace.getKeyValues().put("key", new Value("value"));

        boolean changed = new KeyValueListAppender().appendToOriginalId("key", newStopPlace, existingStopPlace);
        assertThat(changed).isFalse();
        assertThat(existingStopPlace.getKeyValues().get("key").getItems()).hasSize(1);
    }


    @Test
    public void addNewValues() {
        StopPlace newStopPlace = new StopPlace();
        newStopPlace.getKeyValues().put("key", new Value("newValue"));

        StopPlace existingStopPlace = new StopPlace();
        existingStopPlace.getKeyValues().put("key", new Value("oldValue"));

        boolean changed = new KeyValueListAppender().appendToOriginalId("key", newStopPlace, existingStopPlace);
        assertThat(changed).isTrue();
        assertThat(existingStopPlace.getKeyValues().get("key").getItems()).hasSize(2);
    }

    @Test
    public void keepExistingValues() {
        StopPlace newStopPlace = new StopPlace();

        StopPlace existingStopPlace = new StopPlace();
        existingStopPlace.getKeyValues().put("key", new Value("oldValue"));

        boolean changed = new KeyValueListAppender().appendToOriginalId("key", newStopPlace, existingStopPlace);
        assertThat(changed).isFalse();
        assertThat(existingStopPlace.getKeyValues().get("key").getItems()).hasSize(1);
    }
}