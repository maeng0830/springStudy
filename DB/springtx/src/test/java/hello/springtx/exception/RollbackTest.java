package hello.springtx.exception;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class RollbackTest {

	@Autowired
	RollbackService rollbackService;

	@TestConfiguration
	static class RollbackTestConfig {
		@Bean
		RollbackService rollbackService() {
			return new RollbackService();
		}
	}

	/**
	 * 언체크 예외인 RuntimeException, Error와 그 하위 예외가 발생하면 트랜잭션을 롤백한다.
	 * 체크 예외인 Exception과 그 하위 예외가 발생하면 트랜잭션 커밋한다.
	 */
	@Slf4j
	static class RollbackService {
		// 언체크 예외 발생: 롤백
		@Transactional
		public void uncheckedException() {
			log.info("call uncheckedException");
			throw new RuntimeException();
		}

		// 체크 예외 발생: 커밋
		@Transactional
		public void checkedException() throws MyException {
			log.info("call checkedException");
			throw new MyException();
		}

		// 체크 예외 발생: 강제 롤백
		@Transactional(rollbackFor = MyException.class)
		public void rollbackFor() throws MyException {
			log.info("call rollbackFor");
			throw new MyException();
		}
	}

	static class MyException extends Exception {
	}

	@Test
	void uncheckedException() {
		assertThatThrownBy(() -> rollbackService.uncheckedException())
						.isInstanceOf(RuntimeException.class);
//		...
//		** Initiating transaction rollback **
//		** Rolling back JPA transaction on EntityManager [SessionImpl(1206577437<open>)] **
//		...
	}

	@Test
	void checkedException() {
		assertThatThrownBy(() -> rollbackService.checkedException())
				.isInstanceOf(MyException.class);
//		...
//		** Initiating transaction commit **
//		** Committing JPA transaction on EntityManager [SessionImpl(252451553<open>)] **
//		...
	}

	@Test
	void rollbackFor() {
		assertThatThrownBy(() -> rollbackService.rollbackFor())
				.isInstanceOf(MyException.class);
//		...
//		** Initiating transaction rollback **
//		** Rolling back JPA transaction on EntityManager [SessionImpl(1173504479<open>)] **
//		...
	}
}
