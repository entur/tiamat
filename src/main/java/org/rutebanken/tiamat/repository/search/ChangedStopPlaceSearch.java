package org.rutebanken.tiamat.repository.search;
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

import com.google.common.base.MoreObjects;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

public class ChangedStopPlaceSearch {

    private Instant from;

    private Instant to;

    private Pageable pageable;

    public ChangedStopPlaceSearch(Instant from, Instant to, Pageable pageable) {
        this.from = from;
        this.to = to;
        this.pageable = pageable;
    }

    public Instant getFrom() {
        return from;
    }

    public Instant getTo() {
        return to;
    }

    public Pageable getPageable() {
        return pageable;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("from", from)
                .add("to", to)
                .add("pageable", pageable)
                .toString();
    }
}
