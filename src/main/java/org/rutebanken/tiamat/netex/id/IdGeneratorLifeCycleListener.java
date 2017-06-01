package org.rutebanken.tiamat.netex.id;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleListener;
import org.rutebanken.netex.model.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class IdGeneratorLifeCycleListener implements LifecycleListener {

    private static final Logger logger = LoggerFactory.getLogger(IdGeneratorLifeCycleListener.class);

    private static boolean wasCalled = false;

    private static final Object LOCK = new Object();

    @Autowired
    private GaplessIdGeneratorService gaplessIdGeneratorService;

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @PostConstruct
    public void registerListener() {
        hazelcastInstance.getLifecycleService().addLifecycleListener(this);
    }

    @Override
    public void stateChanged(LifecycleEvent lifecycleEvent) {
        if(lifecycleEvent.getState().equals(LifecycleEvent.LifecycleState.SHUTTING_DOWN)) {
            synchronized (LOCK) {
                // This could happen on any node.
                // As long as the claimed IDs are written and claimed IDs are removed from hazelcast queues for entities it should be fine.
                if (!wasCalled) {
                    logger.info("Calling gaplessIdGeneratorService to shut down");
                    gaplessIdGeneratorService.writeStateWhenshuttingDown();
                }
            }
        }
    }
}
