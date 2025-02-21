package org.rutebanken.tiamat.ext.fintraffic.auth;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mizosoft.methanol.AdapterCodec;
import com.github.mizosoft.methanol.MediaType;
import com.github.mizosoft.methanol.Methanol;
import com.github.mizosoft.methanol.MutableRequest;
import com.github.mizosoft.methanol.TypeRef;
import com.github.mizosoft.methanol.adapter.jackson.JacksonAdapterFactory;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.rutebanken.tiamat.ext.fintraffic.auth.model.ExternalPermission;
import org.rutebanken.tiamat.ext.fintraffic.auth.model.ExternalPermissionGrant;
import org.rutebanken.tiamat.ext.fintraffic.auth.model.GroupMembership;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
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

    private final Logger logger = LoggerFactory.getLogger(TrivoreAuthorizations.class);

    /*
     * Permissions are modeled as URN-like triplets. There are some constants which are not in use but have been listed
     * here for completeness' sake, as the same values can be found from the Trivore Id side, so they are meant for you,
     * the developer, as guide to affirm that what you're looking at is exactly what you think it on the other side as
     * well.
     */
    /**
     * Entities are all NeTEx entitities, e.g. StopPlace, Quay etc. etc. Practically synonymous to NeTEx model in this
     * context.
     */
    private static final String ENTITIES_PERMISSIONS = "entities:<scope>:<permission>";

    /**
     * View (R) permission identifier.
     */
    private static final String PERMISSION_VIEW = "view";
    /**
     * Edit (R,U) permission identifier.
     */
    private static final String PERMISSION_EDIT = "edit";
    /**
     * Manage (C, R, U) permission identifier.
     */
    private static final String PERMISSION_MANAGE = "manage";
    /**
     * Administer (C, R, U, D) permission identifier.
     */
    private static final String PERMISSION_ADMINISTER = "administer";

    /**
     * Special scope defining <b>all</b> possible targets as scope. Overriding, admin-like behavior to be expected.
     */
    private static final String SPECIAL_SCOPE_ALL = "{all}";
    /**
     * Special scope allowing <b>own/self-owned</b> targets as scope. Limits permission to contextual group, such as
     * codespaces the user has access to.
     */
    private static final String SPECIAL_SCOPE_OWN = "{own}";
    /**
     * Permission for performing CRU operations on all entities. Logically matches <code>canEdit*()</code>.
     */
    private static final String MANAGE_ALL_ENTITIES = ENTITIES_PERMISSIONS.replace("<scope>", SPECIAL_SCOPE_ALL).replace("<permission>", PERMISSION_MANAGE);
    /**
     * Permission for performing CRU operations on owned entities. Logically matches <code>canEdit*(codespace)</code>.
     */
    private static final String MANAGE_OWN_ENTITIES = ENTITIES_PERMISSIONS.replace("<scope>", SPECIAL_SCOPE_OWN).replace("<permission>", PERMISSION_MANAGE);
    /**
     * Permission for performing CRUD operations on all entities. Logically matches <code>canManage*()</code>.
     */
    private static final String ADMINISTER_ALL_ENTITIES = ENTITIES_PERMISSIONS.replace("<scope>", SPECIAL_SCOPE_ALL).replace("<permission>", PERMISSION_ADMINISTER);
    /**
     * Permission for performing CRUD operations on owned entities. Logically matches <code>canManage*(codespace)</code>.
     */
    private static final String ADMINISTER_OWN_ENTITIES = ENTITIES_PERMISSIONS.replace("<scope>", SPECIAL_SCOPE_OWN).replace("<permission>", PERMISSION_ADMINISTER);

    /**
     * Metadata field which contains <code>codespace</code> value.
     */
    private static final String CUSTOM_FIELD_CODESPACE = "codespace";

    private final Methanol httpClient;
    private final String oidcServerUri;
    private final String clientId;
    private final String clientSecret;

    /**
     * Poor man's infinite cache. This content needs to be loaded once and should never change.
     */
    private final LoadingCache<PermissionIdentifiers, ExternalPermission> externalPermissionCache;
    private final LoadingCache<UserIdentifier, List<GroupMembership>> usersGroupMembershipCache;
    private final LoadingCache<UserIdentifier, List<ExternalPermissionGrant>> usersExternalPermissionsCache;

    public TrivoreAuthorizations(String oidcServerUri,
                                 String clientId,
                                 String clientSecret) {
        this.oidcServerUri = oidcServerUri;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.httpClient = initializeHttpClient();
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

    private static Methanol initializeHttpClient() {
        ObjectMapper mapper = initializeObjectMapper();
        AdapterCodec adapterCodec = AdapterCodec.newBuilder()
                .basic()
                .encoder(JacksonAdapterFactory.createEncoder(mapper, MediaType.APPLICATION_JSON))
                .decoder(JacksonAdapterFactory.createDecoder(mapper, MediaType.APPLICATION_JSON))
                .build();

        return Methanol
                .newBuilder()
                .adapterCodec(adapterCodec)
                .connectTimeout(Duration.ofSeconds(30))
                .requestTimeout(Duration.ofSeconds(30))
                .headersTimeout(Duration.ofSeconds(30))
                .readTimeout(Duration.ofSeconds(30))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .userAgent("Entur Tiamat/" + LocalDate.now().format(DateTimeFormatter.ISO_DATE))
                .build();
    }

    private static ObjectMapper initializeObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return om;
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
        MutableRequest request = MutableRequest
                .GET(oidcServerUri + "/api/rest/v1/user/" + userIdentifier.userId() + "/groupmembership")
                .header("Authorization",
                        "Basic " + basicAuthenticationHeaderValue())
                .header("Content-Type",
                        "application/json");
        Optional<List<GroupMembership>> groupMembership = executeRequest(request, new TypeRef<List<GroupMembership>>() {});
        if (groupMembership.isPresent()) {
            return groupMembership.get();
        } else {
            throw new CacheLoadingException("Failed to load user's group membership data for user [" + userIdentifier + "]");
        }
    }
    private ExternalPermission loadExternalPermission(PermissionIdentifiers permissionIdentifiers) throws CacheLoadingException {
        MutableRequest request = MutableRequest
                .GET(oidcServerUri + "/api/rest/v1/externalpermission/group/" + permissionIdentifiers.permissionGroupId() + "/permission/" + permissionIdentifiers.permissionId())
                .header("Authorization",
                        "Basic " + basicAuthenticationHeaderValue())
                .header("Content-Type",
                        "application/json");
        Optional<ExternalPermission> externalPermission = executeRequest(request, new TypeRef<ExternalPermission>() {});
        if (externalPermission.isPresent()) {
            return externalPermission.get();
        } else {
            throw new CacheLoadingException("Failed to load external permission data for permission [" + permissionIdentifiers + "]");
        }
    }

    private List<ExternalPermissionGrant> loadUsersExternalPermissionGrants(UserIdentifier userIdentifier) throws CacheLoadingException {
        MutableRequest request = MutableRequest
                .GET(oidcServerUri + "/api/rest/v1/user/" + userIdentifier.userId() + "/externalpermissions")
                .header("Authorization",
                        "Basic " + basicAuthenticationHeaderValue())
                .header("Content-Type",
                        "application/json");
        Optional<List<ExternalPermissionGrant>> userExternalPermissionGrants = executeRequest(request, new TypeRef<List<ExternalPermissionGrant>>() {});
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

    private <T> Optional<T> executeRequest(MutableRequest request, TypeRef<T> type) {
        try {
            HttpResponse<T> response = httpClient.send(request, type);
            logger.debug("HTTP {} {} returned {}", request.method(), request.uri(), response.statusCode());
            if (response.statusCode() >= 200 && response.statusCode() < 299) {
                return Optional.ofNullable(response.body());
            } else {
                return Optional.empty();
            }
        } catch (IOException e) {
            throw new AuthorizationException("Failed to execute request " + request.method() + " " + request.uri(), e);
        } catch (InterruptedException e) {
            logger.warn("Operation interrupted during HTTP request, interrupting current thread", e);
            Thread.currentThread().interrupt();
            return Optional.empty();
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

    private boolean hasAccessToCodespace(String codespace) {
        return getToken()
                .flatMap(jwt -> fetchTrivoreUsersGroupMemberships(jwt.getSubject()))
                .map(groupMemberships -> {
                    for (GroupMembership groupMembership : groupMemberships) {
                        if (groupMembership.customFields().getOrDefault(CUSTOM_FIELD_CODESPACE, "").equals(codespace)) {
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

    public boolean canEditAllEntities() {
        return hasDirectPermission(ADMINISTER_ALL_ENTITIES) || hasDirectPermission(MANAGE_ALL_ENTITIES);
    }

    public boolean canDeleteAllEntities() {
        return hasDirectPermission(ADMINISTER_ALL_ENTITIES);
    }

    public boolean canManageAllEntities() {
        return hasDirectPermission(ADMINISTER_ALL_ENTITIES);
    }

    public boolean canManageCodespaceEntities(String codespace) {
        return hasDirectPermission(ADMINISTER_ALL_ENTITIES)
            || ((hasDirectPermission(ADMINISTER_OWN_ENTITIES) || hasDirectPermission(MANAGE_OWN_ENTITIES))
                && hasAccessToCodespace(codespace));
    }

    public boolean canDeleteCodespaceEntities(String codespace) {
        return hasDirectPermission(ADMINISTER_ALL_ENTITIES)
            || (hasDirectPermission(ADMINISTER_OWN_ENTITIES) && hasAccessToCodespace(codespace));
    }

    public boolean canEditEntities(String codespace) {
        return (hasDirectPermission(ADMINISTER_ALL_ENTITIES) || hasDirectPermission(ADMINISTER_OWN_ENTITIES))
            || (hasDirectPermission(MANAGE_OWN_ENTITIES) && hasAccessToCodespace(codespace));
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
