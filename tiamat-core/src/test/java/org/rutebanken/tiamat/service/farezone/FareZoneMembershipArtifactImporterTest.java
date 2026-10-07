package org.rutebanken.tiamat.service.farezone;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.junit.Test;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.tiamat.config.FareZoneConfig;
import org.rutebanken.tiamat.importer.PublicationDeliveryFareFrameImporter;
import org.rutebanken.tiamat.lock.TimeoutMaxLeaseTimeLock;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;
import org.rutebanken.tiamat.repository.FareZoneRepository;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.PublicationDeliveryUnmarshaller;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class FareZoneMembershipArtifactImporterTest {

    /** Shaped like the register artifact: a FareFrame of fare zones. */
    private static final String ARTIFACT = """
            <?xml version="1.0" encoding="UTF-8"?>
            <PublicationDelivery xmlns="http://www.netex.org.uk/netex" version="1.15:NO-NeTEx-fareZoneMembership:1.0">
                <PublicationTimestamp>2026-01-01T00:00:00</PublicationTimestamp>
                <ParticipantRef>NSR</ParticipantRef>
                <dataObjects>
                    <FareFrame version="1" id="NSR:FareFrame:FareZoneMembership">
                        <fareZones>
                            <FareZone version="1" id="NSR:FareZone:1"><Name>Z1</Name></FareZone>
                        </fareZones>
                    </FareFrame>
                </dataObjects>
            </PublicationDelivery>
            """;

    private final PublicationDeliveryFareFrameImporter fareFrameImporter = mock(PublicationDeliveryFareFrameImporter.class);
    private final FareZoneRepository fareZoneRepository = mock(FareZoneRepository.class);
    @SuppressWarnings("unchecked")
    private final IMap<String, String> state = mock(IMap.class);

    @Test
    public void handsTheDeliveryToTheFareFrameImporterAsARegisterReplica() throws Exception {
        FareZoneMembershipArtifactImporter importer = importer(registerImportEnabled(), "");

        importer.importFrom(artifact());

        verify(fareFrameImporter).importPublicationDelivery(any(PublicationDeliveryStructure.class),
                argThat(params -> params.fareZoneRegisterReplica));
    }

    @Test
    public void skipsAnArtifactWithTheLastImportedMd5() throws Exception {
        when(state.get(FareZoneMembershipArtifactImporter.LAST_IMPORTED_MD5)).thenReturn("md5-1");

        importer(registerImportEnabled(), "bucket").importArtifact(storageWithArtifact("md5-1"));

        verifyNoInteractions(fareFrameImporter);
    }

    @Test
    public void importsAChangedArtifactAndRecordsItsMd5() throws Exception {
        when(state.get(FareZoneMembershipArtifactImporter.LAST_IMPORTED_MD5)).thenReturn("md5-1");

        importer(registerImportEnabled(), "bucket").importArtifact(storageWithArtifact("md5-2"));

        verify(fareFrameImporter).importPublicationDelivery(any(), any());
        verify(state).put(FareZoneMembershipArtifactImporter.LAST_IMPORTED_MD5, "md5-2");
    }

    /** A truncated artifact must not prune most of the register. */
    @Test
    public void refusesAnArtifactThatDeletesTooManyZones() throws Exception {
        when(fareZoneRepository.findAllNetexIds()).thenReturn(Set.of("NSR:FareZone:1", "NSR:FareZone:2", "NSR:FareZone:3"));
        FareZoneMembershipArtifactImporter importer = importer(registerImportEnabled(), "bucket");

        assertThatThrownBy(() -> importer.importArtifact(storageWithArtifact("md5-2")))
                .isInstanceOf(IllegalStateException.class);

        verifyNoInteractions(fareFrameImporter);
        verify(state, never()).put(any(), any());
    }

    @Test
    public void importsAnArtifactThatDeletesWithinTheLimit() throws Exception {
        when(fareZoneRepository.findAllNetexIds()).thenReturn(Set.of(
                "NSR:FareZone:1", "NSR:FareZone:2", "NSR:FareZone:3", "NSR:FareZone:4", "NSR:FareZone:5",
                "NSR:FareZone:6", "NSR:FareZone:7", "NSR:FareZone:8", "NSR:FareZone:9", "NSR:FareZone:10"));
        FareZoneMembershipArtifactImporter importer = importer(registerImportEnabled(), "", 0.9);

        importer.importFrom(artifact());

        verify(fareFrameImporter).importPublicationDelivery(any(), any());
    }

    @Test
    public void isInertWithoutABucket() throws Exception {
        FareZoneMembershipArtifactImporter importer = importer(registerImportEnabled(), "");

        importer.scheduleImport();

        assertThat(ReflectionTestUtils.getField(importer, "executor")).isNull();
    }

    @Test
    public void schedulesWhenEnabledWithBucket() throws Exception {
        FareZoneMembershipArtifactImporter importer = importer(registerImportEnabled(), "bucket");

        importer.scheduleImport();
        try {
            assertThat(ReflectionTestUtils.getField(importer, "executor")).isNotNull();
        } finally {
            importer.shutdown();
        }
    }

    private static FareZoneConfig registerImportEnabled() {
        FareZoneConfig config = new FareZoneConfig();
        ReflectionTestUtils.setField(config, "registerImportEnabled", true);
        return config;
    }

    private static ByteArrayInputStream artifact() {
        return new ByteArrayInputStream(ARTIFACT.getBytes(StandardCharsets.UTF_8));
    }

    private static Storage storageWithArtifact(String md5) {
        Blob blob = mock(Blob.class);
        when(blob.getMd5()).thenReturn(md5);
        when(blob.getContent()).thenReturn(ARTIFACT.getBytes(StandardCharsets.UTF_8));
        Storage storage = mock(Storage.class);
        when(storage.get(BlobId.of("bucket", "_stops_farezones.xml"))).thenReturn(blob);
        return storage;
    }

    private FareZoneMembershipArtifactImporter importer(FareZoneConfig config, String bucket) throws Exception {
        return importer(config, bucket, 0.1);
    }

    private FareZoneMembershipArtifactImporter importer(FareZoneConfig config, String bucket,
                                                        double maxDeleteFraction) throws Exception {
        HazelcastInstance hazelcastInstance = mock(HazelcastInstance.class);
        when(hazelcastInstance.<String, String>getMap(FareZoneMembershipArtifactImporter.STATE_MAP)).thenReturn(state);
        return new FareZoneMembershipArtifactImporter(
                new PublicationDeliveryUnmarshaller(), fareFrameImporter, config,
                mock(TimeoutMaxLeaseTimeLock.class), hazelcastInstance, new PublicationDeliveryHelper(), fareZoneRepository,
                bucket, "_stops_farezones.xml", "", "", 3600, 43200, maxDeleteFraction);
    }
}
