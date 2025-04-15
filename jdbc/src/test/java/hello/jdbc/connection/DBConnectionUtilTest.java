package hello.jdbc.connection;

//import org.junit.jupiter.api.Test;

import hello.jdbc.TestStatic;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

@Slf4j
public class DBConnectionUtilTest {
    @Test
    void connection() {
        Connection connection = DBConnectionUtil.getConnection();
        Assertions.assertThat(connection).isNotNull();
    }


}

// Result
// [Test worker] INFO hello.jdbc.connection.DBConnectionUtil --
// get connection=conn0: url=jdbc:h2:tcp://localhost/~/test user=SA,
// class=class org.h2.jdbc.JdbcConnection
