package org.rutebanken.tiamat.rest.graphql.fetchers;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.rutebanken.tiamat.model.StopPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Interceptor to log access to StopPlace collections for debugging N+1 query issues.
 * Only active when debug profile is enabled.
 */
@Aspect
@Component
@Profile("debug")
public class StopPlaceAccessInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(StopPlaceAccessInterceptor.class);
    
    @Around("execution(* org.rutebanken.tiamat.model.StopPlace.getQuays(..))")
    public Object logQuayAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        StopPlace stopPlace = (StopPlace) joinPoint.getTarget();
        logger.debug("Accessing quays for StopPlace: {}", stopPlace.getNetexId());
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        logger.debug("Quay access took {} ms", System.currentTimeMillis() - start);
        return result;
    }
    
    @Around("execution(* org.rutebanken.tiamat.model.StopPlace.getChildren(..))")
    public Object logChildrenAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        StopPlace stopPlace = (StopPlace) joinPoint.getTarget();
        logger.debug("Accessing children for StopPlace: {}", stopPlace.getNetexId());
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        logger.debug("Children access took {} ms", System.currentTimeMillis() - start);
        return result;
    }
    
    @Around("execution(* org.rutebanken.tiamat.model.StopPlace.getAccessSpaces(..))")
    public Object logAccessSpacesAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        StopPlace stopPlace = (StopPlace) joinPoint.getTarget();
        logger.debug("Accessing accessSpaces for StopPlace: {}", stopPlace.getNetexId());
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        logger.debug("AccessSpaces access took {} ms", System.currentTimeMillis() - start);
        return result;
    }
}