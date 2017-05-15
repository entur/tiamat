package org.rutebanken.tiamat.rest.netex.publicationdelivery;

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

