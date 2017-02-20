package org.rutebanken.tiamat.importer;

import org.rutebanken.tiamat.model.PathLink;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.repository.PathLinkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class PathLinksImporter {

    private static final Logger logger = LoggerFactory.getLogger(PathLinksImporter.class);

    private final PathLinkRepository pathLinkRepository;

    private final NetexMapper netexMapper;

    private final KeyValueListAppender keyValueListAppender;

    private final VersionIncrementor versionIncrementor;

    @Autowired
    public PathLinksImporter(PathLinkRepository pathLinkRepository, NetexMapper netexMapper, KeyValueListAppender keyValueListAppender, VersionIncrementor versionIncrementor) {
        this.pathLinkRepository = pathLinkRepository;
        this.netexMapper = netexMapper;
        this.keyValueListAppender = keyValueListAppender;
        this.versionIncrementor = versionIncrementor;
    }

    public List<org.rutebanken.netex.model.PathLink> importPathLinks(List<PathLink> pathLinks) {



        return pathLinks.stream()
                .peek(pathLink -> logger.debug("Importing path link {}", pathLink))
                .map(pathLink -> {
                    Optional<PathLink> existingPathLink = findExistingPathLinkIfPresent(pathLink);
                    if(existingPathLink.isPresent()) {
                        keyValueListAppender.appendToOriginalId(NetexIdMapper.ORIGINAL_ID_KEY, pathLink, existingPathLink.get());
                        return existingPathLink.get();
                    } else {
                        logger.debug("No existing path link. Using incoming {}", pathLink);
                        return pathLink;
                    }
                })
                .map(pathLink -> {
                    versionIncrementor.incrementVersion(pathLink);
                    return pathLink;
                })
                .map(pathLink -> pathLinkRepository.save(pathLink))
                .map(pathLink -> netexMapper.mapToNetexModel(pathLink))
                .collect(toList());
    }

    private Optional<PathLink> findExistingPathLinkIfPresent(PathLink incomingPathLink) {
        Optional<PathLink> existingPathLink = Optional.empty();
        if(incomingPathLink.getId() != null) {
            existingPathLink = Optional.of(pathLinkRepository.findOne(incomingPathLink.getId()));
            logger.info("Found existing existing path link from incoming ID {}", existingPathLink);

        } else if(!incomingPathLink.getOriginalIds().isEmpty()) {
            Long existingPathLinkId = pathLinkRepository.findByKeyValue(NetexIdMapper.ORIGINAL_ID_KEY, incomingPathLink.getOriginalIds());
            if(existingPathLinkId != null) {
                existingPathLink = Optional.of(pathLinkRepository.findOne(existingPathLinkId));
                logger.info("Found existing existing path link from original id. {}", existingPathLink);
            }
        }

        return existingPathLink;

    }

}
