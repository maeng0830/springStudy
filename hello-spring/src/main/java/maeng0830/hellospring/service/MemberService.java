package maeng0830.hellospring.service;

import java.util.List;
import java.util.Optional;
import maeng0830.hellospring.domain.Member;
import maeng0830.hellospring.repository.MemberRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional // jpa를 사용하기 위함. jdbc, jdbc템플릿은 필요 x
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Long join(Member member) {
        validateDuplicateMember(member); // 중복 회원 검증

        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        memberRepository.findByName(member.getName())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
    }

     public List<Member> findMembers() {
        return memberRepository.findAll();
     }

     public Optional<Member> findOne(Long memberId) {
        return memberRepository.findById(memberId);
     }
}
