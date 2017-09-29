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

package org.rutebanken.tiamat.exporter.async;

import org.rutebanken.tiamat.model.EntityStructure;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

public class NetexMappingIteratorList<N extends org.rutebanken.netex.model.EntityStructure> extends ArrayList<N> {

    private final Callable<Iterator<N>> iteratorRetriever;

    public NetexMappingIteratorList(Callable<Iterator<N>> iteratorRetriever) {
        this.iteratorRetriever = iteratorRetriever;
    }

    @Override
    public Iterator<N> iterator() {
        try {
            return iteratorRetriever.call();
        } catch (Exception e) {
            throw new RuntimeException("Cannot get iterator", e);
        }
    }

}
