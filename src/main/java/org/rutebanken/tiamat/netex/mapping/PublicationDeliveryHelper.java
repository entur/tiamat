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

package org.rutebanken.tiamat.netex.mapping;

import jakarta.xml.bind.JAXBElement;
import org.rutebanken.netex.model.Common_VersionFrameStructure;
import org.rutebanken.netex.model.CompositeFrame;
import org.rutebanken.netex.model.Composite_VersionFrameStructure;
import org.rutebanken.netex.model.DataManagedObjectStructure;
import org.rutebanken.netex.model.FareFrame;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.TopographicPlace;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.ORIGINAL_ID_KEY;

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
        return siteFrame.getStopPlaces() != null && siteFrame.getStopPlaces().getStopPlace_() != null;
    }

    public boolean hasTariffZones(SiteFrame netexSiteFrame) {
        return netexSiteFrame.getTariffZones() != null && netexSiteFrame.getTariffZones().getTariffZone() != null;
    }

    public boolean hasPathLinks(SiteFrame netexSiteFrame) {
        return netexSiteFrame.getPathLinks() != null && netexSiteFrame.getPathLinks().getPathLink() != null;
    }

    public boolean hasParkings(SiteFrame siteFrame) {
        return siteFrame.getParkings() != null && siteFrame.getParkings().getParking() != null;
    }

    public int numberOfStops(SiteFrame netexSiteFrame) {
        return hasStops(netexSiteFrame) ? netexSiteFrame.getStopPlaces().getStopPlace_().size() : 0;
    }

    public SiteFrame findSiteFrame(PublicationDeliveryStructure incomingPublicationDelivery) {

        List<JAXBElement<? extends Common_VersionFrameStructure>> compositeFrameOrCommonFrame = incomingPublicationDelivery.getDataObjects().getCompositeFrameOrCommonFrame();

        Optional<SiteFrame> optionalSiteframe = compositeFrameOrCommonFrame
                .stream()
                .filter(element -> element.getValue() instanceof SiteFrame)
                .map(element -> (SiteFrame) element.getValue())
                .findFirst();

        return optionalSiteframe.orElseGet(() -> compositeFrameOrCommonFrame
                .stream()
                .filter(element -> element.getValue() instanceof CompositeFrame)
                .map(element -> (CompositeFrame) element.getValue())
                .map(Composite_VersionFrameStructure::getFrames)
                .flatMap(frames -> frames.getCommonFrame().stream())
                .filter(jaxbElement -> jaxbElement.getValue() instanceof SiteFrame)
                .map(jaxbElement -> (SiteFrame) jaxbElement.getValue())
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("No SiteFrame found in PublicationDelivery. The delivery must contain either a direct SiteFrame or a SiteFrame within a CompositeFrame.")));

    }

    public FareFrame findFareFrame(PublicationDeliveryStructure incomingPublicationDelivery) {

        List<JAXBElement<? extends Common_VersionFrameStructure>> compositeFrameOrCommonFrame = incomingPublicationDelivery.getDataObjects().getCompositeFrameOrCommonFrame();

        Optional<FareFrame> optionalFareFrame = compositeFrameOrCommonFrame
                .stream()
                .filter(element -> element.getValue() instanceof FareFrame)
                .map(element -> (FareFrame) element.getValue())
                .findFirst();

        return optionalFareFrame.orElseGet(() -> compositeFrameOrCommonFrame
                .stream()
                .filter(element -> element.getValue() instanceof CompositeFrame)
                .map(element -> (CompositeFrame) element.getValue())
                .map(Composite_VersionFrameStructure::getFrames)
                .flatMap(frames -> frames.getCommonFrame().stream())
                .filter(jaxbElement -> jaxbElement.getValue() instanceof FareFrame)
                .map(jaxbElement -> (FareFrame) jaxbElement.getValue())
                .findAny()
                .orElse(null));

    }

    public boolean hasFareZonesInFareFrame(FareFrame fareFrame) {
        return fareFrame != null
                && fareFrame.getFareZones() != null
                && fareFrame.getFareZones().getFareZone() != null
                && !fareFrame.getFareZones().getFareZone().isEmpty();
    }

    public Set<String> getImportedIds(DataManagedObjectStructure dataManagedObject) {

        return Stream.of(dataManagedObject)
                .filter(Objects::nonNull)
                .map(object -> object.getKeyList())
                .flatMap(keyList -> keyList.getKeyValue().stream())
                .filter(keyValueStructure -> keyValueStructure.getKey().equals(ORIGINAL_ID_KEY))
                .map(keyValue -> keyValue.getValue())
                .map(value -> value.split(","))
                .flatMap(values -> Stream.of(values))
                .collect(toSet());
    }

    public String getValueByKey(DataManagedObjectStructure dataManagedObject, String key) {

        return Stream.of(dataManagedObject)
                .filter(Objects::nonNull)
                .map(object -> object.getKeyList())
                .filter(Objects::nonNull)
                .flatMap(keyList -> keyList.getKeyValue().stream())
                .filter(keyValueStructure -> keyValueStructure.getKey().equals(key))
                .map(keyValue -> keyValue.getValue())
                .flatMap(values -> Stream.of(values))
                .findFirst().orElse(null);
    }

    public boolean hasGroupOfTariffZones(SiteFrame netexSiteFrame) {
        return netexSiteFrame.getGroupsOfTariffZones() != null
                && netexSiteFrame.getGroupsOfTariffZones().getGroupOfTariffZones() != null;
    }
}
