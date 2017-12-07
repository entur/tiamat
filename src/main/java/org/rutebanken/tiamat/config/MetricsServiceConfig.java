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

import com.google.common.base.Strings;
import org.rutebanken.tiamat.service.metrics.DoNothingMetricsService;
import org.rutebanken.tiamat.service.metrics.MetricsService;
import org.rutebanken.tiamat.service.metrics.MetricsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsServiceConfig {

    private static final Logger logger = LoggerFactory.getLogger(MetricsServiceConfig.class);

    @Bean
    public MetricsService metricsService(@Value("${graphite.server:}") String graphiteServerDns) {

        if(Strings.isNullOrEmpty(graphiteServerDns)) {
            logger.info("Not starting metrics service, as I was not supplied with graphite server dns name");
            return new DoNothingMetricsService();
        }

        return new MetricsServiceImpl(graphiteServerDns, 2003);
    }
}
