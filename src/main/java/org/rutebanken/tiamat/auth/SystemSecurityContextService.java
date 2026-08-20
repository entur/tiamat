package org.rutebanken.tiamat.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

/**
 * Runs a unit of work as a trusted in-process "system import" for internal jobs that have no
 * end-user request behind them (for example {@code FareZonePoller} pulling a fixed feed).
 *
 * <p>For the duration of the work it activates {@link SystemImportContext}, which makes
 * {@link DefaultAuthorizationService} grant the edit/delete checks along the import path - no
 * service-account token required - and makes {@link UsernameFetcher} report the configured system
 * name, so saved entities get a non-null {@code changedBy} for audit.
 *
 * <p>No JWT is fabricated: an issuer-less synthetic token is rejected by the deployed
 * {@code UserInfoExtractor}, which resolves the actor against the Baba user registry where the
 * system actor does not exist. The security context is emptied instead, so nothing downstream can
 * mistake the system import for an authenticated user or inherit a principal left on this thread by
 * an earlier request.
 *
 * <p>Both the context and the flag are restored afterwards, including on exception.
 */
@Service
public class SystemSecurityContextService {

    private final String systemUsername;

    public SystemSecurityContextService(
            @Value("${tiamat.system.import.username:tiamat-system}") String systemUsername) {
        this.systemUsername = systemUsername;
    }

    public <T> T runAsSystemImport(Supplier<T> action) {
        SecurityContext previous = SecurityContextHolder.getContext();
        try {
            SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
            SystemImportContext.activate(systemUsername);
            return action.get();
        } finally {
            SystemImportContext.clear();
            SecurityContextHolder.setContext(previous);
        }
    }

    public void runAsSystemImport(Runnable action) {
        runAsSystemImport(() -> {
            action.run();
            return null;
        });
    }
}
