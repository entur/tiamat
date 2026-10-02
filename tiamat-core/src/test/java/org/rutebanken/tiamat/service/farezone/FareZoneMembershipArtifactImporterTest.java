package org.rutebanken.tiamat.service.farezone;

import org.junit.Test;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.tiamat.config.FareZoneConfig;
import org.rutebanken.tiamat.importer.PublicationDeliveryFareFrameImporter;
import org.rutebanken.tiamat.lock.TimeoutMaxLeaseTimeLock;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.PublicationDeliveryUnmarshaller;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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

    @Test
    public void handsTheDeliveryToTheFareFrameImporterAsARegisterReplica() throws Exception {
        PublicationDeliveryFareFrameImporter fareFrameImporter = mock(PublicationDeliveryFareFrameImporter.class);
        FareZoneMembershipArtifactImporter importer = importer(fareFrameImporter, registerImportEnabled(), "");

        importer.importFrom(new ByteArrayInputStream(ARTIFACT.getBytes(StandardCharsets.UTF_8)));

        verify(fareFrameImporter).importPublicationDelivery(any(PublicationDeliveryStructure.class),
                argThat(params -> params.fareZoneRegisterReplica));
    }

    @Test
    public void isInertWithoutABucket() throws Exception {
        FareZoneMembershipArtifactImporter importer = importer(
                mock(PublicationDeliveryFareFrameImporter.class), registerImportEnabled(), "");

        importer.scheduleImport();

        assertThat(ReflectionTestUtils.getField(importer, "executor")).isNull();
    }

    @Test
    public void schedulesWhenEnabledWithBucket() throws Exception {
        FareZoneMembershipArtifactImporter importer = importer(
                mock(PublicationDeliveryFareFrameImporter.class), registerImportEnabled(), "bucket");

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

    private static FareZoneMembershipArtifactImporter importer(PublicationDeliveryFareFrameImporter fareFrameImporter,
                                                               FareZoneConfig config,
                                                               String bucket) throws Exception {
        return new FareZoneMembershipArtifactImporter(
                new PublicationDeliveryUnmarshaller(), fareFrameImporter, config,
                mock(TimeoutMaxLeaseTimeLock.class),
                bucket, "_stops_farezones.xml", "", "", 3600, 43200);
    }
}
