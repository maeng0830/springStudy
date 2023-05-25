package hello.springtx.apply;

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
public class InternalCallV1Test {

	@Autowired
	CallService callService;

	@TestConfiguration
	static class InternalCallV1TestConfig {

		@Bean
		CallService callService() {
			return new CallService();
		}
	}

	static class CallService {

		public void external() {
			log.info("call external");

			printTxInfo();

			internal();
		}

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
	void internalCall() {
		callService.internal();
		// Getting transaction for [hello.springtx.apply.InternalCallV1Test$CallService.internal]
		// call internal
		// tx active=true
		// read only=false
		// Completing transaction for [hello.springtx.apply.InternalCallV1Test$CallService.internal]
	}

	@Test
	void externalCall() {
		/**
		 * 대상 객체의 내부에서 @Transactional이 적용된 대상 객체의 메소드를 호출할 경우, 트랜잭션이 적용되지 않는다.
		 * 1. callService.external()을 호출한다. 여기서 callService는 프록시 객체이다.
		 * 2. external()에는 @Transactionl이 적용되어 있지 않기 때문에, 프록시 객체는 트랜잭션 적용 없이 실제 객체의 external()을 호출한다.
		 * 3. external()은 내부에서 @Transactional이 적용된 internal()을 호출한다.
		 * 4. 그러나 프록시 객체의 internal()이 아닌, 실제 객체의 internal()을 호출하게 됨에 따라 트랜잭션이 적용되지 않는다.
		 */
		callService.external();
		// call external
		// tx active=false
		// read only=false
		// ** 트랜잭션이 적용되지 않는다.
		// call internal
		// tx active=false
		// read only=false
	}
}
