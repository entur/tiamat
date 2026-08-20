package org.rutebanken.tiamat.importer.fetch;

import com.sun.net.httpserver.HttpServer;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rutebanken.netex.model.FareFrame;
import org.rutebanken.netex.model.FareZone;
import org.rutebanken.netex.model.FareZonesInFrame_RelStructure;
import org.rutebanken.netex.model.GroupOfTariffZones;
import org.rutebanken.netex.model.GroupsOfTariffZonesInFrame_RelStructure;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.StopPlacesInFrame_RelStructure;
import org.rutebanken.netex.model.TariffZoneRef;
import org.rutebanken.netex.model.TariffZoneRefs_RelStructure;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.auth.SystemSecurityContextService;
import org.rutebanken.tiamat.config.FareZonePollerConfig;
import org.rutebanken.tiamat.lock.PostgresAdvisoryLock;
import org.rutebanken.tiamat.repository.FareZonePollerStateRepository;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.PublicationDeliveryTestHelper;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.PublicationDeliveryUnmarshaller;
import org.rutebanken.tiamat.service.batch.BackgroundJobs;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.assertj.core.api.Assertions.assertThat;

public class FareZonePollerIntegrationTest extends TiamatIntegrationTest {

    @Autowired
    private PublicationDeliveryUnmarshaller unmarshaller;

    @Autowired
    private FareZoneSnapshotImporter fareZoneSnapshotImporter;

    @Autowired
    private SystemSecurityContextService systemSecurityContextService;

    @Autowired
    private PostgresAdvisoryLock postgresAdvisoryLock;

    @Autowired
    private BackgroundJobs backgroundJobs;

    @Autowired
    private FareZonePollerStateRepository fareZonePollerStateRepository;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PublicationDeliveryTestHelper publicationDeliveryTestHelper;

    private final ObjectFactory objectFactory = new ObjectFactory();

    private HttpServer server;
    private byte[] feedBytes;

    @Before
    public void startServer() throws Exception {
        fareZonePollerStateRepository.deleteAll();
        fareZonePollerStateRepository.flush();
        feedBytes = marshal(deliveryWithFareZones("NSR:FareZone:9001"));
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/feed", exchange -> {
            exchange.getResponseHeaders().add("Content-Type", "application/xml");
            exchange.sendResponseHeaders(200, feedBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(feedBytes);
            }
        });
        server.start();
    }

    @After
    public void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    public void pollsFetchesAndImports() {
        FareZonePoller poller = newPoller(feedUrl());

        assertThat(poller.pollOnce()).isEqualTo(FareZonePoller.PollResult.IMPORTED);
        assertThat(fareZoneRepository.findFirstByNetexIdOrderByVersionDesc("NSR:FareZone:9001")).isNotNull();
    }

    @Test
    public void unchangedFeedIsNotReimported() {
        FareZonePoller poller = newPoller(feedUrl());

        assertThat(poller.pollOnce()).isEqualTo(FareZonePoller.PollResult.IMPORTED);
        // Same body on the next tick: skipped by the recorded hash.
        assertThat(poller.pollOnce()).isEqualTo(FareZonePoller.PollResult.SKIPPED);
    }

    /**
     * The record of what was imported is shared, so a second replica does not redo the same import.
     * A Hazelcast map would be per-pod, since the replicas do not form a cluster.
     */
    @Test
    public void recordedHashIsSharedAcrossPollerInstances() {
        assertThat(newPoller(feedUrl()).pollOnce()).isEqualTo(FareZonePoller.PollResult.IMPORTED);

        assertThat(newPoller(feedUrl()).pollOnce()).isEqualTo(FareZonePoller.PollResult.SKIPPED);
    }

    @Test
    public void httpErrorYieldsFailedAndNoImport() {
        FareZonePoller poller = newPoller("http://127.0.0.1:" + server.getAddress().getPort() + "/missing");

        assertThat(poller.pollOnce()).isEqualTo(FareZonePoller.PollResult.FAILED);
        assertThat(fareZoneRepository.findFirstByNetexIdOrderByVersionDesc("NSR:FareZone:9001")).isNull();
    }

    /**
     * The lock is held by a separate database session, as another replica would hold it. It is taken
     * before the fetch, so losing the race means not pulling the feed at all.
     */
    @Test
    public void skipsWhenAnotherInstanceHoldsTheLock() throws Exception {
        FareZonePoller poller = newPoller(feedUrl());

        try (Connection otherSession = dataSource.getConnection()) {
            try (PreparedStatement lock = otherSession.prepareStatement("SELECT pg_advisory_lock(?)")) {
                lock.setLong(1, FareZonePoller.LOCK_KEY);
                lock.execute();
            }

            assertThat(poller.pollOnce()).isEqualTo(FareZonePoller.PollResult.SKIPPED);
            assertThat(fareZoneRepository.findFirstByNetexIdOrderByVersionDesc("NSR:FareZone:9001")).isNull();

            try (PreparedStatement unlock = otherSession.prepareStatement("SELECT pg_advisory_unlock(?)")) {
                unlock.setLong(1, FareZonePoller.LOCK_KEY);
                unlock.execute();
            }
        }

        // The lock is free again, so the next tick imports.
        assertThat(poller.pollOnce()).isEqualTo(FareZonePoller.PollResult.IMPORTED);
    }

    /**
     * The feed is external and the import runs with system privileges, so a delivery that reaches
     * beyond fare zones is rejected rather than applied to the rest of the register.
     */
    @Test
    public void rejectsFeedCarryingContentBeyondFareZones() throws Exception {
        PublicationDeliveryStructure delivery = deliveryWithFareZones("NSR:FareZone:9001");
        StopPlacesInFrame_RelStructure stopPlaces = new StopPlacesInFrame_RelStructure();
        stopPlaces.getStopPlace_().add(objectFactory.createStopPlace(new StopPlace()
                .withId("NSR:StopPlace:9001")
                .withVersion("1")
                .withName(new MultilingualString().withValue("Should not be imported"))));
        publicationDeliveryTestHelper.findSiteFrame(delivery).withStopPlaces(stopPlaces);
        feedBytes = marshal(delivery);

        assertThat(newPoller(feedUrl()).pollOnce()).isEqualTo(FareZonePoller.PollResult.FAILED);
        assertThat(fareZoneRepository.findFirstByNetexIdOrderByVersionDesc("NSR:FareZone:9001")).isNull();
        assertThat(stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc("NSR:StopPlace:9001")).isNull();
        assertThat(fareZoneSnapshotImporter.lastImportedHash()).isNull();
    }

    /**
     * A rejected snapshot must not leave anything behind, including the zones the importer had
     * already reached before the rejection, and must not be recorded as imported.
     */
    @Test
    public void rejectedSnapshotLeavesStoredZonesUntouched() throws Exception {
        assertThat(newPoller(feedUrl()).pollOnce()).isEqualTo(FareZonePoller.PollResult.IMPORTED);
        String hashOfGoodSnapshot = fareZoneSnapshotImporter.lastImportedHash();
        assertThat(hashOfGoodSnapshot).isNotNull();

        // A snapshot whose group references a zone it does not declare.
        PublicationDeliveryStructure delivery = deliveryWithFareZones("NSR:FareZone:9001", "NSR:FareZone:9002");
        publicationDeliveryTestHelper.findSiteFrame(delivery)
                .withGroupsOfTariffZones(new GroupsOfTariffZonesInFrame_RelStructure()
                        .withGroupOfTariffZones(groupOfTariffZones("NSR:GroupOfTariffZones:9001", "NSR:FareZone:9003")));
        feedBytes = marshal(delivery);

        assertThat(newPoller(feedUrl()).pollOnce()).isEqualTo(FareZonePoller.PollResult.FAILED);

        assertThat(fareZoneRepository.findFirstByNetexIdOrderByVersionDesc("NSR:FareZone:9001")).isNotNull();
        assertThat(fareZoneRepository.findFirstByNetexIdOrderByVersionDesc("NSR:FareZone:9002")).isNull();
        assertThat(groupOfTariffZonesRepository.findByNetexId("NSR:GroupOfTariffZones:9001")).isEmpty();
        // Still the previous snapshot, so the next tick retries rather than treating this as applied.
        assertThat(fareZoneSnapshotImporter.lastImportedHash()).isEqualTo(hashOfGoodSnapshot);
    }

    @Test
    public void appliesGroupsReferencingZonesInTheSameSnapshot() throws Exception {
        PublicationDeliveryStructure delivery = deliveryWithFareZones("NSR:FareZone:9001", "NSR:FareZone:9002");
        publicationDeliveryTestHelper.findSiteFrame(delivery)
                .withGroupsOfTariffZones(new GroupsOfTariffZonesInFrame_RelStructure()
                        .withGroupOfTariffZones(groupOfTariffZones(
                                "NSR:GroupOfTariffZones:9001", "NSR:FareZone:9001", "NSR:FareZone:9002")));
        feedBytes = marshal(delivery);

        assertThat(newPoller(feedUrl()).pollOnce()).isEqualTo(FareZonePoller.PollResult.IMPORTED);
        assertThat(groupOfTariffZonesRepository.findByNetexId("NSR:GroupOfTariffZones:9001")).isNotEmpty();
    }

    private FareZonePoller newPoller(String url) {
        FareZonePollerConfig config = new FareZonePollerConfig(true, url, 0, 3600, 30, 180);
        return new FareZonePoller(config, unmarshaller, fareZoneSnapshotImporter,
                systemSecurityContextService, postgresAdvisoryLock, backgroundJobs);
    }

    private String feedUrl() {
        return "http://127.0.0.1:" + server.getAddress().getPort() + "/feed";
    }

    private PublicationDeliveryStructure deliveryWithFareZones(String... fareZoneIds) {
        FareFrame fareFrame = publicationDeliveryTestHelper.fareFrame();
        fareFrame.setFareZones(new FareZonesInFrame_RelStructure());
        for (String id : fareZoneIds) {
            fareFrame.getFareZones().getFareZone().add(new FareZone()
                    .withId(id)
                    .withVersion("1")
                    .withName(new MultilingualString().withValue(id)));
        }

        return publicationDeliveryTestHelper.publicationDelivery(publicationDeliveryTestHelper.siteFrame(), fareFrame);
    }

    private GroupOfTariffZones groupOfTariffZones(String groupId, String... memberRefs) {
        TariffZoneRefs_RelStructure members = new TariffZoneRefs_RelStructure();
        for (String ref : memberRefs) {
            members.getTariffZoneRef_().add(objectFactory.createTariffZoneRef(new TariffZoneRef().withRef(ref)));
        }
        return new GroupOfTariffZones()
                .withId(groupId)
                .withVersion("1")
                .withName(new MultilingualString().withValue(groupId))
                .withMembers(members);
    }

    private byte[] marshal(PublicationDeliveryStructure delivery) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance("org.rutebanken.netex.model");
        Marshaller marshaller = jaxbContext.createMarshaller();
        JAXBElement<PublicationDeliveryStructure> element = new ObjectFactory().createPublicationDelivery(delivery);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        marshaller.marshal(element, outputStream);
        return outputStream.toByteArray();
    }
}
