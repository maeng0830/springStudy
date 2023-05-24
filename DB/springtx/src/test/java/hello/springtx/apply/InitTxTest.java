package hello.springtx.apply;

import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@SpringBootTest
public class InitTxTest {

	@Autowired
	Hello hello;

	@TestConfiguration
	static class InitTxTestConfig {
		@Bean
		Hello hello() {
			return new Hello();
		}
	}

	@Slf4j
	static class Hello {

		// 초기화 코드(ex: @PostConstruct)의 @Transactional은 적용되지 않는다.
		// 초기화 코드가 먼저 호출되고, 그 다음에 트랜잭션 AOP가 적용되기 때문이다.
		@PostConstruct
		@Transactional
		public void initV1() {
			boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
			log.info("Hello init @PostConstruct tx active={}", isActive);
		}

		@EventListener(ApplicationReadyEvent.class) // 스프링 초기화가 완료된 후 호출
		@Transactional
		public void initV2() {
			boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
			log.info("Hello init @EventListener(ApplicationReadyEvent.class) tx active={}", isActive);
		}
	}

	@Test
	void go() {
		// Hello init @PostConstruct tx active=false

		// Getting transaction for [hello.springtx.apply.InitTxTest$Hello.initV2]
		// Hello init @EventListener(ApplicationReadyEvent.class) tx active=true
		// Completing transaction for [hello.springtx.apply.InitTxTest$Hello.initV2]
	}

}
