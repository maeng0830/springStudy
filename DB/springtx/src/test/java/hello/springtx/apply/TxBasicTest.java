package hello.springtx.apply;

import static org.assertj.core.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@SpringBootTest
public class TxBasicTest {

	// 실제 객체가 아닌, 프록시 객체(실제 객체를 상속)가 주입된다.
	@Autowired
	BasicService basicService;

	@TestConfiguration
	static class TxApplyBasicConfig {
		@Bean
		BasicService basicService() {
			return new BasicService();
		}
	}

	// 특정 클래스의 클래스 또는 메소드에 @Transactional이 하나라도 적용되어 있으면,
	// 해당 클래스의 실제 객체가 아닌 프록시 객체가 스프링 빈으로 컨테이너에 등록된다.
	// 프록시 객체는 실제 객체를 상속하고 있으며, 프록시 객체는 내부에서 실제 객체를 참조한다.
	@Slf4j
	static class BasicService {

		@Transactional
		public void tx() {
			log.info("call tx");

			// 트랜잭션 적용 여부 확인
			boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();

			log.info("tx active={}", txActive);
		}

		public void nonTx() {
			log.info("call tx");

			// 트랜잭션 적용 여부 확인
			boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();

			log.info("tx active={}", txActive);
		}
	}

	@Test
	void proxyCheck() {
		log.info("aop class={}", basicService.getClass());

		// AOP 프록시 적용 확인
		assertThat(AopUtils.isAopProxy(basicService)).isTrue();
	}

	@Test
	void txTest() {
		basicService.tx();
		// Getting transaction for [hello.springtx.apply.TxBasicTest$BasicService.tx]
		// call tx
		// tx active=true
		// Completing transaction for [hello.springtx.apply.TxBasicTest$BasicService.tx]

		basicService.nonTx();
		// call tx
		// tx active=false
	}
}
