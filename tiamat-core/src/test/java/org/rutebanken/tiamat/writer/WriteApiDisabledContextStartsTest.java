package org.rutebanken.tiamat.writer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.rutebanken.tiamat.TiamatTestApplication;
import org.rutebanken.tiamat.writer.async.WriteJobPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tiamat has to start for a deployment that never asked for the write API.
 * <p>
 * Every other test sets both write API properties, which hid that {@link AsyncStopPlaceWriter}
 * demanded a {@link WriteJobPublisher} unconditionally while the only implementation of one
 * appears only after a transport has been selected. A deployment that set neither property did
 * not fail to serve writes, it failed to start at all, and no existing context caught it because
 * none of them are configured that way.
 * <p>
 * An empty transport rather than an absent one, because a property source cannot unset a value:
 * both leave the conditional unmatched, which is the condition under test.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TiamatTestApplication.class)
@ActiveProfiles({"test", "local-blobstore"})
@TestPropertySource(properties = {
        "tiamat.write-api.enabled=false",
        "tiamat.write-api.transport="
})
public class WriteApiDisabledContextStartsTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void contextStartsWithoutATransport() {
        assertThat(applicationContext.getBeansOfType(WriteJobPublisher.class))
                .as("no transport is selected, so nothing should provide one")
                .isEmpty();
        assertThat(applicationContext.getBeansOfType(AsyncStopPlaceWriter.class))
                .as("the write API is off, so the service that needs a transport must not be created")
                .isEmpty();
    }
}
