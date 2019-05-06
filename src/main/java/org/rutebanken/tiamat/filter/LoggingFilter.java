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


import org.rutebanken.tiamat.auth.UsernameFetcher;
import org.rutebanken.tiamat.service.metrics.MetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;

import static org.rutebanken.tiamat.config.JerseyConfig.ET_CLIENT_ID_HEADER;
import static org.rutebanken.tiamat.config.JerseyConfig.ET_CLIENT_NAME_HEADER;
import static org.rutebanken.tiamat.config.JerseyConfig.X_CORRELATION_ID_HEADER;

@Component
public class LoggingFilter implements Filter {

    final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Autowired
    private UsernameFetcher usernameFetcher;

    @Autowired
    private MetricsService metricsService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        if (servletRequest instanceof HttpServletRequest) {

            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            String requestUri = httpServletRequest.getRequestURI();

            if(!requestUri.startsWith("/health")) {

                String clientName = httpServletRequest.getHeader(ET_CLIENT_NAME_HEADER);
                String clientId = httpServletRequest.getHeader(ET_CLIENT_ID_HEADER);
                final String correlationId = httpServletRequest.getHeader(X_CORRELATION_ID_HEADER);

                String userName = usernameFetcher.getUserNameForAuthenticatedUser();

                if (logger.isTraceEnabled()) {
                    // If trace enabled, log all headers.
                    String allHeaders = headersAsString(httpServletRequest);
                    logger.trace("{} User: '{}', Headers: '{}'", requestUri, userName, allHeaders);
                } else {

                    logger.info("{}: User: '{}', Client: '{}', Client_ID: '{}', Correlation_Id: '{}' ", requestUri, userName, clientName, clientId, correlationId);
                }

                metricsService.registerRequestFromClient(clientName, clientId);
                metricsService.registerRequestFromUser(userName);
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