package maeng0830.hellospring.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import maeng0830.hellospring.domain.Member;
import maeng0830.hellospring.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest // 스프링 통합 테스트를 위한 어노테이션
@Transactional // 트랜잭션 덕분에 beforeEach, afterEach가 필요 없다. test 케이스에 트랜잭셔널이 적용되면, 테스트가 끝난후 롤백한다.
class MemberServiceIntegrationTest {

    // 생성자 주입을 권장하지만, 테스트의 경우 가장 편한 방법인 필드 주입을 해도 상관 없다.
    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository; // 구현체는 SpringConfig 설정대로 선택되어 주입된다.

    @Test
    void 회원가입() {
        // given
        Member member = new Member();
        member.setName("spring");

        // when
        Long saveId = memberService.join(member);

        // then
        Member findMember = memberService.findOne(saveId).get();
        assertThat(member.getName()).isEqualTo(findMember.getName());
    }

    @Test
    void 중복_회원_예외() {
        // given
        Member member1 = new Member();
        member1.setName("spring");

        Member member2 = new Member();
        member2.setName("spring");

        // when
        memberService.join(member1);
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(member2));
        assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");

/*
        try {
            memberService.join(member2);
            fail();
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
        }
*/
        // then
    }
}