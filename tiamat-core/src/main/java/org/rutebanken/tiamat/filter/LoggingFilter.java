/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.filter;


import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.rutebanken.tiamat.auth.UsernameFetcher;
import org.rutebanken.tiamat.service.metrics.PrometheusMetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Enumeration;

import static org.rutebanken.tiamat.config.JerseyConfig.ET_CLIENT_ID_HEADER;
import static org.rutebanken.tiamat.config.JerseyConfig.ET_CLIENT_NAME_HEADER;

@Component
public class LoggingFilter implements Filter {

    final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Autowired
    private UsernameFetcher usernameFetcher;

    @Autowired
    private PrometheusMetricsService prometheusMetricsService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        if (servletRequest instanceof HttpServletRequest httpServletRequest) {

            String requestUri = httpServletRequest.getRequestURI();

            if(!requestUri.startsWith("/health")) {

                String clientName = httpServletRequest.getHeader(ET_CLIENT_NAME_HEADER);
                String clientId = httpServletRequest.getHeader(ET_CLIENT_ID_HEADER);

                String userName = usernameFetcher.getUserNameForAuthenticatedUser();

                if (logger.isTraceEnabled()) {
                    // If trace enabled, log all headers.
                    String allHeaders = headersAsString(httpServletRequest);
                    logger.trace("{} User: '{}', Headers: '{}'", requestUri, userName, allHeaders.toString());
                } else {

                    logger.info("{}: User: '{}', Client: '{}', ID: '{}'", requestUri, userName, clientName, clientId);
                }

                prometheusMetricsService.registerRequestFromClient(clientName,clientId,1L);
                prometheusMetricsService.registerRequestFromUser(userName,1L);
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private String headersAsString(HttpServletRequest httpServletRequest) {
        StringBuilder headers = new StringBuilder();
        if (httpServletRequest.getHeaderNames() != null) {
            Enumeration<String> headerNames = httpServletRequest.getHeaderNames();

            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    headers.append("\n");
                    String headerName = headerNames.nextElement();
                    headers.append(headerName)
                            .append(": ");

                    String headerValue = httpServletRequest.getHeader(headerName);
                    headers.append(headerValue);

                }
            }
        }
        return headers.toString();
    }

    @Override
    public void destroy() {
    }
}