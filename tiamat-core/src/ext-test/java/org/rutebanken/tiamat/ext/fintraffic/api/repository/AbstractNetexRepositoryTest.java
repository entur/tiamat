package org.rutebanken.tiamat.ext.fintraffic.api.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AbstractNetexRepositoryTest {

    private Connection connection;
    private FintrafficNetexRepository repository;

    @BeforeEach
    void setUp() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        connection = mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.getAutoCommit()).thenReturn(true);
        JdbcTemplate jdbc = mock(JdbcTemplate.class);
        when(jdbc.getDataSource()).thenReturn(dataSource);
        repository = new FintrafficNetexRepository(jdbc, new ObjectMapper());
    }

    @Test
    void runWithIsolationRollsBackAndRethrowsWhenCallbackThrowsRuntimeException() throws SQLException {
        RuntimeException failure = new RuntimeException(new SQLException("statement failed"));

        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> repository.runWithIsolation(conn -> {
                    throw failure;
                }));

        assertSame(failure, thrown, "the original exception must not be wrapped again");
        verify(connection).rollback();
        verify(connection, never()).commit();
        verify(connection).setAutoCommit(true);
    }

    @Test
    void runWithIsolationCommitsWithoutRollbackOnSuccess() throws SQLException {
        int result = repository.runWithIsolation(conn -> 7);

        assertEquals(7, result);
        var order = inOrder(connection);
        order.verify(connection).commit();
        order.verify(connection).setAutoCommit(true);
        verify(connection, never()).rollback();
    }
}
