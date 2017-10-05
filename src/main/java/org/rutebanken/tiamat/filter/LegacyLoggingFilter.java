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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;

@Component
public class LegacyLoggingFilter implements Filter {

    final Logger logger = LoggerFactory.getLogger(LegacyLoggingFilter.class);

    @Autowired
    private UsernameFetcher usernameFetcher;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        if (servletRequest instanceof HttpServletRequest) {

            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            String requestUri = httpServletRequest.getRequestURI();
            String userName = usernameFetcher.getUserNameForAuthenticatedUser();
            if (requestUri.contains("tiamat") || requestUri.contains("jersey")) {

                String headers = headersAsString(httpServletRequest);
                logger.warn("Request on legacy path: {}. Username if available: {}. Headers:{}", requestUri, userName, headers.toString());
            } else {
                logger.trace("Non-legacy request: {}. Username if available: {}", requestUri, userName);
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private String headersAsString(HttpServletRequest httpServletRequest) {
        StringBuilder headers = new StringBuilder();
        if (httpServletRequest.getHeaderNames() != null) {
            Enumeration<String> headerNames = httpServletRequest.getHeaderNames();

            if(headerNames != null) {
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
