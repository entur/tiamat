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

package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import net.opengis.gml._3.DirectPositionListType;
import net.opengis.gml._3.LineStringType;
import org.junit.Test;
import org.rutebanken.netex.model.LocationStructure;
import org.rutebanken.netex.model.PathDirectionEnumeration;
import org.rutebanken.netex.model.PathLink;
import org.rutebanken.netex.model.PathLinkEndStructure;
import org.rutebanken.netex.model.PlaceRefStructure;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.SimplePoint_VersionStructure;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.TransferDurationStructure;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.config.GeometryFactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class PathLinkImportTest extends TiamatIntegrationTest {

    @Autowired
    private PublicationDeliveryTestHelper publicationDeliveryTestHelper;

    @Test
    public void publicationDeliveryWithPathLink() throws Exception {

        StopPlace fromStopPlace = new StopPlace()
                .withId("RUT:StopPlace:123123")
                .withVersion("1")
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("9"))
                                .withLongitude(new BigDecimal("71"))));

        StopPlace toStopPlace = new StopPlace()
                .withId("RUT:StopPlace:321654")
                .withVersion("1")
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("9.6"))
                                .withLongitude(new BigDecimal("76"))));

        LineStringType lineStringType = new LineStringType()
                .withId("LineString")
                .withPosList(new DirectPositionListType()
                        .withSrsDimension(BigInteger.valueOf(new GeometryFactoryConfig().geometryFactory().getSRID()))
                        .withValue(9.1,
                                71.1,
                                9.5,
                                74.1));

        Duration duration = Duration.ofMillis(10000);

        PathLink netexPathLink = new PathLink()
                .withId("NRI:ConnectionLink:762130479_762130479")
                .withVersion("1")
                .withAllowedUse(PathDirectionEnumeration.TWO_WAY)
                .withTransferDuration(new TransferDurationStructure()
                        .withDefaultDuration(duration))
                .withLineString(lineStringType)
                .withFrom(
                        new PathLinkEndStructure()
                                .withPlaceRef(
                                        new PlaceRefStructure()
                                                .withRef(fromStopPlace.getId())))
                .withTo(
                        new PathLinkEndStructure()
                                .withPlaceRef(
                                        new PlaceRefStructure()
                                                .withRef(toStopPlace.getId())
                                                .withVersion("1")));

        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(fromStopPlace, toStopPlace);
        publicationDeliveryTestHelper.addPathLinks(publicationDelivery, netexPathLink);

        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);

        List<PathLink> result = publicationDeliveryTestHelper.extractPathLinks(response);
        assertThat(result).as("Expecting path link in return").hasSize(1);
        PathLink importedPathLink = result.getFirst();
        assertThat(importedPathLink.getAllowedUse()).isEqualTo(netexPathLink.getAllowedUse());
        assertThat(importedPathLink.getFrom().getPlaceRef().getRef()).contains(fromStopPlace.getClass().getSimpleName());
        assertThat(importedPathLink.getTo().getPlaceRef().getRef()).contains(toStopPlace.getClass().getSimpleName());
        assertThat(importedPathLink.getTransferDuration().getDefaultDuration()).isEqualTo(duration);

        assertThat(importedPathLink.getLineString()).isNotNull();
        assertThat(importedPathLink.getLineString().getPosList()).isNotNull();
        assertThat(importedPathLink.getLineString().getPosList().getValue()).hasSize(4);
    }
}
