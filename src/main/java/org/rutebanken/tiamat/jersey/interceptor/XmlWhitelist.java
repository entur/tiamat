package org.rutebanken.tiamat.jersey.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Set;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlWhitelist {
    Class<? extends XmlWhitelistProvider> value();

    interface XmlWhitelistProvider {
        Set<String> allowedPaths();
    }
}