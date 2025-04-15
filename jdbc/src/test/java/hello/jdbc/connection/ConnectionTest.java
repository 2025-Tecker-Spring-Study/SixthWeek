package hello.jdbc.connection;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class ConnectionTest {


    @Test
    void driverManager() throws SQLException {
        Connection connection1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Connection connection2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);

        log.info("connection = {}, class = {}", connection1, connection1.getClass());
        log.info("connection = {}, class = {}", connection2, connection2.getClass());

    }

    @Test
    void dataSourceDiverManager() throws SQLException {
        // DriverManagerDataSource는 항상 새 커넥션 풀을 획득
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        useDataSource(dataSource);
    }


    @Test
    void dataSourceConnectionPool() throws SQLException, InterruptedException {
        // 커넥션 풀링 : HikariProxyConnection(Proxy) -> JdbcConnection(Target)
        HikariDataSource dataSource = new HikariDataSource();

        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("MyPool");

        useDataSource(dataSource);
        Thread.sleep(1000);
    }





    private void useDataSource(DataSource dataSource) throws SQLException {
        Connection connection1 = dataSource.getConnection();
        Connection connection2 = dataSource.getConnection();

        log.info("connection = {}, class = {}", connection1, connection1.getClass());
        log.info("connection = {}, class = {}", connection2, connection2.getClass());

    }


}

//23:13:41.077 [Test worker] INFO hello.jdbc.connection.ConnectionTest -- connection = conn0: url=jdbc:h2:tcp://localhost/~/test user=SA, class = class org.h2.jdbc.JdbcConnection
//23:13:41.082 [Test worker] INFO hello.jdbc.connection.ConnectionTest -- connection = conn1: url=jdbc:h2:tcp://localhost/~/test user=SA, class = class org.h2.jdbc.JdbcConnection

// connection = HikariProxyConnection@170052458 wrapping conn0: url=jdbc:h2:tcp://localhost/~/test user=SA, class = class com.zaxxer.hikari.pool.HikariProxyConnection
// connection = HikariProxyConnection@2043106095 wrapping conn1: url=jdbc:h2:tcp://localhost/~/test user=SA, class = class com.zaxxer.hikari.pool.HikariProxyConnection

//23:53:07.158 [Test worker] DEBUG c.z.hikari.util.DriverDataSource --Loaded driver with class name org.h2.Driver for jdbcUrl=jdbc:h2:tcp://localhost/~/test
//        23:53:07.199 [Test worker] INFO  com.zaxxer.hikari.pool.HikariPool --MyPool - Added connection conn0: url=jdbc:h2:tcp://localhost/~/test user=SA
//        23:53:07.202 [Test worker] INFO  com.zaxxer.hikari.HikariDataSource --MyPool - Start completed.
//        23:53:07.206 [MyPool connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool --MyPool - Added connection conn1: url=jdbc:h2:tcp://localhost/~/test user=SA
//        23:53:07.206 [Test worker] INFO  h.jdbc.connection.ConnectionTest --connection = HikariProxyConnection@577127077 wrapping conn0: url=jdbc:h2:tcp://localhost/~/test user=SA, class = class com.zaxxer.hikari.pool.HikariProxyConnection
//        23:53:07.208 [Test worker] INFO  h.jdbc.connection.ConnectionTest --connection = HikariProxyConnection@788892554 wrapping conn1: url=jdbc:h2:tcp://localhost/~/test user=SA, class = class com.zaxxer.hikari.pool.HikariProxyConnection
//        23:53:07.241 [MyPool connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool --MyPool - Connection not added, stats (total=2, active=2, idle=0, waiting=0)
