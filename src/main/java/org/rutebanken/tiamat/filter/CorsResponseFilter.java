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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

import static org.rutebanken.tiamat.config.JerseyConfig.ET_CLIENT_ID_HEADER;
import static org.rutebanken.tiamat.config.JerseyConfig.ET_CLIENT_NAME_HEADER;
import static org.rutebanken.tiamat.config.JerseyConfig.X_CORRELATION_ID_HEADER;

/**
 * https://github.com/Smartling/spring-security-keycloak/issues/1
 */
@Component
public class CorsResponseFilter implements Filter {

    final Logger logger = LoggerFactory.getLogger(CorsResponseFilter.class);

    @Override
    public void init(FilterConfig filterConfig){}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS, POST, DELETE, PUT");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, Authorization, Accept, X-Correlation-Id, entur-pos, "
                                            + ET_CLIENT_ID_HEADER + ", " + ET_CLIENT_NAME_HEADER);
        httpServletResponse.setHeader("Access-Control-Max-Age", "3600");
        // If the call is an options call, do not return a body
        HttpServletRequest req = (HttpServletRequest)servletRequest;
        HttpServletResponse resp = (HttpServletResponse )servletResponse;

        String correlation_id_header = req.getHeader(X_CORRELATION_ID_HEADER);

        if (StringUtils.isEmpty(correlation_id_header)) {
            correlation_id_header = UUID.randomUUID().toString();
            logger.info("No correlation_id found in header generating new correlation-id: " + correlation_id_header);
        }

        httpServletResponse.setHeader(X_CORRELATION_ID_HEADER, correlation_id_header);
        if ("OPTIONS".equals(req.getMethod())) {
            resp.setStatus(HttpStatus.OK.value());
        } else {
            filterChain.doFilter(servletRequest,servletResponse);
        }
    }

    @Override
    public void destroy() {}
}
