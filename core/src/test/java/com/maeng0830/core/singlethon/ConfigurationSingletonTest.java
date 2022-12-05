package com.maeng0830.core.singlethon;

import com.maeng0830.core.AppConfig;
import com.maeng0830.core.member.MemberRepository;
import com.maeng0830.core.member.MemberServiceImpl;
import com.maeng0830.core.order.OrderServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ConfigurationSingletonTest {

    @Test
    @DisplayName("스프링 컨테이너의 싱글톤 유지 확인")
    void configuration() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(
            AppConfig.class);

        MemberServiceImpl memberService = ac.getBean("memberService", MemberServiceImpl.class);
        OrderServiceImpl orderService = ac.getBean("orderService", OrderServiceImpl.class);
        MemberRepository memberRepository = ac.getBean("memberRepository", MemberRepository.class);

        MemberRepository memberRepositoryForMemberService = memberService.getMemberRepository();
        MemberRepository memberRepositoryForOrderService = orderService.getMemberRepository();

        System.out.println(
            "memberRepositoryForMemberService = " + memberRepositoryForMemberService);
        System.out.println(
            "memberRepositoryForOrderService = " + memberRepositoryForOrderService);
        System.out.println("memberRepository = " + memberRepository);

        Assertions.assertThat(memberRepositoryForMemberService).isSameAs(memberRepositoryForOrderService);
        Assertions.assertThat(memberRepositoryForOrderService).isSameAs(memberRepository);
    }

    @Test
    void configurationDeep() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
        AppConfig bean = ac.getBean(AppConfig.class);

        System.out.println("bean = " + bean.getClass());
    }
}
