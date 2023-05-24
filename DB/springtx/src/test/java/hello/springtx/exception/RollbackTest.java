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
	 * 언체크 예외 RuntimeException, Error와 그 하위 예외가 발생하면 트랜잭션을 롤백한다.
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
//		Creating new transaction with name [hello.springtx.exception.RollbackTest$RollbackService.uncheckedException]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
//		Opened new EntityManager [SessionImpl(1206577437<open>)] for JPA transaction
//		Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@2862271a]
//		Getting transaction for [hello.springtx.exception.RollbackTest$RollbackService.uncheckedException]
//		call uncheckedException
//		Completing transaction for [hello.springtx.exception.RollbackTest$RollbackService.uncheckedException] after exception: java.lang.RuntimeException
//		Initiating transaction rollback **
//		Rolling back JPA transaction on EntityManager [SessionImpl(1206577437<open>)] **
//		Closing JPA EntityManager [SessionImpl(1206577437<open>)] after transaction
	}

	@Test
	void checkedException() {
		assertThatThrownBy(() -> rollbackService.checkedException())
				.isInstanceOf(MyException.class);
//		Creating new transaction with name [hello.springtx.exception.RollbackTest$RollbackService.checkedException]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
//		Opened new EntityManager [SessionImpl(252451553<open>)] for JPA transaction
//		Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@41d877bb]
//		Getting transaction for [hello.springtx.exception.RollbackTest$RollbackService.checkedException]
//		call checkedException
//		Completing transaction for [hello.springtx.exception.RollbackTest$RollbackService.checkedException] after exception: hello.springtx.exception.RollbackTest$MyException
//		Initiating transaction commit **
//		Committing JPA transaction on EntityManager [SessionImpl(252451553<open>)] **
//		Closing JPA EntityManager [SessionImpl(252451553<open>)] after transaction
	}

	@Test
	void rollbackFor() {
		assertThatThrownBy(() -> rollbackService.rollbackFor())
				.isInstanceOf(MyException.class);
//		Creating new transaction with name [hello.springtx.exception.RollbackTest$RollbackService.rollbackFor]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT,-hello.springtx.exception.RollbackTest$MyException
//		Opened new EntityManager [SessionImpl(1173504479<open>)] for JPA transaction
//		Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@74f649a7]
//		Getting transaction for [hello.springtx.exception.RollbackTest$RollbackService.rollbackFor]
//		call rollbackFor
//		Completing transaction for [hello.springtx.exception.RollbackTest$RollbackService.rollbackFor] after exception: hello.springtx.exception.RollbackTest$MyException
//		Initiating transaction rollback **
//		Rolling back JPA transaction on EntityManager [SessionImpl(1173504479<open>)] **
//		Closing JPA EntityManager [SessionImpl(1173504479<open>)] after transaction
	}
}
