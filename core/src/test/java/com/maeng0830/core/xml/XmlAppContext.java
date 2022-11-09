package com.maeng0830.core.xml;

import static org.assertj.core.api.Assertions.assertThat;

import com.maeng0830.core.member.MemberService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

public class XmlAppContext {

    @Test
    void xmlAppContext() {
        // given
        ApplicationContext ac = new GenericXmlApplicationContext(
            "appConfig.xml");
        MemberService memberService = ac.getBean("memberService", MemberService.class);
        assertThat(memberService).isInstanceOf(MemberService.class);
        // when

        // then
    }
}
