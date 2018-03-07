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

package org.rutebanken.tiamat.exporter.eviction;

import com.google.common.collect.Sets;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.internal.SessionImpl;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.model.tag.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Evict entities from session during scrolling results from databases
 */
public interface EntitiesEvictor {

    void evictKnownEntitiesFromSession(Object entity);
}
