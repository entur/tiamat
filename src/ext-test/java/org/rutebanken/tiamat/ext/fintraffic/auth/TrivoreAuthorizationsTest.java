package org.rutebanken.tiamat.ext.fintraffic.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class TrivoreAuthorizationsTest {

    private TrivoreAuthorizations trivoreAuthorizations;

    @BeforeEach
    void setUp() {
        trivoreAuthorizations = new TrivoreAuthorizations(null, "", "", "", false);
    }

    @Test
    void generatesCascadingApplicablePermissionsBasedOnGivenValues() {
        String entityType = "StopPlace";
        String transportMode = "bus";
        TrivorePermission permission = TrivorePermission.EDIT;
        List<String> permissionsToTest = trivoreAuthorizations.generateCascadingPermissions(entityType, transportMode, permission);
        
        assertThat(permissionsToTest, equalTo(List.of(
                "{entities}:{all}:administer",
                "{entities}:{all}:manage",
                "{entities}:{all}:edit",
                "{entities}:bus:administer",
                "{entities}:bus:manage",
                "{entities}:bus:edit",
                "StopPlace:{all}:administer",
                "StopPlace:{all}:manage",
                "StopPlace:{all}:edit",
                "StopPlace:bus:administer",
                "StopPlace:bus:manage",
                "StopPlace:bus:edit"
        )));
    }

    @Test
    void generatingCascadingPermissionsDoesNotDuplicateCommonSegments() {
        String entityType = "{entities}";
        String transportMode = "{all}";
        TrivorePermission permission = TrivorePermission.ADMINISTER;
        List<String> permissionsToTest = trivoreAuthorizations.generateCascadingPermissions(entityType, transportMode, permission);

        assertThat(permissionsToTest, equalTo(List.of(
                "{entities}:{all}:administer"
        )));
    }
}