package org.rutebanken.tiamat.service.organisation;

import com.google.api.client.util.Preconditions;
import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.tiamat.auth.UsernameFetcher;
import org.rutebanken.tiamat.changelog.EntityChangedListener;
import org.rutebanken.tiamat.lock.MutateLock;
import org.rutebanken.tiamat.model.Organisation;
import org.rutebanken.tiamat.repository.OrganisationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_EDIT_STOPS;
import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_ORGANISATION_EDIT;

@Service
public class OrganisationDeleter {

    private static final Logger logger = LoggerFactory.getLogger(OrganisationDeleter.class);

    private final EntityChangedListener entityChangedListener;

    private final UsernameFetcher usernameFetcher;

    private OrganisationRepository organisationRepository;

    private final MutateLock mutateLock;

    private final ReflectionAuthorizationService authorizationService;

    @Autowired
    public OrganisationDeleter(OrganisationRepository organisationRepository,
                               EntityChangedListener entityChangedListener,
                               UsernameFetcher usernameFetcher,
                               MutateLock mutateLock,
                               ReflectionAuthorizationService authorizationService) {
        this.organisationRepository = organisationRepository;
        this.entityChangedListener = entityChangedListener;
        this.usernameFetcher = usernameFetcher;
        this.mutateLock = mutateLock;
        this.authorizationService = authorizationService;
    }

    @Transactional
    public boolean deleteOrganisation(String organisationId) {

        return mutateLock.executeInLock(() -> {
            String usernameForAuthenticatedUser = usernameFetcher.getUserNameForAuthenticatedUser();
            logger.warn("About to delete organisation by ID {}. User: {}", organisationId, usernameForAuthenticatedUser);

            List<Organisation> organisations = getAllVersionsOfOrganisation(organisationId);

            authorizationService.assertAuthorized(ROLE_ORGANISATION_EDIT, organisations);
            organisationRepository.deleteAll(organisations);
            notifyDeleted(organisations);

            logger.warn("All versions ({}) of organisation {} deleted by user {}", organisations.size(), organisationId, usernameForAuthenticatedUser);

            return true;
        });
    }

    private List<Organisation> getAllVersionsOfOrganisation(String organisationId) {
        List<Organisation> organisations = organisationRepository.findByNetexId(organisationId);

        Preconditions.checkArgument((organisations != null && !organisations.isEmpty()), "Attempting to fetch Organisation [id = %s], but Organisation does not exist.", organisations);

        return organisations;
    }

    //This is to make sure entity is persisted before sending message
    @Transactional
    public void notifyDeleted(List<Organisation> organisations) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization(){
            @Override
            public void afterCommit(){
                entityChangedListener.onDelete(Collections.max(organisations, Comparator.comparing(c -> c.getVersion())));
            }
        });
    }
}
