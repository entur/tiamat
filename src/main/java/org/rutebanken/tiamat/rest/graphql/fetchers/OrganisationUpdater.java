package org.rutebanken.tiamat.rest.graphql.fetchers;

import com.google.api.client.util.Preconditions;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.tiamat.model.Contact;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_ORGANISATION_EDIT;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.COMPANY_NUMBER;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.CONTACT_DETAILS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.CONTACT_PERSON;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.EMAIL;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.FAX;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.FURTHER_DETAILS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ID;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.LEGAL_NAME;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.NAME;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ORGANISATION_TYPE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.OUTPUT_TYPE_ORGANISATION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PHONE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PRIVATE_CODE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PRIVATE_CONTACT_DETAILS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.URL;
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
    private ReflectionAuthorizationService authorizationService;

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
            authorizationService.assertAuthorized(ROLE_ORGANISATION_EDIT, Arrays.asList(existingVersion, updatedOrganisation));

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
        if (input.get(CONTACT_DETAILS) != null) {
            Map contactDetailsInput = (Map) input.get(CONTACT_DETAILS);
            Contact existingContactDetails = updatedOrganisation.getContactDetails();
            Contact contactDetails = initializeContact(existingContactDetails);
            Contact updatedContactDetails = populateContact(contactDetailsInput, contactDetails);
            updatedOrganisation.setContactDetails(updatedContactDetails);
            isUpdated = true;
        }
        if (input.get(PRIVATE_CONTACT_DETAILS) != null) {
            Map contactDetailsInput = (Map) input.get(PRIVATE_CONTACT_DETAILS);
            Contact existingContactDetails = updatedOrganisation.getPrivateContactDetails();
            Contact contactDetails = initializeContact(existingContactDetails);
            Contact updatedContactDetails = populateContact(contactDetailsInput, contactDetails);
            updatedOrganisation.setPrivateContactDetails(updatedContactDetails);
            isUpdated = true;
        }

        return isUpdated;
    }

    Contact initializeContact(Contact existingContact) {
        if (existingContact != null) {
            return versionCreator.createCopy(existingContact, Contact.class);
        } else {
            return new Contact();
        }
    }

    Contact populateContact(Map contactInput, Contact contact) {
        if (contactInput.get(CONTACT_PERSON) != null) {
            String contactPerson = (String) contactInput.get(CONTACT_PERSON);
            contact.setContactPerson(contactPerson);
        }
        if (contactInput.get(EMAIL) != null) {
            String email = (String) contactInput.get(EMAIL);
            contact.setEmail(email);
        }
        if (contactInput.get(PHONE) != null) {
            String phone = (String) contactInput.get(PHONE);
            contact.setPhone(phone);
        }
        if (contactInput.get(FAX) != null) {
            String fax = (String) contactInput.get(FAX);
            contact.setFax(fax);
        }
        if (contactInput.get(URL) != null) {
            String url = (String) contactInput.get(URL);
            contact.setUrl(url);
        }
        if (contactInput.get(FURTHER_DETAILS) != null) {
            String furtherDetails = (String) contactInput.get(FURTHER_DETAILS);
            contact.setFurtherDetails(furtherDetails);
        }

        return contact;
    }
}
