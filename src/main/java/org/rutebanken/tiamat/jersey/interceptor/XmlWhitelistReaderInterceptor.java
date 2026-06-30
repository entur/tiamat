package org.rutebanken.tiamat.jersey.interceptor;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.ReaderInterceptorContext;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Set;

/**
 * Reader interceptor that validates incoming XML requests against a predefined whitelist of allowed XML paths.
 * The whitelist is specified via the {@link XmlWhitelist} annotation present on the resource method or class.
 * Resources without the annotation are ignored.
 */
@Component
public class XmlWhitelistReaderInterceptor implements ReaderInterceptor {

    @Context
    private ResourceInfo resourceInfo;

    private final ApplicationContext applicationContext;

    public XmlWhitelistReaderInterceptor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
        XmlWhitelist annotation = resolveAnnotation();
        if (annotation == null) {
            return context.proceed();
        }

        Set<String> allowed = instantiateProvider(annotation.value()).allowedPaths();
        byte[] body = context.getInputStream().readAllBytes();
        XmlPathValidator.validate(body, allowed);
        context.setInputStream(new ByteArrayInputStream(body));
        return context.proceed();
    }

    private XmlWhitelist.XmlWhitelistProvider instantiateProvider(Class<? extends XmlWhitelist.XmlWhitelistProvider> providerClass) {
        return applicationContext.getBean(providerClass);
    }

    private XmlWhitelist resolveAnnotation() {
        XmlWhitelist methodAnnotation = resourceInfo.getResourceMethod().getAnnotation(XmlWhitelist.class);
        if (methodAnnotation != null) {
            return methodAnnotation;
        }
        return resourceInfo.getResourceClass().getAnnotation(XmlWhitelist.class);
    }
}