package org.rutebanken.tiamat.rest.write.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.rutebanken.netex.model.StopPlace;

import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(
    name = "stopPlaces",
    namespace = "http://www.netex.org.uk/netex"
)
public class StopPlacesDto {

    @XmlElement(name = "StopPlace", namespace = "http://www.netex.org.uk/netex")
    private List<StopPlace> stopPlaces;

    public List<StopPlace> getStopPlaces() {
        return stopPlaces;
    }

    public void setStopPlaces(List<StopPlace> stopPlaces) {
        this.stopPlaces = stopPlaces;
    }
}
