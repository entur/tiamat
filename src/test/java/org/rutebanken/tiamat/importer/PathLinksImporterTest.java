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

import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.AddressablePlaceRefStructure;
import org.rutebanken.tiamat.model.PathLink;
import org.rutebanken.tiamat.model.PathLinkEnd;
import org.rutebanken.tiamat.model.Quay;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

    public class PathLinksImporterTest extends TiamatIntegrationTest {

    @Autowired
    private PathLinksImporter pathLinksImporter;

    private AtomicInteger pathLinkCounter = new AtomicInteger();

    @Test
    public void importPathLinks() throws Exception {
        PathLink pathLink = new PathLink(new PathLinkEnd(new AddressablePlaceRefStructure(quayRepository.save(new Quay()))), new PathLinkEnd(new AddressablePlaceRefStructure(quayRepository.save(new Quay()))));
        List<org.rutebanken.netex.model.PathLink> netexPathLinks =  pathLinksImporter.importPathLinks(Arrays.asList(pathLink), pathLinkCounter);
        assertThat(netexPathLinks).isNotEmpty();
        assertThat(netexPathLinks).hasSize(1);
        org.rutebanken.netex.model.PathLink actualNetexPathLink = netexPathLinks.getFirst();
        assertThat(actualNetexPathLink.getId()).isNotEmpty();
        assertThat(actualNetexPathLink.getFrom().getPlaceRef()).isNotNull();
        assertThat(actualNetexPathLink.getTo().getPlaceRef()).isNotNull();
    }

    @Test
    public void shouldNotSaveDuplicatePathLinks() throws Exception {
        PathLink pathLink = new PathLink(new PathLinkEnd(new AddressablePlaceRefStructure(quayRepository.save(new Quay()))), new PathLinkEnd(new AddressablePlaceRefStructure(quayRepository.save(new Quay()))));

        pathLink.getOriginalIds().add("originalID");
        List<org.rutebanken.netex.model.PathLink> firsts = pathLinksImporter.importPathLinks(Arrays.asList(pathLink), pathLinkCounter);

        PathLink pathLink2 = new PathLink(new PathLinkEnd(new AddressablePlaceRefStructure(quayRepository.save(new Quay()))), new PathLinkEnd(new AddressablePlaceRefStructure(quayRepository.save(new Quay()))));
        pathLink2.getOriginalIds().add("originalID");
        List<org.rutebanken.netex.model.PathLink> seconds = pathLinksImporter.importPathLinks(Arrays.asList(pathLink2), pathLinkCounter);

        assertThat(firsts).hasSize(1);
        assertThat(seconds).hasSize(1);
        assertThat(firsts.getFirst().getId()).isEqualTo(seconds.getFirst().getId());
    }

    @Test
    public void shouldResolveForeignPlaceReferences() {

        Quay fromQuay = new Quay();
        String fromQuayOriginalId = "RUT:Quay:123";
        fromQuay.getOriginalIds().add(fromQuayOriginalId);

        quayRepository.save(fromQuay);

        Quay toQuay = new Quay();
        String toQuayOriginalId = "RUT:Quay:321";
        toQuay.getOriginalIds().add(toQuayOriginalId);

        quayRepository.save(toQuay);

        AddressablePlaceRefStructure fromPlaceRef = new AddressablePlaceRefStructure(fromQuayOriginalId, null);
        PathLinkEnd pathLinkEndFrom = new PathLinkEnd(fromPlaceRef);

        AddressablePlaceRefStructure toPlaceRef = new AddressablePlaceRefStructure(toQuayOriginalId, null);
        PathLinkEnd pathLinkEndTo = new PathLinkEnd(toPlaceRef);

        PathLink pathLink = new PathLink(pathLinkEndFrom, pathLinkEndTo);

        List<org.rutebanken.netex.model.PathLink> result = pathLinksImporter.importPathLinks(Arrays.asList(pathLink), pathLinkCounter);

        org.rutebanken.netex.model.PathLink actual = result.getFirst();
        assertThat(actual.getFrom().getPlaceRef().getRef()).contains("NSR:Quay:");
        assertThat(actual.getTo().getPlaceRef().getRef()).contains("NSR:Quay:");


    }
}