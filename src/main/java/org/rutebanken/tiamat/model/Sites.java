

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


public class Sites {

    protected StopPlaces stopPlaces;
    protected PointsOfInterest pointsOfInterest;
    protected Parkings parkings;

    public StopPlaces getStopPlaces() {
        return stopPlaces;
    }

    public void setStopPlaces(StopPlaces value) {
        this.stopPlaces = value;
    }

    public PointsOfInterest getPointsOfInterest() {
        return pointsOfInterest;
    }

    public void setPointsOfInterest(PointsOfInterest value) {
        this.pointsOfInterest = value;
    }

    public Parkings getParkings() {
        return parkings;
    }

    public void setParkings(Parkings value) {
        this.parkings = value;
    }

}
