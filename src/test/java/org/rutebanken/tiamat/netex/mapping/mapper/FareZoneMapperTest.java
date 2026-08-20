package org.rutebanken.tiamat.netex.mapping.mapper;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import ma.glasnost.orika.MappingContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rutebanken.netex.model.FareZone;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.PointRefs_RelStructure;
import org.rutebanken.netex.model.ScheduledStopPointRefStructure;
import org.rutebanken.netex.model.ScopingMethodEnumeration;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class FareZoneMapperTest {

    private final FareZoneMapper fareZoneMapper = new FareZoneMapper();

    private final Logger mapperLogger = (Logger) LoggerFactory.getLogger(FareZoneMapper.class);
    private final ListAppender<ILoggingEvent> logAppender = new ListAppender<>();

    @Before
    public void attachAppender() {
        logAppender.start();
        mapperLogger.addAppender(logAppender);
    }

    @After
    public void detachAppender() {
        mapperLogger.detachAppender(logAppender);
    }

    @Test
    public void warnsWhenExplicitStopsHasNoMembers() {
        FareZone netexFareZone = new FareZone()
                .withId("RUT:FareZone:15")
                .withScopingMethod(ScopingMethodEnumeration.EXPLICIT_STOPS);

        org.rutebanken.tiamat.model.FareZone tiamatFareZone = new org.rutebanken.tiamat.model.FareZone();
        fareZoneMapper.mapAtoB(netexFareZone, tiamatFareZone, new MappingContext(new HashMap<>()));

        assertThat(tiamatFareZone.getFareZoneMembers()).isEmpty();
        assertThat(logAppender.list)
                .anySatisfy(event -> {
                    assertThat(event.getLevel()).isEqualTo(Level.WARN);
                    assertThat(event.getFormattedMessage()).contains("RUT:FareZone:15", "EXPLICIT_STOPS");
                });
    }

    @Test
    public void mapsMembersAndDoesNotWarnWhenExplicitStopsHasMembers() {
        ObjectFactory objectFactory = new ObjectFactory();
        FareZone netexFareZone = new FareZone()
                .withId("RUT:FareZone:16")
                .withScopingMethod(ScopingMethodEnumeration.EXPLICIT_STOPS)
                .withMembers(new PointRefs_RelStructure().withPointRef(
                        objectFactory.createScheduledStopPointRef(
                                new ScheduledStopPointRefStructure().withRef("NSR:ScheduledStopPoint:S54471"))));

        org.rutebanken.tiamat.model.FareZone tiamatFareZone = new org.rutebanken.tiamat.model.FareZone();
        fareZoneMapper.mapAtoB(netexFareZone, tiamatFareZone, new MappingContext(new HashMap<>()));

        assertThat(tiamatFareZone.getFareZoneMembers())
                .extracting(org.rutebanken.tiamat.model.StopPlaceReference::getRef)
                .containsExactly("NSR:StopPlace:54471");
        assertThat(logAppender.list).noneSatisfy(event ->
                assertThat(event.getLevel()).isEqualTo(Level.WARN));
    }
}
