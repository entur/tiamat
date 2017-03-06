package org.rutebanken.tiamat.config;

import org.rutebanken.tiamat.netex.id.NetexIdAssigner;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextProvider implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private static final String NETEX_ID_ASSIGNER = "netexIdAssigner";

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextProvider.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static NetexIdAssigner getNetexIdAssigner() {
        return (NetexIdAssigner) applicationContext.getBean(NETEX_ID_ASSIGNER);
    }

}