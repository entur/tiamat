package org.rutebanken.tiamat.ext.fintraffic.auth;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.rutebanken.tiamat.ext.fintraffic.auth.model.ExternalPermission;
import org.rutebanken.tiamat.ext.fintraffic.auth.model.ExternalPermissionGrant;
import org.rutebanken.tiamat.ext.fintraffic.auth.model.GroupMembership;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static java.time.temporal.ChronoUnit.MINUTES;

/**
 * Encapsulation for handling permission checks from Trivore ID Management API.
 * <p>
 * Permission methods in this class only test for the permission specified by the method itself. If you need to do
 * compositions, such as <code>if (admin || hasSpecificPermission) do ...</code>, create these compositions in your
 * calling code, not inside this class.
 */
public class TrivoreAuthorizations {

    public static final String ENTITY_TYPE_ALL = "{entities}";
    public static final String TRANSPORT_MODE_ALL = "{all}";
    private final Logger logger = LoggerFactory.getLogger(TrivoreAuthorizations.class);

    private final WebClient httpClient;
    private final String oidcServerUri;
    private final String clientId;
    private final String clientSecret;
    private final boolean enableCodespaceFiltering;

    /**
     * Poor man's infinite cache. This content needs to be loaded once and should never change.
     */
    private final LoadingCache<PermissionIdentifiers, ExternalPermission> externalPermissionCache;
    private final LoadingCache<UserIdentifier, List<GroupMembership>> usersGroupMembershipCache;
    private final LoadingCache<UserIdentifier, List<ExternalPermissionGrant>> usersExternalPermissionsCache;

    public TrivoreAuthorizations(WebClient webClient,
                                 String oidcServerUri,
                                 String clientId,
                                 String clientSecret,
                                 boolean enableCodespaceFiltering) {
        this.oidcServerUri = oidcServerUri;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.enableCodespaceFiltering = enableCodespaceFiltering;
        this.httpClient = webClient;
        this.externalPermissionCache = createCache(50, Duration.of(5, MINUTES), new CacheLoader<>() {
            public ExternalPermission load(PermissionIdentifiers permissionIdentifiers) throws Exception {
                return loadExternalPermission(permissionIdentifiers);
            }
        });
        this.usersGroupMembershipCache = createCache(1000, Duration.of(5, MINUTES), new CacheLoader<>() {
            @Override
            public List<GroupMembership> load(UserIdentifier userIdentifier) throws Exception {
                return loadUsersGroupMemberships(userIdentifier);
            }
        });
        this.usersExternalPermissionsCache = createCache(50, Duration.of(5, MINUTES), new CacheLoader<>() {
            public List<ExternalPermissionGrant> load(UserIdentifier userIdentifier) throws Exception {
                return loadUsersExternalPermissionGrants(userIdentifier);
            }
        });
    }

    private <K, V> LoadingCache<K, V> createCache(int maximumSize, Duration duration, CacheLoader<K,V> loader) {
        return CacheBuilder.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterWrite(duration)
                .build(loader);
    }

    private static Optional<Jwt> getToken() {
        if (SecurityContextHolder.getContext().getAuthentication() instanceof JwtAuthenticationToken token
                && (token.getPrincipal() instanceof Jwt jwt)) {
            return Optional.of(jwt);
        }
        return Optional.empty();
    }

    /**
     * @return Returns the <code>sub</code> claim of JWT or human readable "unknown" value if token isn't present.
     */
    private static String getCurrentSubject() {
        return getToken().map(Jwt::getSubject).orElse("<unknown user>");
    }

    private String basicAuthenticationHeaderValue() {
        return Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));
    }

    private List<GroupMembership> loadUsersGroupMemberships(UserIdentifier userIdentifier) throws CacheLoadingException {
        Optional<List<GroupMembership>> groupMembership = executeRequest(
                HttpMethod.GET,
                oidcServerUri + "/api/rest/v1/user/" + userIdentifier.userId() + "/groupmembership",
                Map.of("Authorization", "Basic " + basicAuthenticationHeaderValue(),
                        "Content-Type", "application/json"),
                new ParameterizedTypeReference<List<GroupMembership>>() {});
        if (groupMembership.isPresent()) {
            return groupMembership.get();
        } else {
            throw new CacheLoadingException("Failed to load user's group membership data for user [" + userIdentifier + "]");
        }
    }
    private ExternalPermission loadExternalPermission(PermissionIdentifiers permissionIdentifiers) throws CacheLoadingException {
        Optional<ExternalPermission> externalPermission = executeRequest(
                HttpMethod.GET,
                oidcServerUri + "/api/rest/v1/externalpermission/group/" + permissionIdentifiers.permissionGroupId() + "/permission/" + permissionIdentifiers.permissionId(),
                Map.of("Authorization", "Basic " + basicAuthenticationHeaderValue(),
                        "Content-Type", "application/json"),
                new ParameterizedTypeReference<ExternalPermission>() {});
        if (externalPermission.isPresent()) {
            return externalPermission.get();
        } else {
            throw new CacheLoadingException("Failed to load external permission data for permission [" + permissionIdentifiers + "]");
        }
    }

    private List<ExternalPermissionGrant> loadUsersExternalPermissionGrants(UserIdentifier userIdentifier) throws CacheLoadingException {
        Optional<List<ExternalPermissionGrant>> userExternalPermissionGrants = executeRequest(
                HttpMethod.GET,
                oidcServerUri + "/api/rest/v1/user/" + userIdentifier.userId() + "/externalpermissions",
                Map.of("Authorization", "Basic " + basicAuthenticationHeaderValue(),
                        "Content-Type", "application/json"),
                new ParameterizedTypeReference<List<ExternalPermissionGrant>>() {});
        if (userExternalPermissionGrants.isPresent()) {
            return userExternalPermissionGrants.get();
        } else {
            throw new CacheLoadingException("Failed to load user's external permission grants data for user [" + userIdentifier + "]");
        }
    }

    private Optional<List<ExternalPermissionGrant>> fetchTrivoreUsersExternalPermissions(String userId) {
        UserIdentifier identifier  = new UserIdentifier(userId);
        try {
            return Optional.of(usersExternalPermissionsCache.get(identifier));
        } catch (ExecutionException e) {
            logger.warn("Failed to fetch user's external permission grants for identifier [{}]", identifier, e);
            return Optional.empty();
        }
    }
    private Optional<ExternalPermission> fetchTrivoreExternalPermission(String groupId, String permissionId) {
        PermissionIdentifiers identifiers  = new PermissionIdentifiers(groupId, permissionId);
        try {
            return Optional.of(externalPermissionCache.get(identifiers));
        } catch (ExecutionException e) {
            logger.warn("Failed to fetch external permission details for identifiers [{}]", identifiers, e);
            return Optional.empty();
        }
    }
    private Optional<List<GroupMembership>> fetchTrivoreUsersGroupMemberships(String userId) {
        UserIdentifier identifier  = new UserIdentifier(userId);
        try {
            return Optional.of(usersGroupMembershipCache.get(identifier));
        } catch (ExecutionException e) {
            logger.warn("Failed to fetch user's group memberships for identifier [{}]", identifier, e);
            return Optional.empty();
        }
    }

    private <T> Optional<T> executeRequest(HttpMethod method, String uri, Map<String, String> headers, ParameterizedTypeReference<T> type) {
        try {
            return httpClient.method(method)
                    .uri(uri)
                    .accept(org.springframework.http.MediaType.APPLICATION_JSON)
                    .headers(requestHeaders -> headers.forEach(requestHeaders::set))
                    .retrieve()
                    .toEntity(type)
                    .flatMap(entity -> {
                        HttpStatusCode statusCode = entity.getStatusCode();
                        logger.debug("HTTP {} {} returned {}", method, uri, statusCode.value());
                        if (statusCode.is2xxSuccessful()) {
                            return Mono.justOrEmpty(entity.getBody());
                        } else {
                            return Mono.empty();
                        }
                    })
                    .blockOptional();
        } catch (Exception e) {
            throw new AuthorizationException("Failed to execute request " + method + " " + uri, e);
        }
    }

    private boolean hasDirectPermission(String directPermission) {
        boolean hasPermission = getToken()
                .flatMap(jwt -> fetchTrivoreUsersExternalPermissions(jwt.getSubject()))
                .orElse(List.of())
                .stream()
                .map(epg -> fetchTrivoreExternalPermission(epg.permissionGroupId(), epg.permissionId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .anyMatch(p -> p.name().equalsIgnoreCase(directPermission));
        if (logger.isTraceEnabled()) {
            logger.trace("User [{}] has permission [{}] {}", getCurrentSubject(), directPermission, hasPermission);
        }
        return hasPermission;
    }

    public boolean hasAccessToCodespace(String codespace) {
        if (!enableCodespaceFiltering) {
            logger.debug("Codespace filtering is disabled, will not block access to {}", codespace);
            return true;
        }

        return getToken()
                .flatMap(jwt -> fetchTrivoreUsersGroupMemberships(jwt.getSubject()))
                .map(groupMemberships -> {
                    for (GroupMembership groupMembership : groupMemberships) {
                        Set<String> accessibleCodespaces = groupMembership.getCodespaces();
                        if (accessibleCodespaces.contains(codespace)) {
                            if (logger.isTraceEnabled()) {
                                logger.trace("User [{}] is allowed to access codespace {} [{}]", getCurrentSubject(), codespace, groupMembership.id());
                            }
                            return true;
                        }
                    }
                    if (logger.isTraceEnabled()) {
                        logger.trace("User [{}] is not allowed to access codespace {}", getCurrentSubject(), codespace);
                    }
                    return false;
                })
                .orElseGet(() -> {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Could not resolve {} codespace access for user [{}]", codespace, getCurrentSubject());
                    }
                    return false;
                });
    }

    private static final Map<String, List<String>> SUPERCEDING_PERMISSIONS = Map.of(
            "view", List.of("administer", "manage", "edit", "view"),
            "edit", List.of("administer", "manage", "edit"),
            "manage", List.of("administer", "manage"),
            "administer", List.of("administer")
    );

    @VisibleForTesting
    List<String> generateCascadingPermissions(String entityType, String transportMode, TrivorePermission permission) {
        List<String> permissions = new ArrayList<>();

        for (String entity : uniqueList("{entities}", entityType)) {
            for (String mode : uniqueList("{all}", transportMode)) {
                for (String applicablePermission : SUPERCEDING_PERMISSIONS.getOrDefault(permission.getValue(), List.of())) {
                    permissions.add(entity + ":" + mode + ":" + applicablePermission);
                }
            }
        }
        return permissions;
    }

    private static List<String> uniqueList(String... elements) {
        List<String> uniqueElements = new ArrayList<>();
        for (String element : elements) {
            if (!uniqueElements.contains(element)) {
                uniqueElements.add(element);
            }
        }
        return uniqueElements;
    }

    public boolean hasAccess(String entityType, String transportMode, TrivorePermission permission) {
        List<String> cascadingPermissions = generateCascadingPermissions(entityType, transportMode, permission);
        Optional<String> r = cascadingPermissions
                .stream()
                .filter(this::hasDirectPermission)
                .findFirst();

        if (logger.isDebugEnabled()) {
            String requiredPermission = entityType + ":" + transportMode + ":" + permission.getValue();
            String s = "User [" + getCurrentSubject() + "]";
            if (r.isPresent()) {
                s += " has permission [" + r.get() + "] granting access to [" + requiredPermission + "]";
            } else {
                s += " is not allowed to access [" + requiredPermission + "]";
            }
            s += (", possible applicable permissions were " + cascadingPermissions);
            logger.debug(s);
        }

        return r.isPresent();
    }

    public boolean isAuthenticated() {
        return getToken().isPresent();
    }

    /**
     * Reference to Trivore permission to be used as cache key.
     * @param permissionGroupId Permission's group id.
     * @param permissionId Permission's id itself.
     */
    private record PermissionIdentifiers(String permissionGroupId, String permissionId) {}
    /**
     * Reference to Trivore user to be used as cache key.
     * @param userId Trivore's user id
     */
    private record UserIdentifier(String userId) {}
}
