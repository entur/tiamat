package org.rutebanken.tiamat.gcp.writer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.rutebanken.tiamat.TiamatTestApplication;
import org.rutebanken.tiamat.writer.AsyncStopPlaceWriter;
import org.rutebanken.tiamat.writer.async.WriteJobTimeoutSweeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * These two properties together are how an operator takes the write API out of service and
 * leaves the transport configured. {@link OnPubSubWriteTransport} says why that has to stop the
 * subscriber.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TiamatTestApplication.class)
@ActiveProfiles({"test", "local-blobstore"})
@TestPropertySource(properties = {
        "tiamat.write-api.enabled=false",
        "tiamat.write-api.transport=pubsub"
})
public class WriteApiDisabledDoesNotConsumeFromBrokerTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void nothingConsumesFromTheBrokerWhileTheWriteApiIsOff() {
        assertThat(applicationContext.getBeansOfType(PubSubWriteJobSubscriber.class))
                .as("a subscriber keeps writing with no sweeper to finish what it started")
                .isEmpty();
        assertThat(applicationContext.getBeansOfType(PubSubWriteJobPublisher.class))
                .as("and nothing offers a transport for an API that is off")
                .isEmpty();
        assertThat(applicationContext.getBeansOfType(AsyncStopPlaceWriter.class)).isEmpty();
        assertThat(applicationContext.getBeansOfType(WriteJobTimeoutSweeper.class)).isEmpty();
    }
}
