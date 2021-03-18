package org.rutebanken.tiamat.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.Test;

import se.samtrafiken.util.AssertionFailureException;

class DbQueryUtilTest {
    @Test
    void createSaneWhereClause_seriesOfNumbers_shouldConvertToCorrectWhereClause() {
        Set<Long> list = Set.of(2L, 3L, 4L, 7L, 8L, 10L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L);
        String whereClause = DbQueryUtil.createSaneWhereClause("id", list);
        assertEquals("((id >= 2 AND id <= 4) OR (id >= 7 AND id <= 8) OR id = 10 OR (id >= 13 AND id <= 20))", whereClause);
    }

    @Test
    void createSaneWhereClause_emptyList_shouldThrowException() {
        assertThrows(AssertionFailureException.class, () -> DbQueryUtil.createSaneWhereClause("id", null));
        assertThrows(AssertionFailureException.class, () -> DbQueryUtil.createSaneWhereClause("id", Set.of()));
    }

    @Test
    void createSaneWhereClause_invalidFieldName_shouldThrowException() {
        assertThrows(AssertionFailureException.class, () -> DbQueryUtil.createSaneWhereClause(null, Set.of(1L)));
        assertThrows(AssertionFailureException.class, () -> DbQueryUtil.createSaneWhereClause("", Set.of(1L)));
    }
}