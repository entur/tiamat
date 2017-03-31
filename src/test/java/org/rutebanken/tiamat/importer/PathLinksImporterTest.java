package org.rutebanken.tiamat.importer;

import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.model.AddressablePlaceRefStructure;
import org.rutebanken.tiamat.model.PathLink;
import org.rutebanken.tiamat.model.Quay;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.rutebanken.tiamat.model.VersionOfObjectRefStructure.ANY_VERSION;

public class PathLinksImporterTest extends TiamatIntegrationTest {

    @Autowired
    private PathLinksImporter pathLinksImporter;

    @Test
    public void importPathLinks() throws Exception {
        PathLink pathLink = new PathLink(new PathLinkEnd(new AddressablePlaceRefStructure(quayRepository.save(new Quay()))), new PathLinkEnd(new AddressablePlaceRefStructure(quayRepository.save(new Quay()))));
        List<org.rutebanken.netex.model.PathLink> netexPathLinks =  pathLinksImporter.importPathLinks(Arrays.asList(pathLink));
        assertThat(netexPathLinks).isNotEmpty();
        assertThat(netexPathLinks).hasSize(1);
        org.rutebanken.netex.model.PathLink actualNetexPathLink = netexPathLinks.get(0);
        assertThat(actualNetexPathLink.getId()).isNotEmpty();
        assertThat(actualNetexPathLink.getFrom().getPlaceRef()).isNotNull();
        assertThat(actualNetexPathLink.getTo().getPlaceRef()).isNotNull();
    }

    @Test
    public void shouldNotSaveDuplicatePathLinks() throws Exception {
        PathLink pathLink = new PathLink(new PathLinkEnd(new AddressablePlaceRefStructure(quayRepository.save(new Quay()))), new PathLinkEnd(new AddressablePlaceRefStructure(quayRepository.save(new Quay()))));

        pathLink.getOriginalIds().add("originalID");
        List<org.rutebanken.netex.model.PathLink> firsts = pathLinksImporter.importPathLinks(Arrays.asList(pathLink));

        PathLink pathLink2 = new PathLink(new PathLinkEnd(new AddressablePlaceRefStructure(quayRepository.save(new Quay()))), new PathLinkEnd(new AddressablePlaceRefStructure(quayRepository.save(new Quay()))));
        pathLink2.getOriginalIds().add("originalID");
        List<org.rutebanken.netex.model.PathLink> seconds = pathLinksImporter.importPathLinks(Arrays.asList(pathLink2));

        assertThat(firsts).hasSize(1);
        assertThat(seconds).hasSize(1);
        assertThat(firsts.get(0).getId()).isEqualTo(seconds.get(0).getId());
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

        AddressablePlaceRefStructure fromPlaceRef = new AddressablePlaceRefStructure(fromQuayOriginalId, ANY_VERSION);
        PathLinkEnd pathLinkEndFrom = new PathLinkEnd(fromPlaceRef);

        AddressablePlaceRefStructure toPlaceRef = new AddressablePlaceRefStructure(toQuayOriginalId, ANY_VERSION);
        PathLinkEnd pathLinkEndTo = new PathLinkEnd(toPlaceRef);

        PathLink pathLink = new PathLink(pathLinkEndFrom, pathLinkEndTo);

        List<org.rutebanken.netex.model.PathLink> result = pathLinksImporter.importPathLinks(Arrays.asList(pathLink));

        org.rutebanken.netex.model.PathLink actual = result.get(0);
        assertThat(actual.getFrom().getPlaceRef().getRef()).contains("NSR:Quay:");
        assertThat(actual.getTo().getPlaceRef().getRef()).contains("NSR:Quay:");


    }
}