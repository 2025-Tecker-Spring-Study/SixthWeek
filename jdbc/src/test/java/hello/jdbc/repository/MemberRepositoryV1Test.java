package hello.jdbc.repository;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Assertions;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
class MemberRepositoryV1Test {

    MemberRepositoryV1 repository;

    @BeforeEach
    void beforeEach() throws Exception {
        // 기본 DriverManager - 항상 새 커넥션 획득
//        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);


        // 커넥션 풀링
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

        repository = new MemberRepositoryV1(dataSource);
    }


    @Test
    void crud() throws SQLException, InterruptedException {
        log.info("start");

        // save
        Member member = new Member("memberV0", 10000);
        repository.save(member);

        // findById
        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember = {}", findMember);
//        [Test worker] INFO hello.jdbc.repository.MemberRepositoryV0Test -- findMember = Member(memberId=memberV0, money=10000)

        log.info("member == findMember {}", member == findMember);
        log.info("member equals findMember {}", member.equals(findMember));
//        12:53:03.023 [Test worker] INFO hello.jdbc.repository.MemberRepositoryV0Test -- member == findMember false
//        12:53:03.023 [Test worker] INFO hello.jdbc.repository.MemberRepositoryV0Test -- member equals findMember true

        // 사실 두 데이터는 다른 인스턴스이다.
        // lombok의 @Data를 이용해 모든 필드를 이용해 equals로 만들어줌
        // 따라서 .equals 사용시 두 객체가 같다고 나온다.

        Assertions.assertThat(findMember).isEqualTo(member);


        //update
        repository.update(member.getMemberId(), 25000);
        Member updatedMember = repository.findById(member.getMemberId());
        Assertions.assertThat(updatedMember.getMoney()).isEqualTo(25000);


        // delete
        repository.delete(member.getMemberId());
        // 삭제되었으므로 member 객체를 가져와서 검증할 수 없다.
        // NosuchElementException을 이용해 데이터가 DB에서 사라졌음을 검증한다.
        Assertions.assertThatThrownBy(() -> repository.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);

        // 위와 같은 기본적 CRUD 로직에 의해 데이터가 생성, 조회, 수정 이후 삭제까지 된다.
        // 따라서 반복적으로 테스트할수도 있다.
        // 하지만 이 방법은 좋은 방법은 아니고 추후에 트랜잭션을 통해서 DB를 청소해주는 로직을 이용할 것이다.

    }
}
