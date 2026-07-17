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

package org.rutebanken.tiamat.ext.fintraffic.config;

import org.rutebanken.tiamat.config.ApplicationContextProvider;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 * Test-only replacement for {@link ApplicationContextProvider} used in the fintraffic Spring test context.
 * <p>
 * {@link ApplicationContextProvider} stores the active {@link ApplicationContext} in a static field.
 * When the fintraffic test context starts it overwrites this field, causing subsequent tests running
 * in the primary test context to get the wrong beans via {@code ApplicationContextProvider}.
 * <p>
 * This class saves the previous value of the static field when the fintraffic context sets it and
 * exposes {@link #restoreContext()} so test teardown can restore the field before the primary-context
 * tests that follow continue running.
 * <p>
 * Registered as a {@code @Bean} in {@link FintrafficTestContextConfiguration} which also excludes
 * the component-scanned {@link ApplicationContextProvider} from the fintraffic context.
 */
class RestoringApplicationContextProvider extends ApplicationContextProvider {

    private static ApplicationContext previousContext;
    private static RestoringApplicationContextProvider instance;

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        previousContext = ApplicationContextProvider.getApplicationContext();
        instance = this;
        super.setApplicationContext(ctx);
    }

    private void applyContext(ApplicationContext ctx) throws BeansException {
        super.setApplicationContext(ctx);
    }

    static void restoreContext() {
        if (instance != null && previousContext != null) {
            instance.applyContext(previousContext);
            previousContext = null;
        }
    }
}
