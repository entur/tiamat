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

package org.rutebanken.tiamat.netex.mapping.mapper;

import ma.glasnost.orika.MappingContext;
import org.junit.Test;
import org.rutebanken.netex.model.KeyListStructure;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.netex.id.ValidPrefixList;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;
import org.rutebanken.tiamat.repository.TagRepository;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.rutebanken.tiamat.netex.mapping.mapper.DataManagedObjectStructureMapper.CHANGED_BY;
import static org.rutebanken.tiamat.netex.mapping.mapper.DataManagedObjectStructureMapper.VERSION_COMMENT;


public class DataManagedObjectStructureMapperTest {

    private TagRepository tagRepository = mock(TagRepository.class);
    private TagKeyValuesMapper tagKeyValuesMapper = new TagKeyValuesMapper(tagRepository);

    private ValidPrefixList validPrefixList = new ValidPrefixList("NSR", new HashMap<>());
    private NetexIdHelper netexIdHelper = new NetexIdHelper(validPrefixList);

    private DataManagedObjectStructureMapper dataManagedObjectStructureMapper = new DataManagedObjectStructureMapper(tagRepository, new NetexIdMapper(validPrefixList, netexIdHelper), tagKeyValuesMapper);

    private PublicationDeliveryHelper publicationDeliveryHelper = new PublicationDeliveryHelper();

    private MappingContext mappingContext = new MappingContext(new HashMap<>());

    @Test
    public void mappingChangedBToNetex() {

        StopPlace tiamatStopPlace = new StopPlace();
        tiamatStopPlace.setChangedBy("me");

        org.rutebanken.netex.model.StopPlace netexStopPlace = new org.rutebanken.netex.model.StopPlace();
        dataManagedObjectStructureMapper.mapBtoA(tiamatStopPlace, netexStopPlace, mappingContext);

        assertThat(publicationDeliveryHelper.getValueByKey(netexStopPlace, CHANGED_BY)).isNull();
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