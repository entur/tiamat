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

package org.rutebanken.tiamat.config;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JettyConfig {

    private static final Logger logger = LoggerFactory.getLogger(JettyConfig.class);

    @Bean
    public JettyServletWebServerFactory jettyEmbeddedServletContainerFactory(
            @Value("${jettyMinThreads:10}") int jettyMinThreads,
            @Value("${jettyMaxThreads:40}") int jettyMaxThreads) {

        logger.info("Configuring jetty with minThreads: {} and maxThreads: {}", jettyMinThreads, jettyMaxThreads);

        final JettyServletWebServerFactory factory = new JettyServletWebServerFactory() {
            @Override
            public QueuedThreadPool getThreadPool() {
                return new QueuedThreadPool(jettyMaxThreads, jettyMinThreads);
            }
        };
        return factory;
    }
}
