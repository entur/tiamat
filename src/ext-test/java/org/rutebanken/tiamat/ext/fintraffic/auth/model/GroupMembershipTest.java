package org.rutebanken.tiamat.ext.fintraffic.auth.model;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;

class GroupMembershipTest {

    private static GroupMembership createMembership(String codespaces, String municipalityCodes) {
        Map<String, Object> customFields = Map.of(
                "codespaces", codespaces,
                "municipalityCodes", municipalityCodes
        );
        return new GroupMembership("g1", "Test Group", "desc", true, null, null, customFields);
    }

    @Test
    void testValidCodespaces() {
        GroupMembership gm = createMembership("ABC,XYZ,ÅÄÖ", "");
        assertThat(gm.getCodespaces(), equalTo(Set.of("ABC", "XYZ", "ÅÄÖ")));
    }

    @Test
    void testInvalidCodespacesSkipped() {
        GroupMembership gm = createMembership("abc,AB,1234", "");
        assertThat(gm.getCodespaces(), empty());
    }

    @Test
    void testMixedCodespaces() {
        GroupMembership gm = createMembership("ABC, invalid, XYZ, ab", "");
        assertThat(gm.getCodespaces(), equalTo(Set.of("ABC", "XYZ")));
    }

    @Test
    void testValidMunicipalityCodes() {
        GroupMembership gm = createMembership("", "091,049,1001");
        assertThat(gm.getMunicipalityCodes(), equalTo(Set.of("091", "049", "1001")));
    }

    @Test
    void testInvalidMunicipalityCodesSkipped() {
        GroupMembership gm = createMembership("", "ABC,12,12345");
        assertThat(gm.getMunicipalityCodes(), empty());
    }

    @Test
    void testMixedMunicipalityCodes() {
        GroupMembership gm = createMembership("", "091, bad, 049, ABCD");
        assertThat(gm.getMunicipalityCodes(), equalTo(Set.of("091", "049")));
    }

    @Test
    void testNullCustomFields() {
        GroupMembership gm = new GroupMembership("g1", "Test", "desc", true, null, null, null);
        assertThat(gm.getCodespaces(), empty());
        assertThat(gm.getMunicipalityCodes(), empty());
    }

    @Test
    void testEmptyValues() {
        GroupMembership gm = createMembership("", "");
        assertThat(gm.getCodespaces(), empty());
        assertThat(gm.getMunicipalityCodes(), empty());
    }
}
