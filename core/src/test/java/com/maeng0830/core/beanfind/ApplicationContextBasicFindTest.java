package com.maeng0830.core.beanfind;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.maeng0830.core.AppConfig;
import com.maeng0830.core.member.MemberService;
import com.maeng0830.core.member.MemberServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ApplicationContextBasicFindTest {

    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

    @DisplayName("빈 이름으로 조회")
    @Test
    void findBeanByName() {
        // given
        MemberService memberService = ac.getBean("memberService", MemberService.class);
        assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
        // when

        // then
    }

    @DisplayName("인터페이스 타입으로만 조회")
    @Test
    void findBeanByType() {
        // given
        MemberService memberService = ac.getBean(MemberService.class);
        assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
        // when

        // then
    }

    @DisplayName("구체 타입으로 조회") // 구체에 의존하기 때문에 좋은 코드는 아니다.
    @Test
    void findBeanByType2() {
        // given
        MemberService memberService = ac.getBean("memberService", MemberServiceImpl.class);
        assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
        // when

        // then
    }

    @DisplayName("빈 이름으로 조회X")
    @Test
    void findBeanByNameX() {
        // given

        assertThrows(NoSuchBeanDefinitionException.class, () ->
            ac.getBean("xxxxx", MemberService.class));
        // when

        // then
    }

}
