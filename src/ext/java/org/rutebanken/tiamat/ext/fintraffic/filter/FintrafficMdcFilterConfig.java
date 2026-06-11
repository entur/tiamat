package org.rutebanken.tiamat.ext.fintraffic.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import org.rutebanken.tiamat.ext.fintraffic.auth.TrivoreAuthorizations;
import org.slf4j.MDC;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Populates Logback MDC fields for each request to enable structured log correlation:
 * <ul>
 *   <li>{@code userId} — the {@code sub} claim from the JWT, identifying the authenticated Trivore user.
 *       Falls back to {@code "<unknown user>"} for unauthenticated requests.</li>
 *   <li>{@code awsCfId} — the CloudFront request ID ({@code X-Amz-Cf-Id} header), useful for
 *       correlating logs with CloudFront access logs.</li>
 *   <li>{@code awsAmznTraceId} — the ALB/X-Ray trace ID ({@code X-Amzn-Trace-Id} header), useful for
 *       distributed tracing across AWS services.</li>
 * </ul>
 */
@Configuration
@Profile("fintraffic")
public class FintrafficMdcFilterConfig {

    private static final String MDC_USER_ID = "userId";
    private static final String MDC_AWS_CF_ID = "awsCfId";
    private static final String MDC_AWS_ALB_ID = "awsAmznTraceId";

    private static final String HEADER_AWS_CF_ID = "X-Amz-Cf-Id";
    private static final String HEADER_AWS_ALB_ID = "X-Amzn-Trace-Id";

    private Filter mdcFilter() {
        return (req, res, filterChain) -> {
            MDC.put(MDC_USER_ID, TrivoreAuthorizations.getCurrentSubject());
            if (req instanceof HttpServletRequest httpRequest) {
                String cfId = httpRequest.getHeader(HEADER_AWS_CF_ID);
                String albId = httpRequest.getHeader(HEADER_AWS_ALB_ID);
                if (cfId != null) {
                    MDC.put(MDC_AWS_CF_ID, cfId);
                }
                if (albId != null) {
                    MDC.put(MDC_AWS_ALB_ID, albId);
                }
            }
            try {
                filterChain.doFilter(req, res);
            } finally {
                MDC.remove(MDC_USER_ID);
                MDC.remove(MDC_AWS_CF_ID);
                MDC.remove(MDC_AWS_ALB_ID);
            }
        };
    }

    @Bean
    public FilterRegistrationBean<Filter> mdcFilterRegistrationBean() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(mdcFilter());
        registration.addUrlPatterns("/*");
        registration.setName("mdcFilter");
        registration.setOrder(1); // Run after Spring Security populates the SecurityContext
        return registration;
    }
}
