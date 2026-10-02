package org.rutebanken.tiamat.service.farezone;

import org.junit.Test;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.tiamat.config.FareZoneConfig;
import org.rutebanken.tiamat.importer.PublicationDeliveryFareFrameImporter;
import org.rutebanken.tiamat.lock.TimeoutMaxLeaseTimeLock;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.PublicationDeliveryUnmarshaller;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class FareZoneMembershipArtifactImporterTest {

    /** Shaped like farezone-resolver's register artifact: a FareFrame of fare zones. */
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
    public void unmarshalsAndHandsTheDeliveryToTheFareFrameImporter() throws Exception {
        PublicationDeliveryFareFrameImporter fareFrameImporter = mock(PublicationDeliveryFareFrameImporter.class);
        FareZoneMembershipArtifactImporter importer = new FareZoneMembershipArtifactImporter(
                new PublicationDeliveryUnmarshaller(), fareFrameImporter, new FareZoneConfig(),
                mock(TimeoutMaxLeaseTimeLock.class),
                "", "_stops_farezones.xml", "", "", 60, 43200);

        importer.importFrom(new ByteArrayInputStream(ARTIFACT.getBytes(StandardCharsets.UTF_8)));

        verify(fareFrameImporter).importPublicationDelivery(any(PublicationDeliveryStructure.class));
    }

    @Test
    public void isInertWithoutABucket() throws Exception {
        FareZoneMembershipArtifactImporter importer = new FareZoneMembershipArtifactImporter(
                new PublicationDeliveryUnmarshaller(), mock(PublicationDeliveryFareFrameImporter.class), new FareZoneConfig(),
                mock(TimeoutMaxLeaseTimeLock.class),
                "", "_stops_farezones.xml", "", "", 60, 43200);
        assertThat(importer.isConfigured()).isFalse();
    }
}
