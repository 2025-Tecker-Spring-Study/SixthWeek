package hello.jdbc.connection;

// DB 접근에 필요한 기본 정보를 상수로 생성
public abstract class ConnectionConst {
    public static final String URL = "jdbc:h2:tcp://localhost/~/test";
    public static final String USERNAME = "sa";
    public static final String PASSWORD = "";
}
