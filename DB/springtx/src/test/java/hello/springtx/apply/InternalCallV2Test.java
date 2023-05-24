package hello.springtx.apply;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@SpringBootTest
public class InternalCallV2Test {

	@Autowired
	CallService callService;

	@TestConfiguration
	static class InternalCallV1TestConfig {

		@Bean
		CallService callService() {
			return new CallService(internalService());
		}

		@Bean
		InternalService internalService() {
			return new InternalService();
		}
	}

	@Slf4j
	@RequiredArgsConstructor
	static class CallService {

		// 프록시 객체 주입
		private final InternalService internalService;

		public void external() {
			log.info("call external");

			printTxInfo();

			internalService.internal();
		}

		private void printTxInfo() {
			boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
			log.info("tx active={}", txActive);

			boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
			log.info("read only={}", readOnly);
		}
	}

	// 내부 호출할 메소드를 별도의 클래스로 분리
	static class InternalService {

		@Transactional
		public void internal() {
			log.info("call internal");
			printTxInfo();
		}

		private void printTxInfo() {
			boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
			log.info("tx active={}", txActive);

			boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
			log.info("read only={}", readOnly);
		}
	}

	@Test
	void externalCallV2() {
		/**
		 * 1. callService.external()을 호출한다. 여기서 callService는 실제 객체이다.
		 * 2. 실제 객체의 external()을 호출한다.
		 * 3. external()은 @Transactional이 적용된 internalService의 internal()을 호출한다.
		 * 4. internalService는 프록시 객체이며, 프록시 객체의 internal()이 호출된다.
		 */
		callService.external();
		// call external
		// tx active=false
		// read only=false
		// ** 트랜잭션이 적용된다.
		// Getting transaction for [hello.springtx.apply.InternalCallV2Test$InternalService.internal]
		// call internal
		// tx active=true
		// read only=false
		// Completing transaction for [hello.springtx.apply.InternalCallV2Test$InternalService.internal]
	}
}
