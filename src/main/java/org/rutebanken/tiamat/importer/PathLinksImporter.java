package org.rutebanken.tiamat.importer;

import org.rutebanken.tiamat.model.PathLink;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.PathLinkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class PathLinksImporter {

    private static final Logger logger = LoggerFactory.getLogger(PathLinksImporter.class);

    private final PathLinkRepository pathLinkRepository;

    private final NetexMapper netexMapper;

    public PathLinksImporter(PathLinkRepository pathLinkRepository, NetexMapper netexMapper) {
        this.pathLinkRepository = pathLinkRepository;
        this.netexMapper = netexMapper;
    }

    public List<org.rutebanken.netex.model.PathLink> importPathLinks(List<PathLink> pathLinks) {
        return pathLinks.stream()
                .peek(pathLink -> logger.debug("Save {}", pathLink))
                .map(pathLink -> pathLinkRepository.save(pathLink))
                .map(pathLink -> netexMapper.mapToNetexModel(pathLink))
                .collect(toList());
    }

}
