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

package org.rutebanken.tiamat.rest.graphql.fetchers;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ID;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.MUTATE_TARIFF_ZONE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.OUTPUT_TYPE_TARIFF_ZONE;

import java.util.List;
import java.util.Map;

import com.google.api.client.util.Preconditions;
import graphql.language.Field;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.lock.MutateLock;
import org.rutebanken.tiamat.model.TariffZone;
import org.rutebanken.tiamat.repository.TariffZoneRepository;
import org.rutebanken.tiamat.rest.graphql.helpers.CleanupHelper;
import org.rutebanken.tiamat.rest.graphql.mappers.TariffZoneMapper;
import org.rutebanken.tiamat.versioning.VersionCreator;
import org.rutebanken.tiamat.versioning.save.TariffZoneSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is used to update TariffZone objects.
 *
 * @author Dick Zetterberg (dick@transitor.se)
 * @version 2020-06-01
 */
@Service("tariffZonesUpdater")
@Transactional
class TariffZonesUpdater implements DataFetcher<TariffZone> {

    private static final Logger logger = LoggerFactory.getLogger(TariffZonesUpdater.class);

    @Autowired
    private TariffZoneSaverService tariffZoneSaverService;

    @Autowired
    private TariffZoneRepository tariffZoneRepository;

    @Autowired
    private TariffZoneMapper tariffZoneMapper;

    @Autowired
    private MutateLock mutateLock;

    @Autowired
    protected VersionCreator versionCreator;

    @Override
    public TariffZone get(DataFetchingEnvironment environment) {
        List<Field> fields = environment.getFields();
        CleanupHelper.trimValues(environment.getArguments());
        for (Field field : fields) {
            if (field.getName().equals(MUTATE_TARIFF_ZONE)) {
                return createOrUpdateTariffZone(environment);
            }
        }
        throw new IllegalArgumentException("Could not find a field with name " + MUTATE_TARIFF_ZONE);
    }


    private TariffZone createOrUpdateTariffZone(DataFetchingEnvironment environment) {
        return mutateLock.executeInLock(() -> {
            TariffZone updatedTariffZone;
            TariffZone existingVersion = null;
            Map input = environment.getArgument(OUTPUT_TYPE_TARIFF_ZONE);

            if (input != null) {

                String netexId = (String) input.get(ID);

                if (netexId != null) {

                    logger.info("About to update TariffZone {}", netexId);

                    existingVersion = findAndVerify(netexId);
                    updatedTariffZone = versionCreator.createCopy(existingVersion, TariffZone.class);

                } else {
                    logger.info("Creating new TariffZone");
                    updatedTariffZone = new TariffZone();
                }

                boolean isUpdated = tariffZoneMapper.populate(input, updatedTariffZone);

                if (isUpdated) {
                    logger.info("Saving {}", updatedTariffZone);
                    return tariffZoneSaverService.saveNewVersion(updatedTariffZone);
                }
            }
            logger.warn("TariffZone was attempted mutated, but no changes were applied {}", existingVersion);
            return existingVersion;
        });
    }

    private TariffZone findAndVerify(String netexId) {
        TariffZone existingTariffZone = tariffZoneRepository.findFirstByNetexIdOrderByVersionDesc(netexId);
        verifyTariffZoneNotNull(existingTariffZone, netexId);
        return existingTariffZone;
    }

    private void verifyTariffZoneNotNull(TariffZone existingTariffZone, String netexId) {
        Preconditions.checkArgument(existingTariffZone != null, "Attempting to update TariffZone [id = %s], but TariffZone does not exist.", netexId);
    }
}
