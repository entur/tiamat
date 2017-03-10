package org.rutebanken.tiamat;


import org.rutebanken.tiamat.netex.id.GaplessIdGenerator;
import org.rutebanken.tiamat.netex.id.GeneratedIdState;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

public class TestCleanUpExecutionListener extends DependencyInjectionTestExecutionListener {
/*
    @Override
    public void prepareTestInstance(TestContext testContext) throws Exception {
        super.prepareTestInstance(testContext);
        GeneratedIdState generatedIdState = testContext.getApplicationContext().getBean(GeneratedIdState.class);
        GaplessIdGenerator gaplessIdGenerator = testContext.getApplicationContext().getBean(GaplessIdGenerator.class);
        gaplessIdGenerator.getEntityTypeNames().forEach(entityTypeName -> {
            generatedIdState.getClaimedIdQueueForEntity(entityTypeName).clear();
            generatedIdState.getLastIdForEntityMap().put(entityTypeName, 1L);
            generatedIdState.getQueueForEntity(entityTypeName).clear();
        });

    }
*

}
