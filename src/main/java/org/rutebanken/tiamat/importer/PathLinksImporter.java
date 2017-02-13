package org.rutebanken.tiamat.importer;

import org.rutebanken.tiamat.model.SiteFrame;
import org.rutebanken.tiamat.repository.PathLinkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PathLinksImporter {

    private static final Logger logger = LoggerFactory.getLogger(PathLinksImporter.class);

    private final PathLinkRepository pathLinkRepository;

    public PathLinksImporter(PathLinkRepository pathLinkRepository) {
        this.pathLinkRepository = pathLinkRepository;
    }

    public SiteFrame importPathLinks(SiteFrame tiamatSiteFrame) {


        tiamatSiteFrame.getPathLinks().getPathLink().forEach(pathLink -> {
            logger.debug("Importing path link {}", pathLink);




        });

        return tiamatSiteFrame;

    }

}
