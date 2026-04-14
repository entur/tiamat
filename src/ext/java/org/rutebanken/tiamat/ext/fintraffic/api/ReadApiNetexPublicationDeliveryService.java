package org.rutebanken.tiamat.ext.fintraffic.api;

import org.rutebanken.tiamat.ext.fintraffic.api.model.ReadApiEntityOutRecord;
import org.rutebanken.tiamat.ext.fintraffic.api.model.ReadApiSearchKey;
import org.rutebanken.tiamat.ext.fintraffic.api.repository.NetexRepository;
import org.rutebanken.tiamat.netex.id.ValidPrefixList;
import org.rutebanken.tiamat.time.ExportTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;

public class ReadApiNetexPublicationDeliveryService {
    // Predefined string constants for collection tags
    private static final String COLLECTION_TAG_SCHEDULED_STOP_POINTS = "<scheduledStopPoints/>";
    private static final String COLLECTION_TAG_STOP_ASSIGNMENTS = "<stopAssignments/>";
    private static final String COLLECTION_TAG_STOP_PLACES = "<stopPlaces/>";
    private static final String COLLECTION_TAG_PARKINGS = "<parkings/>";
    private static final byte[] START_TAG_SCHEDULED_STOP_POINTS = "<scheduledStopPoints>".getBytes(StandardCharsets.UTF_8);
    private static final byte[] START_TAG_STOP_ASSIGNMENTS = "<stopAssignments>".getBytes(StandardCharsets.UTF_8);
    private static final byte[] START_TAG_STOP_PLACES = "<stopPlaces>".getBytes(StandardCharsets.UTF_8);
    private static final byte[] START_TAG_PARKINGS = "<parkings>".getBytes(StandardCharsets.UTF_8);
    private static final byte[] END_TAG_SCHEDULED_STOP_POINTS = "</scheduledStopPoints>".getBytes(StandardCharsets.UTF_8);
    private static final byte[] END_TAG_STOP_ASSIGNMENTS = "</stopAssignments>".getBytes(StandardCharsets.UTF_8);
    private static final byte[] END_TAG_STOP_PLACES = "</stopPlaces>".getBytes(StandardCharsets.UTF_8);
    private static final byte[] END_TAG_PARKINGS = "</parkings>".getBytes(StandardCharsets.UTF_8);
    private static final byte[] NEWLINE = "\n".getBytes(StandardCharsets.UTF_8);

    private static final String TIMESTAMP_PLACEHOLDER = "\\{timeStamp}";
    private static final String PREFIX_PLACEHOLDER = "\\{prefix}";
    private static final String EPOCH_SECONDS_PLACEHOLDER = "\\{epochSeconds}";
    private static final String TIMEZONE_PLACEHOLDER = "\\{timeZone}";
    private static final String DEFAULT_LANGUAGE_PLACEHOLDER = "\\{defaultLanguage}";
    private static final String PUBLICATION_DELIVERY_ID_PLACEHOLDER = "\\{publicationDeliveryId}";

    private static final String PUBLICATION_DELIVERY_TEMPLATE = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <PublicationDelivery xmlns="http://www.netex.org.uk/netex" xmlns:ns2="http://www.opengis.net/gml/3.2" xmlns:ns3="http://www.siri.org.uk/siri" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="{publicationDeliveryId}" xsi:schemaLocation="">
                <PublicationTimestamp>{timeStamp}</PublicationTimestamp>
                <ParticipantRef>{prefix}</ParticipantRef>
                <dataObjects>
                    <ServiceFrame modification="new" version="1" id="{prefix}:ServiceFrame:{epochSeconds}">
                        <scheduledStopPoints/>
                        <stopAssignments/>
                    </ServiceFrame>
                    <SiteFrame modification="new" version="1" id="{prefix}:SiteFrame:{epochSeconds}">
                        <FrameDefaults>
                            <DefaultLocale>
                                <TimeZone>{timeZone}</TimeZone>
                                <DefaultLanguage>{defaultLanguage}</DefaultLanguage>
                            </DefaultLocale>
                        </FrameDefaults>
                        <stopPlaces/>
                        <parkings/>
                    </SiteFrame>
                </dataObjects>
            </PublicationDelivery>""";

    private final NetexRepository netexRepository;
    private final ValidPrefixList prefixList;
    private final ExportTimeZone exportTimeZone;
    private final String defaultLanguage;
    private final String publicationDeliveryId;

    private final Logger logger = LoggerFactory.getLogger(ReadApiNetexPublicationDeliveryService.class);


    public ReadApiNetexPublicationDeliveryService(
            NetexRepository netexRepository,
            ValidPrefixList validPrefixList,
            ExportTimeZone exportTimeZone,
            @Value("${tiamat.locals.language.default:nor}") String defaultLanguage,
            @Value("${netex.profile.version:1.12:NO-NeTEx-stops:1.4}") String publicationDeliveryId
    ) {
        this.netexRepository = netexRepository;
        this.prefixList = validPrefixList;
        this.exportTimeZone = exportTimeZone;
        this.defaultLanguage = defaultLanguage;
        this.publicationDeliveryId = publicationDeliveryId;
    }

    private String getPublicationDeliveryTemplate() {
        return PUBLICATION_DELIVERY_TEMPLATE
                .replaceAll(TIMESTAMP_PLACEHOLDER, LocalDateTime.now().toString())
                .replaceAll(PREFIX_PLACEHOLDER, prefixList.getValidNetexPrefix())
                .replaceAll(TIMEZONE_PLACEHOLDER, exportTimeZone.getDefaultTimeZoneId().toString())
                .replaceAll(EPOCH_SECONDS_PLACEHOLDER, String.valueOf(Instant.now().getEpochSecond()))
                .replaceAll(PUBLICATION_DELIVERY_ID_PLACEHOLDER, publicationDeliveryId)
                .replaceAll(DEFAULT_LANGUAGE_PLACEHOLDER, defaultLanguage);
    }

    private static String getCollectionTag(String type) {
        return switch (type) {
            case "ScheduledStopPoint" -> COLLECTION_TAG_SCHEDULED_STOP_POINTS;
            case "PassengerStopAssignment" -> COLLECTION_TAG_STOP_ASSIGNMENTS;
            case "StopPlace" -> COLLECTION_TAG_STOP_PLACES;
            case "Parking" -> COLLECTION_TAG_PARKINGS;
            default -> throw new IllegalArgumentException("Unsupported type for collection tag: " + type);
        };
    }

    private static byte[] getCollectionStartTag(String type) {
        return switch (type) {
            case "ScheduledStopPoint" -> START_TAG_SCHEDULED_STOP_POINTS;
            case "PassengerStopAssignment" -> START_TAG_STOP_ASSIGNMENTS;
            case "StopPlace" -> START_TAG_STOP_PLACES;
            case "Parking" -> START_TAG_PARKINGS;
            default -> throw new IllegalArgumentException("Unsupported type for collection start tag: " + type);
        };
    }

    private static byte[] getCollectionEndTag(String type) {
        return switch (type) {
            case "ScheduledStopPoint" -> END_TAG_SCHEDULED_STOP_POINTS;
            case "PassengerStopAssignment" -> END_TAG_STOP_ASSIGNMENTS;
            case "StopPlace" -> END_TAG_STOP_PLACES;
            case "Parking" -> END_TAG_PARKINGS;
            default -> throw new IllegalArgumentException("Unsupported type for collection end tag: " + type);
        };
    }

    private static byte[] suppressCollectionPlaceholder(byte[] bytes) {
        String trimmed = new String(bytes, StandardCharsets.UTF_8).trim();
        if (trimmed.equals(COLLECTION_TAG_SCHEDULED_STOP_POINTS)
                || trimmed.equals(COLLECTION_TAG_STOP_ASSIGNMENTS)
                || trimmed.equals(COLLECTION_TAG_STOP_PLACES)
                || trimmed.equals(COLLECTION_TAG_PARKINGS)) {
            return new byte[]{};
        }
        return bytes;
    }

    public void streamPublicationDelivery(
            ReadApiSearchKey searchKey,
            OutputStream outputStream
    ) throws IOException {
        Instant start = Instant.now();
        int entities = 0;
        try (BufferedReader xmlReader = new BufferedReader(new StringReader(getPublicationDeliveryTemplate()));
             Stream<ReadApiEntityOutRecord> stopPlaceStream = this.netexRepository.streamStopPlaces(searchKey)) {
            Iterator<ReadApiEntityOutRecord> netexIterator = stopPlaceStream.iterator();
            String previousType = null;
            String line;
            Instant streamOpenedAt = Instant.now();
            while (netexIterator.hasNext()) {
                ReadApiEntityOutRecord netexEntityRow = netexIterator.next();
                String type = netexEntityRow.type();
                byte[] xmlEntity = netexEntityRow.xml();

                if (!Objects.equals(previousType, type)) {
                    // Close previous collection if needed
                    if (previousType != null) {
                        outputStream.write(getCollectionEndTag(previousType));
                        outputStream.write(NEWLINE);
                    }

                    // Type has changed, need to move to the correct collection
                    while ((line = xmlReader.readLine()) != null) {
                        if (line.trim().equals(getCollectionTag(type))) {
                            outputStream.write(getCollectionStartTag(type));
                            outputStream.write(NEWLINE);
                            break;
                        } else {
                            outputStream.write(suppressCollectionPlaceholder(line.getBytes(StandardCharsets.UTF_8)));
                            outputStream.write(NEWLINE);
                        }
                    }
                    previousType = type;
                }

                outputStream.write(xmlEntity);
                outputStream.write(NEWLINE);
                entities++;
            }

            if (previousType != null) {
                // Close last collection
                outputStream.write(getCollectionEndTag(previousType));
                outputStream.write(NEWLINE);
            }

            while ((line = xmlReader.readLine()) != null) {
                // Write remaining lines after last collection
                outputStream.write(suppressCollectionPlaceholder(line.getBytes(StandardCharsets.UTF_8)));
                outputStream.write(NEWLINE);
            }
            outputStream.flush();

            Instant end = Instant.now();
            logger.info(
                    "Publication delivery completed. Total time {} ms. Stream opened at {} ms. Total {} entities returned.",
                    end.toEpochMilli() - start.toEpochMilli(), streamOpenedAt.toEpochMilli() - start.toEpochMilli(), entities);
        } catch (IOException e) {
            logger.error("Failed to write publication delivery", e);
            throw e;  // Re-throw to signal failure
        }
    }
}
