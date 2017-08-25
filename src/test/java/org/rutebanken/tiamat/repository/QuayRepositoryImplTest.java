package org.rutebanken.tiamat.repository;

import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.dtoassembling.dto.IdMappingDto;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.versioning.VersionCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.MERGED_ID_KEY;
import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.ORIGINAL_ID_KEY;

public class QuayRepositoryImplTest extends TiamatIntegrationTest {

    @Autowired
    private VersionCreator versionCreator;

    @Test
    public void findByKeyValue() throws Exception {

        Quay version1 = new Quay();
        version1.getOrCreateValues("test").add("value");
        version1.setVersion(1L);
        quayRepository.save(version1);

        Quay version2 = versionCreator.createCopy(version1, Quay.class);
        version2.setVersion(2L);

        quayRepository.save(version2);

        quayRepository.flush();

        quayRepository.findFirstByKeyValues("test", Sets.newHashSet("value"));

    }

    @Test
    public void findKeyValueMappingsForQuayReturnsQuayWithParentValidAtPointIntimeForImportedId() {

        String importedIdPosix = "187";
        String importedId = "XXX:Quay:" + importedIdPosix;
        Instant now = Instant.now();

        StopPlace childStop = new StopPlace();
        childStop.setVersion(1L);

        Quay quay = new Quay();
        quay.getOrCreateValues(ORIGINAL_ID_KEY).add(importedId);

        childStop.getQuays().add(quay);

        StopPlace parentStop = new StopPlace();
        parentStop.setParentStopPlace(true);
        parentStop.setVersion(2L);
        parentStop.setValidBetween(new ValidBetween(now.minusSeconds(11)));
        parentStop.getChildren().add(childStop);

        stopPlaceRepository.save(parentStop);

        childStop.setParentSiteRef(new SiteRefStructure(parentStop.getNetexId(), String.valueOf(parentStop.getVersion())));
        stopPlaceRepository.save(childStop);

        List<IdMappingDto> idMapping = quayRepository.findKeyValueMappingsForQuay(now, 0, 10);
        assertThat(idMapping).extracting(idMappingDto -> idMappingDto.netexId).contains(quay.getNetexId());
    }

    @Test
    public void findKeyValueMappingsForQuayReturnsNoQuayWithParentValidAtPointIntimeForImportedId() {

        String importedIdPosix = "104";
        String importedId = "YYY:Quay:" + importedIdPosix;
        Instant now = Instant.now();

        StopPlace childStop = new StopPlace();
        childStop.setVersion(1L);

        Quay quay = new Quay();
        quay.getOrCreateValues(ORIGINAL_ID_KEY).add(importedId);

        childStop.getQuays().add(quay);

        StopPlace notCurrentlyValidParentStop = new StopPlace();
        notCurrentlyValidParentStop.setParentStopPlace(true);
        notCurrentlyValidParentStop.setVersion(2L);
        notCurrentlyValidParentStop.setValidBetween(new ValidBetween(now.plusSeconds(11)));
        notCurrentlyValidParentStop.getChildren().add(childStop);

        stopPlaceRepository.save(notCurrentlyValidParentStop);

        childStop.setParentSiteRef(new SiteRefStructure(notCurrentlyValidParentStop.getNetexId(), String.valueOf(notCurrentlyValidParentStop.getVersion())));
        stopPlaceRepository.save(childStop);

        List<IdMappingDto> idMapping = quayRepository.findKeyValueMappingsForQuay(now, 0, 10);
        assertThat(idMapping).extracting(idMappingDto -> idMappingDto.netexId)
                .as("Quay " + quay.getNetexId() + " should not be returned in mapping as it belongs to a stop place with parent stop place wich is currently not valid")
                .doesNotContain(quay.getNetexId());
    }

    @Test
    public void findKeyValueMappingsForQuayReturnsOnlyQuaysValidAtPointInTimeForImportedId() {
        testFindKeyValueMappingsForQuayReturnsOnlyQuaysValidAtPointInTime(ORIGINAL_ID_KEY);
    }

    @Test
    public void findKeyValueMappingsForQuayReturnsOnlyQuaysValidAtPointInTimeForMergedId() {
        testFindKeyValueMappingsForQuayReturnsOnlyQuaysValidAtPointInTime(MERGED_ID_KEY);
    }

    public void testFindKeyValueMappingsForQuayReturnsOnlyQuaysValidAtPointInTime(String orgIdKey) {
        String orgIdSuffix = "2";
        String orgId = "XXX:Quay:" + orgIdSuffix;
        Instant now = Instant.now();

        StopPlace historicMatchingStopV1 = saveStop("NSR:StopPlace:1", 1l, now.minusSeconds(200), now.minusSeconds(10));
        Quay historicMatchingQuay = saveQuay(historicMatchingStopV1, "NSR:Quay:1", 1l, orgIdKey, orgId);

        StopPlace currentMatchingStop = saveStop("NSR:StopPlace:2", 1l, now.minusSeconds(10), null);
        Quay currentMatchingQuay = saveQuay(currentMatchingStop, "NSR:Quay:2", 1l, orgIdKey, orgId);

        List<IdMappingDto> currentMapping = quayRepository.findKeyValueMappingsForQuay(now, 0, 2000);
        Assert.assertEquals(1, currentMapping.size());
        Assert.assertEquals(orgId, currentMapping.get(0).originalId);
        Assert.assertEquals(currentMatchingQuay.getNetexId(), currentMapping.get(0).netexId);

        List<IdMappingDto> historicMapping = quayRepository.findKeyValueMappingsForQuay(now.minusSeconds(100), 0, 2000);
        Assert.assertEquals(1, historicMapping.size());
        Assert.assertEquals(orgId, historicMapping.get(0).originalId);
        Assert.assertEquals(historicMatchingQuay.getNetexId(), historicMapping.get(0).netexId);

        // No imported-ids or merged-ids are valid for point in time 300 seconds ago
        Assert.assertTrue(quayRepository.findKeyValueMappingsForQuay(now.minusSeconds(300), 0, 2000).isEmpty());
    }

    private StopPlace saveStop(String id, Long version, Instant startOfPeriod, Instant endOfPeriod) {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(version);
        stopPlace.setNetexId(id);
        stopPlace.setValidBetween(new ValidBetween(startOfPeriod, endOfPeriod));
        return stopPlaceRepository.save(stopPlace);
    }

    private Quay saveQuay(StopPlace stopPlace, String id, Long version, String orgIdKeyName, String orgId) {
        Quay quay = new Quay(new EmbeddableMultilingualString("Quay"));
        stopPlace.getQuays().add(quay);


        quay.setNetexId(id);
        quay.setVersion(version);
        quay.getKeyValues().put(orgIdKeyName, new Value(orgId));
        stopPlaceRepository.save(stopPlace);
        return quay;
    }
}