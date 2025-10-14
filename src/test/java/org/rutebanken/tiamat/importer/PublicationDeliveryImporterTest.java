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

package org.rutebanken.tiamat.importer;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Before;
import org.junit.Test;
import org.rutebanken.netex.model.*;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.importer.finder.StopPlaceBySomethingFinder;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.PublicationDeliveryTestHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.ORIGINAL_ID_KEY;

public class PublicationDeliveryImporterTest extends TiamatIntegrationTest {

    @Autowired
    private PublicationDeliveryHelper publicationDeliveryHelper;

    @Autowired
    private PublicationDeliveryTestHelper publicationDeliveryTestHelper;

    @Autowired
    private PublicationDeliveryImporter publicationDeliveryImporter;

    @Autowired
    private StopPlaceBySomethingFinder finder;

    @Before
    public void setUp() {
        setUpSecurityContext();
    }

    @Test
    public void importPublicationDelivery() {

        StopPlace parentStop = new StopPlace()
                .withId("ID:12345:01")
                .withVersion("1")
                .withKeyList(new KeyListStructure()
                        .withKeyValue(new KeyValueStructure()
                                .withKey(ORIGINAL_ID_KEY)
                                .withValue("003:9021003779347000,050:StopPlace:73423_2")))
                .withName(new MultilingualString().withValue("ParentStop").withLang("nb"))
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("9"))
                                .withLongitude(new BigDecimal("71"))));

        StopPlace parentStopB = new StopPlace()
                .withId("ID:34567:01")
                .withVersion("1")
                .withName(new MultilingualString().withValue("ParentStopB").withLang("nb"))
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("9"))
                                .withLongitude(new BigDecimal("71"))));

        StopPlace childStopC = new StopPlace()
                .withId("ID:45678:02")
                .withVersion("1")
                .withName(new MultilingualString().withValue("ChildStop C").withLang("nb"))
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("9"))
                                .withLongitude(new BigDecimal("11"))))
                .withParentSiteRef(new SiteRefStructure().withRef("ID:34567:01").withVersion(parentStop.getVersion()))
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                .withId("XYZ:02:01")
                                .withVersion("1")
                                .withName(new MultilingualString().withValue("C"))
                                .withCentroid(new SimplePoint_VersionStructure()
                                        .withLocation(new LocationStructure()
                                                .withLatitude(new BigDecimal("9.1"))
                                                .withLongitude(new BigDecimal("71.2"))))));

        StopPlace childStopA = new StopPlace()
                .withId("ID:23456:02")
                .withVersion("1")
                .withName(new MultilingualString().withValue("ChildStop A").withLang("nb"))
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("9"))
                                .withLongitude(new BigDecimal("71"))))
                .withParentSiteRef(new SiteRefStructure().withRef("ID:12345:01").withVersion(parentStop.getVersion()))
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                .withId("XYZ:01:03")
                                .withVersion("1")
                                .withName(new MultilingualString().withValue("A"))
                                .withCentroid(new SimplePoint_VersionStructure()
                                        .withLocation(new LocationStructure()
                                                .withLatitude(new BigDecimal("9.1"))
                                                .withLongitude(new BigDecimal("71.2"))))));

        StopPlace childStopB = new StopPlace()
                .withId("ID:23456:03")
                .withVersion("1")
                .withName(new MultilingualString().withValue("ChildStop B").withLang("nb"))
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("9"))
                                .withLongitude(new BigDecimal("71"))))
                .withParentSiteRef(new SiteRefStructure().withRef("ID:12345:01").withVersion(parentStop.getVersion()))
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                .withId("XYZ:01:02")
                                .withVersion("1")
                                .withName(new MultilingualString().withValue("B"))
                                .withCentroid(new SimplePoint_VersionStructure()
                                        .withLocation(new LocationStructure()
                                                .withLatitude(new BigDecimal("9.2"))
                                                .withLongitude(new BigDecimal("71.3"))))));

        ImportParams importParams = new ImportParams();
        importParams.importType = ImportType.INITIAL;

        PublicationDeliveryStructure delivery = publicationDeliveryTestHelper
                .createPublicationDeliveryWithStopPlace(parentStop, childStopA, childStopB, parentStopB, childStopC);
        PublicationDeliveryStructure response = publicationDeliveryImporter.importPublicationDelivery(delivery, importParams);

        AssertionsForClassTypes.assertThat(response).isNotNull();
        List<org.rutebanken.tiamat.model.StopPlace> allStops = stopPlaceRepository.findAll();

        assertThat(allStops)
                .extracting(s -> s.getName().getValue())
                .containsExactlyInAnyOrder("ParentStop", "ChildStop A", "ChildStop B", "ParentStopB", "ChildStop C");

        assertThat(allStops)
                .extracting(org.rutebanken.tiamat.model.StopPlace::isParentStopPlace)
                .containsExactlyInAnyOrder(true, false, false, true, false);

        Map<Boolean, List<org.rutebanken.tiamat.model.StopPlace>> stopsByIsParent = allStops.stream()
                .collect(Collectors.groupingBy(org.rutebanken.tiamat.model.StopPlace::isParentStopPlace));

        assertThat(stopsByIsParent.get(true))
                .hasSize(2)
                .extracting(org.rutebanken.tiamat.model.StopPlace::getParentSiteRef)
                .allMatch(Objects::isNull);

//        assertThat(stopsByIsParent.get(false))
//                .hasSize(3)
//                .extracting(stopPlace -> stopPlace.getParentSiteRef().getRef())
//                .containsExactlyInAnyOrder(stopsByIsParent.get(true).stream().map(i -> i.getParentSiteRef().getRef()).collect(Collectors.toUnmodifiableList()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findSiteFrameFromCompositeFrame() {
        ObjectFactory objectFactory = new ObjectFactory();

        PublicationDeliveryStructure publicationDeliveryStructure = new PublicationDeliveryStructure()
                .withDataObjects(
                        new PublicationDeliveryStructure.DataObjects()
                                .withCompositeFrameOrCommonFrame(
                                        objectFactory.createCompositeFrame(
                                                new CompositeFrame()
                                                        .withFrames(new Frames_RelStructure()
                                                                .withCommonFrame(objectFactory.createCommonFrame(new SiteFrame()))))));

        SiteFrame siteFrame = publicationDeliveryHelper.findSiteFrame(publicationDeliveryStructure);
        assertThat(siteFrame).isNotNull();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findSiteFrameFromCommonFrame() {
        ObjectFactory objectFactory = new ObjectFactory();

        PublicationDeliveryStructure publicationDeliveryStructure = new PublicationDeliveryStructure()
                .withDataObjects(
                        new PublicationDeliveryStructure.DataObjects()
                                .withCompositeFrameOrCommonFrame(
                                        objectFactory.createCommonFrame(new SiteFrame())));

        SiteFrame siteFrame = publicationDeliveryHelper.findSiteFrame(publicationDeliveryStructure);
        assertThat(siteFrame).isNotNull();
    }

    private void setUpSecurityContext() {
        // Create a Jwt with claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "testuser");
        claims.put("scope", "ROLE_USER");  // Or other relevant scopes/roles

        // Create a Jwt instance
        Jwt jwt = new Jwt(
                "tokenValue",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                claims
        );

        final AbstractAuthenticationToken authToken = new JwtAuthenticationToken(jwt, Collections.singleton(new SimpleGrantedAuthority("ROLE_EDIT_STOPS")));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}