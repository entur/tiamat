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

package org.rutebanken.tiamat.service.metrics;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.Counter;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class MetricsServiceImpl implements MetricsService{
    private static final Logger logger = LoggerFactory.getLogger(MetricsServiceImpl.class);

    private final MetricRegistry metrics = new MetricRegistry();

    private final GraphiteReporter reporter;

    private final Graphite graphite;

    public MetricsServiceImpl(String graphiteServerDns, int graphitePort) {

        if (Strings.isNullOrEmpty(graphiteServerDns)) {
            throw new IllegalArgumentException("graphiteServerDns must not be null or empty");
        }

        logger.info("Setting up metrics service with graphite server dns: {}", graphiteServerDns);
        graphite = new Graphite(new InetSocketAddress(graphiteServerDns, graphitePort));

        reporter = GraphiteReporter.forRegistry(metrics)
                .prefixedWith("app.tiamat")
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(MetricFilter.ALL)
                .build(graphite);
    }

    @PostConstruct
    public void postConstruct() {
        logger.info("Starting graphite reporter");
        reporter.start(1, TimeUnit.MINUTES);
    }

    @PreDestroy
    public void shutdown() throws IOException {
        reporter.stop();
        if (graphite.isConnected()) {
            graphite.flush();
            graphite.close();
        }
    }

    @Override
    public void registerRequestFromClient(String clientName, String clientId) {

        metrics.meter("requests.from.client.name." + clientName).mark();
        metrics.meter("requests.from.client.id." + clientId).mark();
    }

    @Override
    public void registerRequestFromUser(String userName) {
        metrics.meter("requests.from.user.name." + userName).mark();
    }

}
