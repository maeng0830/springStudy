package com.maeng0830.core.beanfind;

import com.maeng0830.core.AppConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ApplicationContextInfoTest {

    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);


    @DisplayName("모든 빈 출력하기")
    @Test
    void findAllBean() {
        // given
        String[] beanDefinitionNames = ac.getBeanDefinitionNames();
        for (String beanDefinitionName: beanDefinitionNames) {
            Object bean = ac.getBean(beanDefinitionName);
            System.out.println("name = " + beanDefinitionName + " Object = " + bean);
        }
        // when

        // then
    }

    @DisplayName("애플리케이션 빈 출력하기")
    @Test
    void findApplicationBean() {
        // given
        String[] beanDefinitionNames = ac.getBeanDefinitionNames();
        for (String beanDefinitionName: beanDefinitionNames) {
            BeanDefinition beanDefinition = ac.getBeanDefinition(beanDefinitionName);

            if (beanDefinition.getRole() == BeanDefinition.ROLE_APPLICATION) {
                Object bean = ac.getBean(beanDefinitionName);
                System.out.println("name = " + beanDefinitionName + " Object = " + bean);
            }
        }
        // when

        // then
    }

}
