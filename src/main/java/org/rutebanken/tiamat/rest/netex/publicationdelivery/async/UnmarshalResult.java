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

import com.google.common.base.MoreObjects;
import org.rutebanken.netex.model.*;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class UnmarshalResult {

    private final BlockingQueue<StopPlace> stopPlaceQueue;
    private final BlockingQueue<Parking> parkingQueue;
    private final BlockingQueue<TopographicPlace> topographicPlaceQueue;
    private final BlockingQueue<NavigationPath> navigationPathsQueue;

    private PublicationDeliveryStructure publicationDeliveryStructure;

    public UnmarshalResult(int size) {
        stopPlaceQueue = new ArrayBlockingQueue<>(size);
        parkingQueue = new ArrayBlockingQueue<>(size);
        topographicPlaceQueue = new ArrayBlockingQueue<>(size);
        navigationPathsQueue = new ArrayBlockingQueue<>(size);
    }

    public BlockingQueue<StopPlace> getStopPlaceQueue() {
        return stopPlaceQueue;
    }

    public BlockingQueue<Parking> getParkingQueue() {
        return parkingQueue;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("publicationDelivery", publicationDeliveryStructure)
                .add("stopPlaceQueue", stopPlaceQueue.size())
                .add("parkingQueue", parkingQueue.size())
                .add("topographicPlaceQueue", topographicPlaceQueue.size())
                .add("navigationPathsQueue", navigationPathsQueue.size())
                .toString();
    }

    public PublicationDeliveryStructure getPublicationDeliveryStructure() {
        return publicationDeliveryStructure;
    }

    public void setPublicationDeliveryStructure(PublicationDeliveryStructure publicationDeliveryStructure) {
        this.publicationDeliveryStructure = publicationDeliveryStructure;
    }
}

