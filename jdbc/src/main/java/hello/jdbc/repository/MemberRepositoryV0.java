package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

import static hello.jdbc.connection.DBConnectionUtil.getConnection;

/**
 * JDBC - Driver Manager 사용
 */

@Slf4j
public class MemberRepositoryV0 {

    // 회원 가입 메서드
    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values(?, ?)";

        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = getConnection();

            // DB에 전달할 SQL과 파라미터로 전달할 데이터를 준비
            pstmt = connection.prepareStatement(sql);

            // SQL 쿼리문에 대한 바인딩 코드
            // 각각 1, 2번째 물음표에 값을 할당한다.
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();
//            pstmt.close(~, ~ ,~); -> 여기에 작성하면 안됨!!
            return member;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
//            pstmt.close();
//            connection.close();
            // 다만 pstmt가 예외가 터지면, connection이 닫히지 않는다.

            // 아래에서 작성한 예외문을 적용
            close(connection, pstmt, null);

            // Note. 쿼리문 날리는걸 보장하기 위해 연결을 닫는건 finally문에서 한다.
            // 만일 try문에서 하면, 에러 발생시 연결을 끊지 못한다.
        }
    }


    // 회원 조회 메서드
    public Member findById(String memberId) throws SQLException {
        String sql = "SELECT * FROM member WHERE member_id = ?";

        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            connection = getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();

            // next : 데이터를 가르키는 메서드
            if (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;

                // 데이터가 없을 때 예외문
            } else { // 서비스에서 어떤 id에서 문제가 발생했는지 보기위해 명시해주면 좋다.
                throw new NoSuchElementException("member not found memberId = " + memberId);
            }
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(connection, pstmt, rs);
        }
    }


    // 각각 Connection과 Statement을 닫아주는 예외
    private void close(Connection connection, Statement stmt, ResultSet rs) {
        // 사용한 자원들을 닫는 코드들
        if (rs != null) {
            try {
                rs.close(); // checked Exception 발생시 catch문 실행
            } catch (SQLException e) {
                log.info("error ", e);
            }
        }

        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.info("error "+e);
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                log.info("error " + e);
            }
        }
    }


    // 회원수정
    public void update(String memberId, int money) throws SQLException {
        String sql = "UPDATE member SET money=? WHERE member_id=?";

        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);

            int resultSize = pstmt.executeUpdate();
            log.info("resultSize = {}", resultSize); // 0 OR 1이 나온다.
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(connection, pstmt,null);
        }
    }


    // 회원삭제
    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id=?";
        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(connection, pstmt, null);
        }
    }

}
