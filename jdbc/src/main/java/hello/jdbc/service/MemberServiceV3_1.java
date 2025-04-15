package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 매니저
 */

@RequiredArgsConstructor
@Slf4j
public class MemberServiceV3_1 {

//    private final DataSource dataSource;

    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 memberRepository;

    // transaction 시작
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
//        Connection connection = dataSource.getConnection();
        // 트랜잭션 시작
        // 매개값은 트랜잭션 옵션을 정하는 객체를 넣어줌
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
//            connection.setAutoCommit(false);
            bizLogic(fromId, toId, money);

            transactionManager.commit(status);
            // 트랜잭션 성공시, DB에 변경사항 커밋
//            connection.commit();
        } catch (Exception e) {
            // 실패시 롤백
//            connection.rollback();
            transactionManager.rollback(status);
            // 기존의 예외를 IllegalStateException에 감싸서 날림.
            throw new IllegalStateException(e);
//        } finally {
//            release(connection);
//          커넥션 릴리즈가 필요없다. 트랜잭션 매니저 내부에서 처리해줌!
//        }
    }


    // 세션 조작 이후 커넥션을 닫는 메서드
//    private void release(Connection connection) {
//        if (connection != null) {
//            try {
//                // 대게 오토커밋이 트루라고 생각하기에
//                // 세션 종료때 오토커밋을 True로 재설정
//                connection.setAutoCommit(true);
//                connection.close();
//            } catch (Exception e) {
//                log.info("error ", e);
//            }
//        }
    }

    private void bizLogic(String fromId, String toId, int money) throws  SQLException {

        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);

        validation(toMember);

        memberRepository.update(toId, toMember.getMoney() + money);
    }

    private void validation(Member toMember)  {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }

}
