package org.rutebanken.tiamat.importer;

import org.rutebanken.tiamat.model.AddressablePlace;
import org.rutebanken.tiamat.model.AddressablePlaceRefStructure;
import org.rutebanken.tiamat.model.PathLink;
import org.rutebanken.tiamat.model.PathLinkEnd;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.repository.PathLinkRepository;
import org.rutebanken.tiamat.repository.ReferenceResolver;
import org.rutebanken.tiamat.versioning.VersionIncrementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class PathLinksImporter {

    private static final Logger logger = LoggerFactory.getLogger(PathLinksImporter.class);

    private final PathLinkRepository pathLinkRepository;

    private final ReferenceResolver referenceResolver;

    private final NetexMapper netexMapper;

    private final KeyValueListAppender keyValueListAppender;

    private final VersionIncrementor versionIncrementor;

    @Autowired
    public PathLinksImporter(PathLinkRepository pathLinkRepository, ReferenceResolver referenceResolver, NetexMapper netexMapper, KeyValueListAppender keyValueListAppender, VersionIncrementor versionIncrementor) {
        this.pathLinkRepository = pathLinkRepository;
        this.referenceResolver = referenceResolver;
        this.netexMapper = netexMapper;
        this.keyValueListAppender = keyValueListAppender;
        this.versionIncrementor = versionIncrementor;
    }

    public List<org.rutebanken.netex.model.PathLink> importPathLinks(List<PathLink> pathLinks) {

        return pathLinks.stream()
                .peek(pathLink -> logger.debug("Importing path link {}", pathLink))
                .map(pathLink -> {
                    Optional<PathLink> optionalPathLink = findExistingPathLinkIfPresent(pathLink);
                    if(optionalPathLink.isPresent()) {
                        PathLink existing = optionalPathLink.get();
                        boolean changed = keyValueListAppender.appendToOriginalId(NetexIdMapper.ORIGINAL_ID_KEY, pathLink, existing);
                        if(changed) {
                            existing.setChanged(ZonedDateTime.now());
                        }
                        // Update place ref?
                        versionIncrementor.incrementVersion(existing);
                        return existing;
                    } else {
                        logger.debug("No existing path link. Using incoming {}", pathLink);
                        pathLink.setCreated(ZonedDateTime.now());
                        pathLink.setVersion(VersionIncrementor.INITIAL_VERSION);

                        resolveAndFixPlaceRefs(pathLink.getFrom());
                        resolveAndFixPlaceRefs(pathLink.getTo());

                        return pathLink;
                    }
                })
                .map(pathLink -> pathLinkRepository.save(pathLink))
                .map(pathLink -> netexMapper.mapToNetexModel(pathLink))
                .collect(toList());
    }

    private void resolveAndFixPlaceRefs(PathLinkEnd pathLinkEnd) {
        if(pathLinkEnd == null || pathLinkEnd.getPlaceRef() == null) {
            throw new IllegalArgumentException("Cannot import path link without pathlink end with from/to place ref: "+pathLinkEnd);
        }
        AddressablePlace addressablePlace = referenceResolver.resolve(pathLinkEnd.getPlaceRef());
        if (addressablePlace == null) {
            throw new IllegalArgumentException("Cannot resolve " + pathLinkEnd.getPlaceRef());
        }
        pathLinkEnd.setPlaceRef(new AddressablePlaceRefStructure(addressablePlace));
    }

    private Optional<PathLink> findExistingPathLinkIfPresent(PathLink incomingPathLink) {
        Optional<PathLink> existingPathLink = Optional.empty();
        if(incomingPathLink.getNetexId() != null) {
            existingPathLink = Optional.of(pathLinkRepository.findFirstByNetexIdOrderByVersionDesc(incomingPathLink.getNetexId()));
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
