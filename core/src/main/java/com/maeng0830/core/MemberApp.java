package com.maeng0830.core;

import com.maeng0830.core.member.Grade;
import com.maeng0830.core.member.Member;
import com.maeng0830.core.member.MemberService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MemberApp {

    public static void main(String[] args) {
        /*AppConfig appConfig = new AppConfig();
        MemberService memberService = appConfig.memberService();*/

        // ApplicationContext는 스프링 컨테이너
        // AppConfig 내부의 bean을 스프링 컨테이너에 등록
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);

        // (bean으로 등록된 메소드 이름, 반환 타입)
        MemberService memberService = applicationContext.getBean("memberService",
            MemberService.class);

        Member member = new Member(1L, "memberA", Grade.VIP);
        memberService.join(member);

        Member findMember = memberService.findMember(1L);

        System.out.println("member = " + member.getName());
        System.out.println("findMember = " + findMember.getName());

    }
}
