package org.rutebanken.tiamat.netex.mapping;

import org.rutebanken.netex.model.*;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class PublicationDeliveryHelper {

    public boolean hasTopographicPlaces(SiteFrame netexSiteFrame) {
        return netexSiteFrame.getTopographicPlaces() != null
                && netexSiteFrame.getTopographicPlaces().getTopographicPlace() != null
                && !netexSiteFrame.getTopographicPlaces().getTopographicPlace().isEmpty();
    }

    public List<TopographicPlace> extractTopographicPlaces(SiteFrame siteFrame) {
        if(siteFrame.getTopographicPlaces() != null && siteFrame.getTopographicPlaces().getTopographicPlace() != null) {
            return siteFrame.getTopographicPlaces().getTopographicPlace();
        } else {
            return new ArrayList<>();
        }
    }

    public boolean hasStops(SiteFrame siteFrame) {
        return siteFrame.getStopPlaces() != null && siteFrame.getStopPlaces().getStopPlace() != null;
    }

    public boolean hasTariffZones(SiteFrame netexSiteFrame) {
        return netexSiteFrame.getTariffZones() != null && netexSiteFrame.getTariffZones().getTariffZone() != null;
    }

    public boolean hasParkings(SiteFrame siteFrame) {
        return siteFrame.getParkings() != null && siteFrame.getParkings().getParking() != null;
    }

    public int numberOfStops(SiteFrame netexSiteFrame) {
        return hasStops(netexSiteFrame) ? netexSiteFrame.getStopPlaces().getStopPlace().size() : 0;
    }

    public SiteFrame findSiteFrame(PublicationDeliveryStructure incomingPublicationDelivery) {

        List<JAXBElement<? extends Common_VersionFrameStructure>> compositeFrameOrCommonFrame = incomingPublicationDelivery.getDataObjects().getCompositeFrameOrCommonFrame();

        Optional<SiteFrame> optionalSiteframe = compositeFrameOrCommonFrame
                .stream()
                .filter(element -> element.getValue() instanceof SiteFrame)
                .map(element -> (SiteFrame) element.getValue())
                .findFirst();

        if (optionalSiteframe.isPresent()) {
            return optionalSiteframe.get();
        }

        return compositeFrameOrCommonFrame
                .stream()
                .filter(element -> element.getValue() instanceof CompositeFrame)
                .map(element -> (CompositeFrame) element.getValue())
                .map(compositeFrame -> compositeFrame.getFrames())
                .flatMap(frames -> frames.getCommonFrame().stream())
                .filter(jaxbElement -> jaxbElement.getValue() instanceof SiteFrame)
                .map(jaxbElement -> (SiteFrame) jaxbElement.getValue())
                .findAny().get();
    }
}
