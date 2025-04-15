package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 파라미터 연동, 풀을 고려한 경로
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    // transaction 시작
    public void accountTransfer(String fromId, String toId, int money) throws SQLException  {
        Connection connection = dataSource.getConnection();
        try {
            // 오토커밋 비활성화, 트랜잭션 활성화
            connection.setAutoCommit(false);
            bizLogic(connection, fromId, toId, money);

            // 트랜잭션 성공시, DB에 변경사항 커밋
            connection.commit();
        } catch (Exception e) {
            // 실패시 롤백
            connection.rollback();
            // 기존의 예외를 IllegalStateException에 감싸서 날림.
            throw new IllegalStateException(e);
        } finally {
            release(connection);
        }
    }

    // bizLogic, release() : 트랜잭션 처리 메서드
    // 비즈니스 로직을 따로 작성
    private void bizLogic(Connection connection, String fromId, String toId, int money) throws  SQLException {

        Member fromMember = memberRepository.findById(connection, fromId);
        Member toMember = memberRepository.findById(connection, toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);

        validation(toMember);

        memberRepository.update(toId, toMember.getMoney() + money);
    }

    // 이체 중 예외 발생 처리 메서드
    private void validation(Member tomember) {
        if (tomember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }

    // 세션 조작 이후 커넥션을 닫는 메서드
    private void release(Connection connection) {
        if (connection != null) {
            try {
                // 대게 오토커밋이 트루라고 생각하기에
                // 세션 종료때 오토커밋을 True로 재설정
                connection.setAutoCommit(true);
                connection.close();
            } catch (Exception e) {
                log.info("error ", e);
            }
        }
    }
}
