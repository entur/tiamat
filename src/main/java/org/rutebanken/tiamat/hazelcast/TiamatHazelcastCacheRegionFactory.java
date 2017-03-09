package org.rutebanken.tiamat.hazelcast;


import com.hazelcast.core.HazelcastInstance;
import org.hibernate.cache.spi.RegionFactory;
import org.rutebanken.hazelcasthelper.service.KubernetesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is implemented because we want to run our own configuration of HazelCast, and we want to make
 * sure that not two instances of Hazelcast is running. (Hazelcast reads default config xml if this class is not used)
 * Because this class is initialized before spring DI, we read properties from System.properties.
 */
public class TiamatHazelcastCacheRegionFactory extends com.hazelcast.hibernate.HazelcastCacheRegionFactory implements RegionFactory{

    private static final Logger logger = LoggerFactory.getLogger(TiamatHazelcastCacheRegionFactory.class);

    private static final ExtendedHazelcastService extendedHazelcastService = initHazelcastService();

    private static ExtendedHazelcastService initHazelcastService() {

        try {
            logger.info("initHazelcastService");
            String kubernetesUrl = getProperty("rutebanken.kubernetes.url", false);

            boolean kuberentesEnabled = getBooleanProperty("rutebanken.kubernetes.enabled", true);
            String namespace = getProperty("rutebanken.kubernetes.namespace", false);
            String hazelcastManagementUrl = getProperty("rutebanken.hazelcast.management.url", false);

            KubernetesService kubernetesService = new KubernetesService(kubernetesUrl, namespace, kuberentesEnabled);
            logger.info("Created kubernetes service");
            ExtendedHazelcastService extendedHazelcastService = new ExtendedHazelcastService(kubernetesService, hazelcastManagementUrl);
            logger.info("Init ExtendedHazelcastService");
            extendedHazelcastService.init();
            logger.info(extendedHazelcastService.information());
            return extendedHazelcastService;

        } catch (Exception e) {
            throw new RuntimeException("Error initializing hazelcast service", e);
        }
    }

    public static HazelcastInstance getHazelCastInstance() {
        return extendedHazelcastService.getHazelcastInstance();
    }

    private static String getProperty(String key, boolean required) {
        String value = System.getProperty(key);
        logger.info("Loaded {}: {}", key, value);
        if(required && value == null) {
            throw new RuntimeException("Property " + key + " cannot be null");
        }
        return value;
    }

    private static boolean getBooleanProperty(String key, boolean required) {
        String value = getProperty(key, required);
        boolean booleanValue = value.equalsIgnoreCase("true");
        return booleanValue;
    }

    /**
     * Must be configured in properties file. Like this:
     * spring.jpa.properties.hibernate.cache.region.factory_class=org.rutebanken.tiamat.hazelcast.TiamatHazelcastCacheRegionFactory
     */
    public TiamatHazelcastCacheRegionFactory() {
        super(extendedHazelcastService.getHazelcastInstance());
        logger.info("Created factory with: {}", getHazelcastInstance());
    }

}
