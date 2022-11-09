package com.maeng0830.core.beanfind;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.maeng0830.core.member.Member;
import com.maeng0830.core.member.MemberRepository;
import com.maeng0830.core.member.MemoryMemberRepository;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class ApplicationContextSameBeanFindTest {
    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(SameBeanConfig.class);

    @DisplayName("타입으로 조회시 같은 타입이 둘 이상 있으면, 중복 오류 발생")
    @Test
    void findBeanByTypeDuplicate() {
        // given
        assertThrows(NoUniqueBeanDefinitionException.class, () -> ac.getBean(MemberRepository.class));
        // when

        // then
    }

    @DisplayName("타입으로 조회시 같은 타입이 둘 이상 있으면, 빈 이름을 지정해주면 된다.")
    @Test
    void findBeanByName() {
        // given
        MemberRepository memberRepository = ac.getBean("memberRepository1", MemberRepository.class);
        assertThat(memberRepository).isInstanceOf(MemberRepository.class);
        // when

        // then
    }

    @DisplayName("특정 타입을 모두 조회하기")
    @Test
    void findAllBeanByType() {
        // given
        Map<String, MemberRepository> beansOfType = ac.getBeansOfType(MemberRepository.class);

        for (String key: beansOfType.keySet()) {
            System.out.println("key = " + key + " value" + beansOfType.get(key));
        }
        System.out.println("beansOfType = " + beansOfType);
        assertThat(beansOfType.size()).isEqualTo(2);
        // when

        // then
    }

    @Configuration
    static class SameBeanConfig {
        @Bean
        public MemberRepository memberRepository1() {
            return new MemoryMemberRepository();
        }

        @Bean
        public MemberRepository memberRepository2() {
            return new MemoryMemberRepository();
        }
    }
}
