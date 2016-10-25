package org.rutebanken.tiamat.importers;

import org.junit.Test;
import org.rutebanken.tiamat.model.KeyListStructure;
import org.rutebanken.tiamat.model.KeyValueStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netexmapping.NetexIdMapper;

import static org.assertj.core.api.Assertions.assertThat;


public class KeyValueAppenderTest {

    private KeyValueAppender keyValueAppender = new KeyValueAppender();

    @Test
    public void appendNewToExisting() {
        StopPlace existingStopPlace = new StopPlace();
        addId("OPP:StopArea:12345", existingStopPlace);
        StopPlace newStopPlace = new StopPlace();
        addId("HED:StopArea:4321", newStopPlace);

        keyValueAppender.appendToOriginalId(NetexIdMapper.ORIGINAL_ID_KEY, newStopPlace, existingStopPlace);

        assertThat(existingStopPlace.getKeyList().getKeyValue().get(0).getValue()).isEqualTo("OPP:StopArea:12345,HED:StopArea:4321");
    }

    @Test
    public void appendNewWithoutExisting() {
        StopPlace existingStopPlace = new StopPlace();
        StopPlace newStopPlace = new StopPlace();
        addId("HED:StopArea:4321", newStopPlace);

        keyValueAppender.appendToOriginalId(NetexIdMapper.ORIGINAL_ID_KEY, newStopPlace, existingStopPlace);

        assertThat(existingStopPlace.getKeyList().getKeyValue().get(0).getValue()).isEqualTo("HED:StopArea:4321");
    }

    @Test
    public void noIdsReturnsEmptyList() {
        StopPlace existingStopPlace = new StopPlace();
        StopPlace newStopPlace = new StopPlace();

        keyValueAppender.appendToOriginalId(NetexIdMapper.ORIGINAL_ID_KEY, newStopPlace, existingStopPlace);

        assertThat(existingStopPlace.getKeyList().getKeyValue().get(0).getValue()).isEqualTo("");
    }


    private void addId(String value, StopPlace stopPlace) {
        stopPlace.setKeyList(new KeyListStructure());
        stopPlace.getKeyList().getKeyValue().add(new KeyValueStructure(NetexIdMapper.ORIGINAL_ID_KEY, value));
    }

}