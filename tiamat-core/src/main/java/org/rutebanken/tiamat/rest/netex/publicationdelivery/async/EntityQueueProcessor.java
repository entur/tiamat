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

package org.rutebanken.tiamat.rest.netex.publicationdelivery.async;

import org.rutebanken.netex.model.EntityInVersionStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class EntityQueueProcessor<T extends EntityInVersionStructure> implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(EntityQueueProcessor.class);

    private final Consumer<T> consumer;
    private final T poison;
    private final AtomicBoolean stopExecution;
    private final BlockingQueue<T> queue;
    private final String type;

    public EntityQueueProcessor(BlockingQueue<T> queue, AtomicBoolean stopExecution, Consumer<T> consumer, T poison) {
        this.queue = queue;
        this.stopExecution = stopExecution;
        this.consumer = consumer;
        this.poison = poison;
        this.type = poison.getClass().getSimpleName();
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted() && !stopExecution.get()) {
                T t = queue.poll(1, TimeUnit.SECONDS);

                if (t == null) {
                    continue;
                }

                if (t.getId().equals(poison.getId())) {
                    logger.info("Finished importing {}. Got poison pill.", type);
                    stopExecution.set(true);
                    break;
                }

                consumer.accept(t);
            }
        } catch (InterruptedException e) {
            logger.warn("Interrupted. Stopping all jobs for {}", type);
            stopExecution.set(true);
            Thread.currentThread().interrupt();
            return;
        } catch (Exception e) {
            logger.warn("Caught exception while importing {}", type, e);
            stopExecution.set(true);
        }
    }
}
