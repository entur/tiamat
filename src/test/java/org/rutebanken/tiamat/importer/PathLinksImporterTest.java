package org.rutebanken.tiamat.importer;

import org.junit.Test;
import org.rutebanken.tiamat.CommonSpringBootTest;
import org.rutebanken.tiamat.model.PathLink;
import org.rutebanken.tiamat.model.PathLinkEnd;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.repository.PathLinkRepository;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class PathLinksImporterTest extends CommonSpringBootTest{

    @Autowired
    private QuayRepository quayRepository;

    @Autowired
    private PathLinksImporter pathLinksImporter;

    @Test
    public void importPathLinks() throws Exception {
        PathLink pathLink = new PathLink(new PathLinkEnd(quayRepository.save(new Quay())), new PathLinkEnd(quayRepository.save(new Quay())));
        List<org.rutebanken.netex.model.PathLink> netexPathLinks =  pathLinksImporter.importPathLinks(Arrays.asList(pathLink));
        assertThat(netexPathLinks).isNotEmpty();
        assertThat(netexPathLinks).hasSize(1);
        org.rutebanken.netex.model.PathLink actualNetexPathLink = netexPathLinks.get(0);
        assertThat(actualNetexPathLink.getId()).isNotEmpty();
        assertThat(actualNetexPathLink.getFrom().getPlaceRef()).isNotNull();
        assertThat(actualNetexPathLink.getTo().getPlaceRef()).isNotNull();
    }

}