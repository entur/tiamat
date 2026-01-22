package org.rutebanken.tiamat.rest.write.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.rutebanken.netex.model.StopPlace;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StopPlace")
@XmlRootElement(
        name = "StopPlace",
        namespace = "http://www.netex.org.uk/netex"
)
public class StopPlaceDto extends StopPlace {}
