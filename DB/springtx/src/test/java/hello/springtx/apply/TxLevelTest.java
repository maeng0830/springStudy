package hello.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@SpringBootTest
public class TxLevelTest {

	@Autowired
	LevelService levelService;

	@TestConfiguration
	static class TxLevelTestConfig {
		@Bean
		LevelService levelService() {
			return new LevelService();
		}
	}

	// @Transactional의 적용 규칙
	// 클래스에 @Transactional을 적용하면 메소드에도 자동 적용된다.
	// 클래스에 적용된 규칙을 먼저 적용한다. 그리고 메소드에 적용된 규칙이 있을 경우 덮어 씌운다.
	@Slf4j
	@Transactional(readOnly = true)
	static class LevelService {

		// @Transactional(readOnly = false) 적용
		@Transactional(readOnly = false)
		public void write() {
			log.info("call write");
			printTxInfo();
		}

		// @Transactional(readOnly = true) 적용
		public void read() {
			log.info("call read");
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
	void orderTest() {
		levelService.write();
		// Getting transaction for [hello.springtx.apply.TxLevelTest$LevelService.write]
		// call write
		// tx active=true
		// read only=false
		// Completing transaction for [hello.springtx.apply.TxLevelTest$LevelService.write]

		levelService.read();
		// Getting transaction for [hello.springtx.apply.TxLevelTest$LevelService.read]
		// call read
		// tx active=true
		// read only=true
		// Completing transaction for [hello.springtx.apply.TxLevelTest$LevelService.read]
	}
}
