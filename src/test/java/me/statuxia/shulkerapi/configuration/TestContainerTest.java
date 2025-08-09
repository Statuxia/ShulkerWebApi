package me.statuxia.shulkerapi.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TestContainerTest extends BaseContainerTest {

    @Autowired
    protected DataSource dataSource;

    @Test
    void testDataSourceConnection() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            assertThat(connection).isNotNull();
            assertThat(connection.isValid(2)).isTrue();
            assertThat(connection.prepareStatement("select 1").execute()).isTrue();
        }
    }
}
