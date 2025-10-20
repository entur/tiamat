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

package org.rutebanken.tiamat.importer.handler;

//import org.rutebanken.netex.model.PathLinksInFrame_RelStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.tiamat.importer.ImportParams;
import org.rutebanken.tiamat.importer.PathLinksImporter;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class PathLinkImportHandler {

    private static final Logger logger = LoggerFactory.getLogger(PathLinkImportHandler.class);


    private final NetexMapper netexMapper;

    private final PathLinksImporter pathLinksImporter;

    public PathLinkImportHandler(NetexMapper netexMapper, PathLinksImporter pathLinksImporter) {
        this.netexMapper = netexMapper;
        this.pathLinksImporter = pathLinksImporter;
    }

    public void handlePathLinks(SiteFrame netexSiteFrame, ImportParams importParams, AtomicInteger pathLinkCounter, SiteFrame responseSiteframe) {
        if (netexSiteFrame.getPathLinks() != null) {// && netexSiteFrame.getPathLinks().getPathLink() != null) { TODO
//            List<org.rutebanken.tiamat.model.PathLink> tiamatPathLinks = netexMapper.mapPathLinksToTiamatModel(netexSiteFrame.getPathLinks().getPathLink());
//            tiamatPathLinks.forEach(tiamatPathLink -> logger.debug("Received path link: {}", tiamatPathLink));
//
//            List<org.rutebanken.netex.model.PathLink> pathLinks = pathLinksImporter.importPathLinks(tiamatPathLinks, pathLinkCounter);
//            responseSiteframe.withPathLinks(new PathLinksInFrame_RelStructure().withPathLink(pathLinks));
        }
    }

}
