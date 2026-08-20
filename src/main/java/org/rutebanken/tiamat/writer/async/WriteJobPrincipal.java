package org.rutebanken.tiamat.writer.async;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Carries the caller's principal from the request thread to the processing unit.
 * <p>
 * The write is authorized and attributed where it happens, not where it was accepted, and both
 * read {@code SecurityContextHolder}. Within one JVM that used to be handled by wrapping the
 * executor, but a processing unit in another pod has no security context at all, so the claims are
 * captured when the job is accepted and reinstated when it is processed.
 * <p>
 * Only claims are carried, never the token itself: a bearer token at rest in a broker or a dead
 * letter queue is a liability, and one that has expired cannot be replayed.
 */
@Component
public class WriteJobPrincipal {

    /**
     * These names are duplicated rather than imported, because the libraries that read them do not
     * expose them: the permission store declares its own package private, and the oauth2 helper
     * dropped its constants class after 5.x. Verified against the versions actually resolved,
     * permission-store-proxy and oauth2 7.2.0; worth rechecking on a major upgrade of either.
     */
    private static final String CLAIM_ORGANISATION_ID = "https://entur.io/organisationID";

    /** Primary role source for the jwt extractor. */
    private static final String CLAIM_ROLE_ASSIGNMENTS = "role_assignments";

    /** Fallback role source for the jwt extractor, and part of the permission store request. */
    private static final String CLAIM_PERMISSIONS = "permissions";

    /** Read by the user info extractor, which supplies the changedBy on the saved stop place. */
    private static final String CLAIM_PREFERRED_USERNAME = "preferred_username";

    /**
     * The union of what either role assignment extractor needs, plus what the audit trail needs.
     * Under the jwt extractor the roles travel in {@code role_assignments}, falling back to
     * {@code permissions}; under the permission store they are looked up from the identity, for
     * which the subject and issuer are mandatory and the organisation id is validated against the
     * stored client for machine callers.
     */
    private static final List<String> CARRIED_CLAIMS = List.of(
            JwtClaimNames.SUB,
            JwtClaimNames.ISS,
            JwtClaimNames.EXP,
            CLAIM_ROLE_ASSIGNMENTS,
            CLAIM_PERMISSIONS,
            CLAIM_ORGANISATION_ID,
            CLAIM_PREFERRED_USERNAME
    );

    /**
     * @return the claims to carry, or empty when there is no authenticated caller, which is the
     *         case when authorization is disabled.
     */
    public Map<String, Object> capture() {
        if (!(SecurityContextHolder.getContext().getAuthentication() instanceof JwtAuthenticationToken token)) {
            return Map.of();
        }
        Map<String, Object> claims = new LinkedHashMap<>();
        CARRIED_CLAIMS.forEach(claim -> {
            Object value = token.getToken().getClaim(claim);
            if (value != null) {
                claims.put(claim, normalise(value));
            }
        });
        return claims;
    }

    /**
     * Reinstates the caller for the duration of the job. The rebuilt {@link Jwt} is a claims
     * holder, not a signed token: it is never validated and never leaves the process. Everything
     * downstream then behaves exactly as it does on a request thread.
     *
     * @throws WriteJobCredentialsExpiredException if the caller's credentials expired before the
     *                                             write could be applied.
     */
    public void restore(Map<String, Object> claims) {
        if (claims == null || claims.isEmpty()) {
            // Nothing was captured, so authorization was disabled. Fabricating a principal here
            // would be worse than having none.
            return;
        }
        requireNotExpired(claims);

        Jwt jwt = new Jwt("restored", null, null, Map.of("alg", "none"), widenNumbers(claims));
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
    }

    /**
     * The subject identifies the caller stably, unlike a preferred username, so it is what job
     * ownership is keyed on.
     */
    public String currentSubject() {
        if (!(SecurityContextHolder.getContext().getAuthentication() instanceof JwtAuthenticationToken token)) {
            return null;
        }
        return token.getToken().getSubject();
    }

    public void clear() {
        SecurityContextHolder.clearContext();
    }

    private static void requireNotExpired(Map<String, Object> claims) {
        Object expiry = claims.get(JwtClaimNames.EXP);
        if (expiry == null) {
            return;
        }
        Instant expiresAt = Instant.ofEpochSecond(((Number) expiry).longValue());
        if (Instant.now().isAfter(expiresAt)) {
            throw new WriteJobCredentialsExpiredException(expiresAt);
        }
    }

    /**
     * Stored as epoch seconds so the claims survive a round trip through JSON unchanged.
     */
    private static Object normalise(Object value) {
        return value instanceof Instant instant ? instant.getEpochSecond() : value;
    }

    /**
     * JSON has no integer width, so a claim written as a long comes back as an Integer if it is
     * small enough. That matters because consumers cast rather than convert:
     * {@code AuthenticatedUser.of} does an unchecked {@code (Long)} on the organisation id, which
     * would throw a ClassCastException at authorization time for every machine client.
     */
    private static Map<String, Object> widenNumbers(Map<String, Object> claims) {
        Map<String, Object> widened = new LinkedHashMap<>(claims);
        widened.replaceAll((claim, value) ->
                value instanceof Integer integer ? integer.longValue() : value);
        return widened;
    }
}
