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

package org.rutebanken.tiamat.ext.fintraffic;

import org.junit.AfterClass;
import org.rutebanken.tiamat.ext.fintraffic.config.FintrafficTestContextConfiguration;

/**
 * Base class for fintraffic integration tests that start a secondary Spring context.
 * <p>
 * {@link org.rutebanken.tiamat.config.ApplicationContextProvider} uses a static field to hold
 * the active {@code ApplicationContext}, which is a JVM-global singleton. When the fintraffic
 * test context starts it overwrites this field via {@code RestoringApplicationContextProvider},
 * which saves the previous value. {@link #restoreApplicationContext()} restores it after all
 * tests in the subclass complete, so the primary-context tests that follow get the correct beans.
 */
public abstract class FintrafficIntegrationTest {

    @AfterClass
    public static void restoreApplicationContext() {
        FintrafficTestContextConfiguration.restoreContext();
    }
}
