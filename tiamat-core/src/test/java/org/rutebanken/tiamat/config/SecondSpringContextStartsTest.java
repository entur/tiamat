package org.rutebanken.tiamat.config;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * More than one Hazelcast instance has to be able to start in the same JVM.
 * <p>
 * Tests need this because every Spring context brings up its own instance and Spring caches
 * contexts rather than closing them, so several are alive at once. If an instance cannot move
 * off a port that is already taken, the second context fails to load, and it surfaces as an
 * ApplicationContext failure in whichever test happens to run second rather than anywhere near
 * the cause. That is what {@code tiamat.hazelcast.port.auto-increment} is for, and production
 * leaves it off because one instance per pod binds a known port.
 * <p>
 * Asserting it this way rather than by declaring a second context on purpose: a context based
 * test passes whenever it happens to run first, and pushes the failure into an unrelated class.
 * Starting a second instance from the same configuration fails the same way every time, because
 * the context this test runs in is already holding the port.
 */
public class SecondSpringContextStartsTest extends TiamatIntegrationTest {

    @Autowired
    private Config hazelcastConfig;

    @Autowired
    private HazelcastInstance runningInstance;

    @Test
    public void aSecondInstanceCanStartAlongsideTheFirst() {
        assertThat(runningInstance.getLifecycleService().isRunning())
                .as("precondition: this context is already holding a port")
                .isTrue();

        HazelcastInstance second = Hazelcast.newHazelcastInstance(hazelcastConfig);
        try {
            assertThat(second.getLifecycleService().isRunning())
                    .as("a second context must be able to start, which means moving off the taken port")
                    .isTrue();
        } finally {
            second.getLifecycleService().terminate();
        }
    }
}
