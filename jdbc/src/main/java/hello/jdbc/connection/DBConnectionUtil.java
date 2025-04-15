package hello.jdbc.connection;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;


@Slf4j
public class DBConnectionUtil {
    public static Connection getConnection() {
        try {
            // 연결이 되었는지 확인
            // h2 라이브러리의 org.drive 파일을 규칙에 따라서 찾는다.
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            log.info("get connection={}, class={}", connection, connection.getClass());
            return connection;
            // 연결 실패시 RunTime 예외로 분류하여 에러를 확인
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}



