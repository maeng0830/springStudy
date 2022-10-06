package maeng0830.hellospring;

import maeng0830.hellospring.repository.MemberRepository;
import maeng0830.hellospring.repository.MemoryMemberRepository;
import maeng0830.hellospring.service.MemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    @Bean
    public MemberService memberService() {
        return new MemberService(memberRepository());
    }

    @Bean
    public MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }
}
