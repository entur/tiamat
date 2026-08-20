package org.rutebanken.tiamat.auth;

import java.util.Objects;

/**
 * Thread-scoped marker for a trusted in-process "system import": a write triggered by internal code
 * (for example {@code FareZonePoller} pulling a fixed, operator-configured feed) rather than by an
 * authenticated end-user request.
 *
 * <p>When active on the current thread, {@link DefaultAuthorizationService} treats edit/delete
 * checks as granted, so a background job with no user principal can write without a service-account
 * token, and {@link UsernameFetcher} attributes the write to {@link #username()} instead of looking
 * the actor up in the user registry - there is no registry entry for it. The system actor is
 * represented by this context alone; no JWT is fabricated for it.
 *
 * <p>The flag can only be toggled from within this package (via
 * {@link SystemSecurityContextService}), so it is not reachable from user-facing request handling.
 */
public final class SystemImportContext {

    private static final ThreadLocal<String> SYSTEM_USERNAME = new ThreadLocal<>();

    private SystemImportContext() {
    }

    public static boolean isActive() {
        return SYSTEM_USERNAME.get() != null;
    }

    /**
     * The name to attribute writes to while a system import runs on this thread, or null outside one.
     */
    public static String username() {
        return SYSTEM_USERNAME.get();
    }

    static void activate(String username) {
        SYSTEM_USERNAME.set(Objects.requireNonNull(username, "System import username must be set"));
    }

    static void clear() {
        SYSTEM_USERNAME.remove();
    }
}
