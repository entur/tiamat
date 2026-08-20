package org.rutebanken.tiamat.auth;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rutebanken.helper.organisation.user.UserInfoExtractor;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for the system-import context: within it, authorization edit checks are granted and the
 * write is attributed to the system name without consulting the user registry; the context is fully
 * restored afterwards.
 */
public class SystemSecurityContextServiceTest {

    private final SystemSecurityContextService service = new SystemSecurityContextService("tiamat-system");
    private final DefaultAuthorizationService authorizationService =
            new DefaultAuthorizationService(null, true, null, null, null);

    /**
     * Stands in for the deployed extractor, which resolves the actor against the Baba user registry.
     * A system import must never reach it: the system actor has no registry entry, and the lookup
     * requires JWT claims (issuer) that a system import has no legitimate value for.
     */
    private final UserInfoExtractor failingExtractor = new UserInfoExtractor() {
        @Override
        public String getPreferredName() {
            throw new AssertionError("User registry must not be consulted for a system import");
        }

        @Override
        public String getPreferredUsername() {
            throw new AssertionError("User registry must not be consulted for a system import");
        }
    };

    private final UsernameFetcher usernameFetcher = new UsernameFetcher(failingExtractor);

    @Before
    @After
    public void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void grantsEditChecksWithinSystemImport() {
        // Outside the system import there is no principal, so edits are denied.
        assertThat(authorizationService.canEditAllEntities()).isFalse();
        assertThat(SystemImportContext.isActive()).isFalse();

        AtomicBoolean ran = new AtomicBoolean(false);
        service.runAsSystemImport(() -> {
            assertThat(SystemImportContext.isActive()).isTrue();
            assertThat(authorizationService.canEditAllEntities()).isTrue();
            // verify* must not throw for a system import
            authorizationService.verifyCanEditEntities(List.of());
            authorizationService.verifyCanDeleteEntities(List.of());
            ran.set(true);
            return null;
        });

        assertThat(ran).isTrue();
    }

    @Test
    public void attributesWritesToSystemNameWithoutUserLookup() {
        service.runAsSystemImport(() -> {
            assertThat(usernameFetcher.getUserNameForAuthenticatedUser()).isEqualTo("tiamat-system");
            return null;
        });
    }

    /**
     * No principal is installed for a system import. A fabricated JWT would be passed to the
     * deployed extractor, which requires an issuer claim and a matching user in the registry.
     */
    @Test
    public void installsNoPrincipalForSystemImport() {
        service.runAsSystemImport(() -> {
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            return null;
        });
    }

    @Test
    public void restoresContextAndFlagAfterRun() {
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        service.runAsSystemImport(() -> null);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(SystemImportContext.isActive()).isFalse();
        assertThat(usernameFetcher.getUserNameForAuthenticatedUser()).isNull();
    }

    @Test
    public void restoresContextAndFlagEvenOnException() {
        try {
            service.runAsSystemImport(() -> {
                throw new RuntimeException("boom");
            });
        } catch (RuntimeException expected) {
            // ignored
        }

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(SystemImportContext.isActive()).isFalse();
    }
}
