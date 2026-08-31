package org.rutebanken.tiamat.rest.graphql.fetchers;

import com.google.api.client.util.Preconditions;
import graphql.language.Field;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.lock.MutateLock;
import org.rutebanken.tiamat.model.PurposeOfGrouping;
import org.rutebanken.tiamat.repository.PurposeOfGroupingRepository;
import org.rutebanken.tiamat.rest.graphql.mappers.PurposeOfGroupingMapper;
import org.rutebanken.tiamat.versioning.VersionCreator;
import org.rutebanken.tiamat.versioning.save.PurposeOfGroupingSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ID;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.MUTATE_PURPOSE_OF_GROUPING;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.OUTPUT_TYPE_PURPOSE_OF_GROUPING;

@Service("purposeOfGroupingUpdater")
@Transactional
public class PurposeOfGroupingUpdater implements DataFetcher<PurposeOfGrouping> {
    private static final Logger logger= LoggerFactory.getLogger(PurposeOfGroupingUpdater.class);
    
    @Autowired
    private PurposeOfGroupingSaverService purposeOfGroupingSaverService;

    @Autowired
    private MutateLock mutateLock;

    @Autowired
    protected VersionCreator versionCreator;
    @Autowired
    private PurposeOfGroupingRepository purposeOfGroupingRepository;

    @Autowired
    private PurposeOfGroupingMapper purposeOfGroupingMapper;

    @Override
    public PurposeOfGrouping get(DataFetchingEnvironment environment) {
        final List<Field> fields = environment.getMergedField().getFields();
        for (Field field : fields) {
            if(field.getName().equals(MUTATE_PURPOSE_OF_GROUPING)) {
                return createOrUpdatePurposeOfGrouping(environment);
            }
        }
        throw new IllegalArgumentException("Could not find with name" + MUTATE_PURPOSE_OF_GROUPING);
    }

    private PurposeOfGrouping createOrUpdatePurposeOfGrouping(DataFetchingEnvironment environment) {
        return mutateLock.executeInLock(() -> {
            PurposeOfGrouping updatedPurposeOfGrouping;
            PurposeOfGrouping existingVersion = null;
            Map input = environment.getArgument(OUTPUT_TYPE_PURPOSE_OF_GROUPING);

            if(input !=null){
                String netexId = (String) input.get(ID);

                if(netexId!=null) {
                    logger.info("About to update PurposeOfGrouping {}",netexId);
                    existingVersion = findAndVerify(netexId);
                     updatedPurposeOfGrouping = versionCreator.createCopy(existingVersion, PurposeOfGrouping.class);
                } else  {
                    logger.info("Creating new PurposeOfGrouping");
                    updatedPurposeOfGrouping = new PurposeOfGrouping();
                }
                boolean isUpdated=purposeOfGroupingMapper.populate(input,updatedPurposeOfGrouping);
                if (isUpdated) {
                    logger.info("Saving {}",updatedPurposeOfGrouping);
                    return purposeOfGroupingSaverService.saveNewVersion(updatedPurposeOfGrouping);
                }
            }
            logger.warn("PurposeOfGrouping was attempted mutated, but no changes where applied {}",existingVersion);
            return existingVersion;
        });
    }

    private PurposeOfGrouping findAndVerify(String netexId) {
        PurposeOfGrouping existingPurposeOfGrouping = purposeOfGroupingRepository.findFirstByNetexIdOrderByVersionDesc(netexId);
        Preconditions.checkArgument(existingPurposeOfGrouping !=null,"Attempting to update PurposeOfGrouping [id = %s], but PurposeOfGrouping does not exist.", netexId);
        return existingPurposeOfGrouping;
    }
}
