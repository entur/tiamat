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

import org.junit.Before;
import org.junit.Test;
import org.rutebanken.netex.model.CompositeFrame;
import org.rutebanken.netex.model.Frames_RelStructure;
import org.rutebanken.netex.model.KeyListStructure;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.netex.model.LocationStructure;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.Quays_RelStructure;
import org.rutebanken.netex.model.SimplePoint_VersionStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.SiteRefStructure;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.PublicationDeliveryTestHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.ORIGINAL_ID_KEY;

@Transactional
public class PublicationDeliveryImporterTest extends TiamatIntegrationTest {

    @Autowired
    private PublicationDeliveryHelper publicationDeliveryHelper;

    @Autowired
    private PublicationDeliveryTestHelper publicationDeliveryTestHelper;

    @Autowired
    private PublicationDeliveryImporter publicationDeliveryImporter;

    @Before
    public void setUp() {
        setUpSecurityContext();
    }

    @Test
    public void importPublicationDelivery() {
        StopPlace parentStop = new StopPlace()
                .withId("NSR:StopPlace:01")
                .withVersion("1")
                .withKeyList(new KeyListStructure()
                        .withKeyValue(new KeyValueStructure()
                                .withKey(ORIGINAL_ID_KEY)
                                .withValue("RANDOM:ACB:01")))
                .withName(new MultilingualString().withValue("ParentStop").withLang("nb"))
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("9"))
                                .withLongitude(new BigDecimal("71"))));

        StopPlace parentStopB = new StopPlace()
                .withId("NSR:StopPlace:02")
                .withVersion("1")
                .withKeyList(new KeyListStructure()
                        .withKeyValue(new KeyValueStructure()
                                .withKey(ORIGINAL_ID_KEY)
                                .withValue("RANDOM:ABC:02")))
                .withName(new MultilingualString().withValue("ParentStopB").withLang("nb"))
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("9"))
                                .withLongitude(new BigDecimal("71"))));

        StopPlace childStopA = new StopPlace()
                .withId("NSR:StopPlace:04")
                .withVersion("1")
                .withName(new MultilingualString().withValue("ChildStop A").withLang("nb"))
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("9"))
                                .withLongitude(new BigDecimal("71"))))
                .withParentSiteRef(new SiteRefStructure().withRef("NSR:StopPlace:01").withVersion(parentStop.getVersion()))
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
                .withId("NSR:StopPlace:05")
                .withVersion("1")
                .withName(new MultilingualString().withValue("ChildStop B").withLang("nb"))
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("9"))
                                .withLongitude(new BigDecimal("71"))))
                .withParentSiteRef(new SiteRefStructure().withRef("NSR:StopPlace:01").withVersion(parentStop.getVersion()))
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                .withId("XYZ:01:02")
                                .withVersion("1")
                                .withName(new MultilingualString().withValue("B"))
                                .withCentroid(new SimplePoint_VersionStructure()
                                        .withLocation(new LocationStructure()
                                                .withLatitude(new BigDecimal("9.2"))
                                                .withLongitude(new BigDecimal("71.3"))))));

        StopPlace childStopC = new StopPlace()
                .withId("NSR:StopPlace:03")
                .withVersion("1")
                .withName(new MultilingualString().withValue("ChildStop C").withLang("nb"))
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("9"))
                                .withLongitude(new BigDecimal("11"))))
                .withParentSiteRef(new SiteRefStructure().withRef("NSR:StopPlace:02").withVersion(parentStop.getVersion()))
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                .withId("XYZ:02:01")
                                .withVersion("1")
                                .withName(new MultilingualString().withValue("C"))
                                .withCentroid(new SimplePoint_VersionStructure()
                                        .withLocation(new LocationStructure()
                                                .withLatitude(new BigDecimal("9.1"))
                                                .withLongitude(new BigDecimal("71.2"))))));

        ImportParams importParams = new ImportParams();
        importParams.importType = ImportType.INITIAL;

        PublicationDeliveryStructure delivery = publicationDeliveryTestHelper
                .createPublicationDeliveryWithStopPlace(parentStop, childStopA, childStopB, parentStopB, childStopC);
        PublicationDeliveryStructure response = publicationDeliveryImporter.importPublicationDelivery(delivery, importParams);

        assertThat(response).isNotNull();
        List<org.rutebanken.tiamat.model.StopPlace> allStops = stopPlaceRepository.findAll();

        assertThat(allStops).hasSize(5);

        assertThat(allStops)
                .extracting(s -> s.getName().getValue())
                .containsOnly("ParentStop", "ChildStop A", "ChildStop B", "ParentStopB", "ChildStop C");

        List<org.rutebanken.tiamat.model.StopPlace> savedParentStop = stopPlaceRepository.findByNetexId("NSR:StopPlace:01");
        assertThat(savedParentStop).hasSize(1);
        assertThat(savedParentStop.getFirst().isParentStopPlace()).isEqualTo(true);
        assertThat(savedParentStop.getFirst().getChildren()).hasSize(2);
        assertThat(savedParentStop.getFirst().getChildren())
                .extracting(childStop -> childStop.getParentSiteRef().getRef())
                .containsOnly("NSR:StopPlace:01");

        List<org.rutebanken.tiamat.model.StopPlace> savedParentStopB = stopPlaceRepository.findByNetexId("NSR:StopPlace:02");
        assertThat(savedParentStopB).hasSize(1);
        assertThat(savedParentStopB.getFirst().isParentStopPlace()).isEqualTo(true);
        assertThat(savedParentStopB.getFirst().getChildren()).hasSize(1);
        assertThat(savedParentStopB.getFirst().getChildren())
                .extracting(childStop -> childStop.getParentSiteRef().getRef())
                .containsOnly("NSR:StopPlace:02");
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