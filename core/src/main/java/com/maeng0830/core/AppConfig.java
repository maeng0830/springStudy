package com.maeng0830.core;

import com.maeng0830.core.discount.DiscountPolicy;
import com.maeng0830.core.discount.RateDiscountPolicy;
import com.maeng0830.core.member.MemberRepository;
import com.maeng0830.core.member.MemberService;
import com.maeng0830.core.member.MemberServiceImpl;
import com.maeng0830.core.member.MemoryMemberRepository;
import com.maeng0830.core.order.OrderService;
import com.maeng0830.core.order.OrderServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // app 설정 정보
public class AppConfig {

    // 예상되는 Bean 호출
    // call AppConfig.memberService
    // call AppConfig.memberRepository
    // call AppConfig.memberRepository
    // call AppConfig.orderService
    // call AppConfig.memberRepository

    // 실제 Bean 호출
    // call AppConfig.memberService
    // call AppConfig.memberRepository
    // call AppConfig.orderService

    @Bean // 스프링 컨테이너에 등록 (키(메소드명) : 밸류(리턴 값))
    public MemberService memberService() {
        System.out.println("call AppConfig.memberService");
        return new MemberServiceImpl(memberRepository());
    }

    @Bean
    public MemberRepository memberRepository() {
        System.out.println("call AppConfig.memberRepository");
        return new MemoryMemberRepository();
    }

    @Bean
    public OrderService orderService() {
        System.out.println("call AppConfig.orderService");
        return new OrderServiceImpl(memberRepository(),
            discountPolicy());
    }

    @Bean
    public DiscountPolicy discountPolicy() {
        return new RateDiscountPolicy();
    }
}
