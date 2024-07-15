package org.rutebanken.tiamat.rest.graphql.fetchers;

import com.google.api.client.util.Preconditions;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Organisation;
import org.rutebanken.tiamat.model.OrganisationTypeEnumeration;
import org.rutebanken.tiamat.repository.OrganisationRepository;
import org.rutebanken.tiamat.versioning.VersionCreator;
import org.rutebanken.tiamat.versioning.save.OrganisationVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import graphql.schema.DataFetcher;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.COMPANY_NUMBER;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ID;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.LEGAL_NAME;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.NAME;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ORGANISATION_TYPE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.OUTPUT_TYPE_ORGANISATION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PRIVATE_CODE;
import static org.rutebanken.tiamat.rest.graphql.mappers.EmbeddableMultilingualStringMapper.getEmbeddableString;

@Service("organisationUpdater")
@Transactional
public class OrganisationUpdater implements DataFetcher {
    private static final Logger logger = LoggerFactory.getLogger(OrganisationUpdater.class);

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private OrganisationVersionedSaverService organisationVersionedSaverService;

    @Autowired
    private VersionCreator versionCreator;

    @Override
    public Object get(DataFetchingEnvironment environment) throws Exception {
        List<Map> input = environment.getArgument(OUTPUT_TYPE_ORGANISATION);
        List<Organisation> organisations = null;
        if (input != null) {
            organisations = input.stream()
             .map(m -> createOrUpdateOrganisation(m))
            .collect(Collectors.toList());
        }
        return organisations;
    }

    private Organisation createOrUpdateOrganisation(Map input) {
        Organisation updatedOrganisation;
        Organisation existingVersion = null;
        String netexId = (String) input.get(ID);
        if (netexId != null) {
            logger.info("Updating Organisation {}", netexId);
            existingVersion = organisationRepository.findFirstByNetexIdOrderByVersionDesc(netexId);
            Preconditions.checkArgument(existingVersion != null, "Attempting to update Organisation [id = %s], but Organisation does not exist.", netexId);
            updatedOrganisation = versionCreator.createCopy(existingVersion, Organisation.class);

        } else {
            logger.info("Creating new Organisation");
            updatedOrganisation = new Organisation();
        }
        boolean isUpdated = populateOrganisation(input, updatedOrganisation);

        if (isUpdated) {
            logger.info("Saving new version of organisation {}", updatedOrganisation);
            updatedOrganisation = organisationVersionedSaverService.saveNewVersion(updatedOrganisation);

            return updatedOrganisation;
        } else {
            logger.info("No changes - Organisation {} NOT updated", netexId);
        }
        return existingVersion;
    }

    private boolean populateOrganisation(Map input, Organisation updatedOrganisation) {
        boolean isUpdated = false;

        if (input.get(PRIVATE_CODE) != null) {
            String privateCode = (String) input.get(PRIVATE_CODE);
            isUpdated = isUpdated || (!privateCode.equals(updatedOrganisation.getPrivateCode()));
            updatedOrganisation.setPrivateCode(privateCode);
        }
        if (input.get(COMPANY_NUMBER) != null) {
            String companyNumber = (String) input.get(COMPANY_NUMBER);
            isUpdated = isUpdated || (!companyNumber.equals(updatedOrganisation.getCompanyNumber()));
            updatedOrganisation.setCompanyNumber(companyNumber);
        }
        if (input.get(NAME) != null) {
            String name = (String) input.get(NAME);
            isUpdated = isUpdated || (!name.equals(updatedOrganisation.getName()));
            updatedOrganisation.setName(name);
        }
        if (input.get(ORGANISATION_TYPE) != null) {
            OrganisationTypeEnumeration organisationType = (OrganisationTypeEnumeration) input.get(ORGANISATION_TYPE);
            isUpdated = isUpdated || (!organisationType.equals(updatedOrganisation.getOrganisationType()));
            updatedOrganisation.setOrganisationType(organisationType);
        }
        if (input.get(LEGAL_NAME) != null) {
            EmbeddableMultilingualString legalName = getEmbeddableString((Map) input.get(LEGAL_NAME));
            isUpdated = isUpdated || (!legalName.equals(updatedOrganisation.getLegalName()));
            updatedOrganisation.setLegalName(legalName);
        }

        return isUpdated;
    }
}
