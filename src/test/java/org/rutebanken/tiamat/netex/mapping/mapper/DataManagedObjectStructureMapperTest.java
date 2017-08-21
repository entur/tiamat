package org.rutebanken.tiamat.netex.mapping.mapper;

import ma.glasnost.orika.MappingContext;
import org.junit.Test;
import org.rutebanken.netex.model.KeyListStructure;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.rutebanken.tiamat.netex.mapping.mapper.DataManagedObjectStructureMapper.CHANGED_BY;
import static org.rutebanken.tiamat.netex.mapping.mapper.DataManagedObjectStructureMapper.VERSION_COMMENT;


public class DataManagedObjectStructureMapperTest {

    private DataManagedObjectStructureMapper dataManagedObjectStructureMapper = new DataManagedObjectStructureMapper(new NetexIdMapper());

    private PublicationDeliveryHelper publicationDeliveryHelper = new PublicationDeliveryHelper();

    private MappingContext mappingContext = new MappingContext(new HashMap<>());

    @Test
    public void mappingChangedBToNetex() {

        StopPlace tiamatStopPlace = new StopPlace();
        tiamatStopPlace.setChangedBy("me");

        org.rutebanken.netex.model.StopPlace netexStopPlace = new org.rutebanken.netex.model.StopPlace();
        dataManagedObjectStructureMapper.mapBtoA(tiamatStopPlace, netexStopPlace, mappingContext);

        assertThat(publicationDeliveryHelper.getValueByKey(netexStopPlace, CHANGED_BY)).isEqualTo(tiamatStopPlace.getChangedBy());
    }

    @Test
    public void mappingChangedByFromNetex() {

        String changedBy = "someone";

        org.rutebanken.netex.model.StopPlace netexStopPlace = new org.rutebanken.netex.model.StopPlace();
        netexStopPlace.withKeyList(new KeyListStructure()
                .withKeyValue(new KeyValueStructure()
                        .withKey(CHANGED_BY)
                        .withValue(changedBy)));

        StopPlace tiamatStopPlace = new StopPlace();

        dataManagedObjectStructureMapper.mapAtoB(netexStopPlace, tiamatStopPlace, mappingContext);

        assertThat(tiamatStopPlace.getChangedBy()).isEqualTo(changedBy);
    }

    @Test
    public void mappingVersionCommentToNetex() {

        StopPlace tiamatStopPlace = new StopPlace();
        tiamatStopPlace.setVersionComment("good changes");

        org.rutebanken.netex.model.StopPlace netexStopPlace = new org.rutebanken.netex.model.StopPlace();
        dataManagedObjectStructureMapper.mapBtoA(tiamatStopPlace, netexStopPlace, mappingContext);

        assertThat(publicationDeliveryHelper.getValueByKey(netexStopPlace, VERSION_COMMENT)).isEqualTo(tiamatStopPlace.getVersionComment());
    }

    @Test
    public void mappingVersionCommentFromNetex() {

        String comment = "some change";

        org.rutebanken.netex.model.StopPlace netexStopPlace = new org.rutebanken.netex.model.StopPlace();
        netexStopPlace.withKeyList(new KeyListStructure()
                .withKeyValue(new KeyValueStructure()
                        .withKey(VERSION_COMMENT)
                        .withValue(comment)));

        StopPlace tiamatStopPlace = new StopPlace();

        dataManagedObjectStructureMapper.mapAtoB(netexStopPlace, tiamatStopPlace, mappingContext);

        assertThat(tiamatStopPlace.getVersionComment()).isEqualTo(comment);
    }
}