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

package org.rutebanken.tiamat.exporter;

import com.google.common.base.MoreObjects;
import org.rutebanken.netex.model.PublicationDeliveryStructure;

public class PublicationDeliveryStructurePage {

    public final int size;

    public PublicationDeliveryStructure publicationDeliveryStructure;

    public long totalElements;

    public boolean hasNext;

    public PublicationDeliveryStructurePage(PublicationDeliveryStructure publicationDeliveryStructure, int size, long totalElements, boolean hasNext) {
        this.publicationDeliveryStructure = publicationDeliveryStructure;
        this.totalElements = totalElements;
        this.size = size;
        this.hasNext = hasNext;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("publicationDeliveryStructure", publicationDeliveryStructure)
                .add("totalElements", totalElements)
                .add("hasNext", hasNext)
                .add("size", size)
                .toString();
    }
}
