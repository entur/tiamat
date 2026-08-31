package org.rutebanken.tiamat.service;

import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.auth.UsernameFetcher;
import org.rutebanken.tiamat.lock.MutateLock;
import org.rutebanken.tiamat.model.DataManagedObjectStructure;
import org.rutebanken.tiamat.model.TariffZone;
import org.rutebanken.tiamat.model.VersionOfObjectRefStructure;
import org.rutebanken.tiamat.repository.TariffZoneRepository;
import org.rutebanken.tiamat.repository.reference.ReferenceResolver;
import org.rutebanken.tiamat.service.batch.BackgroundJobs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;

@Service("tariffZoneTerminator")
public class TariffZoneTerminator {
    private static final Logger logger= LoggerFactory.getLogger(TariffZoneTerminator.class);


    private final TariffZoneRepository tariffZoneRepository;
    private final UsernameFetcher usernameFetcher;
    private final MutateLock mutateLock;
    private final BackgroundJobs backgroundJobs;
    private final AuthorizationService authorizationService;
    private final ReferenceResolver referenceResolver;
    @Autowired
    public TariffZoneTerminator(TariffZoneRepository tariffZoneRepository,
                                UsernameFetcher usernameFetcher,
                                MutateLock mutateLock,
                                BackgroundJobs backgroundJobs,
                                AuthorizationService authorizationService,
                                ReferenceResolver referenceResolver) {
        this.tariffZoneRepository = tariffZoneRepository;
        this.usernameFetcher = usernameFetcher;
        this.mutateLock = mutateLock;
        this.backgroundJobs = backgroundJobs;
        this.authorizationService = authorizationService;
        this.referenceResolver = referenceResolver;
    }

    public TariffZone terminateTariffZone(String tariffZoneId, Instant suggestedTimeOfTermination, String versionComment) {
        return mutateLock.executeInLock(() -> {
            String usernameForAuthenticatedUser = usernameFetcher.getUserNameForAuthenticatedUser();
            logger.warn("About to terminate tariff zone by ID {}. User: {}", tariffZoneId, usernameForAuthenticatedUser);
            DataManagedObjectStructure resolved = referenceResolver.resolve(new VersionOfObjectRefStructure(tariffZoneId));
            authorizationService.verifyCanEditEntities( Collections.singletonList(resolved));
            Instant now = Instant.now();
            Instant timeOfTermination;

            if (suggestedTimeOfTermination.isBefore(now)) {
                logger.warn("Termination date {} cannot be before now {}. Setting now as time of termination for {}", suggestedTimeOfTermination, now, tariffZoneId);
                timeOfTermination = now;
            } else {
                timeOfTermination = suggestedTimeOfTermination;
            }

            logger.info("User {} is terminating tariff zone {} at {} with comment '{}'", usernameFetcher.getUserNameForAuthenticatedUser(), tariffZoneId, timeOfTermination, versionComment);

            final TariffZone tariffZone = tariffZoneRepository.findFirstByNetexIdOrderByVersionDesc(tariffZoneId);
            if (tariffZone != null) {
                // If TariffZone already has a to_date and is in future, it is possible change to before future date, but its not other way around i.e. extend to_date,
                // or to_date after future date, this is to avoid duplicated tariff zones.
                if (tariffZone.getValidBetween() != null && tariffZone.getValidBetween().getToDate() != null && tariffZone.getValidBetween().getToDate().isBefore(timeOfTermination)) {
                    throw new IllegalArgumentException("The tariff zone " + tariffZoneId + ", version " + tariffZone.getVersion() + " is already terminated at " + tariffZone.getValidBetween().getToDate());
                }

                logger.debug("End previous version {} of tariff zone {} at {} (timeOfTermination)", tariffZone.getVersion(), tariffZone.getNetexId(), timeOfTermination);
                tariffZone.getValidBetween().setToDate(timeOfTermination);

                final TariffZone result = tariffZoneRepository.save(tariffZone);

                //Start updating stop_place tariff zone ref
                backgroundJobs.triggerStopPlaceUpdate();

                return result;
            }  else {
                throw new IllegalArgumentException("Cannot find tariff zone to terminate: " + tariffZoneId + ". No changes executed.");
            }
        });
    }
}
