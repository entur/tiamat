package org.rutebanken.tiamat.ext.fintraffic.config;

import org.rutebanken.tiamat.ext.fintraffic.model.FintrafficParking;
import org.springframework.boot.persistence.autoconfigure.EntityScanPackages;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.Profile;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Import;

/**
 * Adds {@code org.rutebanken.tiamat.ext.fintraffic.model} to Hibernate's entity scan.
 * <p>
 * Core's {@code TiamatApplication} only declares
 * {@code @EntityScan(basePackageClasses = {StopPlace.class, ...})}, which covers
 * {@code org.rutebanken.tiamat.model} — not this ext package. Without this, Hibernate
 * never registers {@link FintrafficParking} (or any other ext {@code @Entity}) and
 * every operation on it fails with "... is not an entity".
 * <p>
 * {@link EntityScanPackages#register} is additive: if a package list is already
 * registered (by core's {@code @EntityScan}), it appends to it rather than replacing
 * it, so this does not require modifying {@code TiamatApplication} — core is only
 * ever extended here, never overridden.
 */
@Configuration
@Profile("fintraffic")
@Import(FintrafficEntityScanConfig.Registrar.class)
public class FintrafficEntityScanConfig {

    static class Registrar implements ImportBeanDefinitionRegistrar {
        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            EntityScanPackages.register(registry, FintrafficParking.class.getPackageName());
        }
    }
}
