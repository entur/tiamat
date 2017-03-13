package org.rutebanken.tiamat;


import com.hazelcast.core.HazelcastInstance;
import org.rutebanken.tiamat.netex.id.GaplessIdGenerator;
import org.rutebanken.tiamat.netex.id.GeneratedIdState;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

public class TestCleanUpExecutionListener extends AbstractTestExecutionListener {

    @Override
    public void prepareTestInstance(TestContext testContext) throws Exception {
        super.prepareTestInstance(testContext);
        GeneratedIdState generatedIdState = testContext.getApplicationContext().getBean(GeneratedIdState.class);
        GaplessIdGenerator gaplessIdGenerator = testContext.getApplicationContext().getBean(GaplessIdGenerator.class);
        HazelcastInstance hazelcastInstance = testContext.getApplicationContext().getBean(HazelcastInstance.class);

        gaplessIdGenerator.getEntityTypeNames().forEach(entityTypeName -> {
            generatedIdState.getClaimedIdQueueForEntity(entityTypeName).clear();
            generatedIdState.getLastIdForEntityMap().put(entityTypeName, 1L);
            generatedIdState.getQueueForEntity(entityTypeName).clear();
            hazelcastInstance.getList("used-h2-ids-by-entity-" + entityTypeName).clear();

        });

    }


}
