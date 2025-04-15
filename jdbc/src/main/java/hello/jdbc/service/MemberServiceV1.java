package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;

@RequiredArgsConstructor
public class MemberServiceV1 {
    private final MemberRepositoryV1 memberRepository;

    // transaction 시작
    public void accountTransfer(String fromId, String toId, int money) throws SQLException  {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        // fromId에서 toId에게 money만큼 돈을 이체하는 로직
        // fromId의 돈을 갱신
        memberRepository.update(fromId, fromMember.getMoney()-money);
        // 예외 발생시 중단
        validation(toMember);
        // 예외가 발생하지 않으면 정상 이체
        memberRepository.update(toId, toMember.getMoney()+money);
    }

    // 이체 중 예외 발생 처리 메서드
    private void validation(Member tomember) {
        if (tomember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }
}
