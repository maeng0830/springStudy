package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import hello.jdbc.repository.MemberRepositoryV3;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * JDBC - 트랜잭션매니저(트랜잭션 동기화 매니저)를 통한 트랜잭션
 */
@RequiredArgsConstructor
@Slf4j
public class MemberServiceV3_1 {

	// 트랜잭션 매니저를 주입 받는다. 지금은 JDBC 기술을 사용하기 때문에 DataSourceTransactionManager 구현체를 주입 받는다.
	private final PlatformTransactionManager transactionManager;
	private final MemberRepositoryV3 memberRepository;


	public void accountTransfer(String fromId, String toId, int money) throws SQLException {
		/**
		 * 1. 트랜잭션매니저는 내부에서 DataSource를 사용해서 커넥션을 생성한다.
		 * 2. 커넥션을 수동 커밋 모드로 변경해서 실제 데이터베이스 트랜잭션을 시작한다.
		 * 3. 해당 커넥션을 트랜잭션 동기화 매니저에 보관한다.
		 * 4. 트랜잭션 동기화 매니저는 쓰레드 로컬에 커넥션을 보관한다. -> 멀티 쓰레드 환경에서 안전하게 커넥션 보관
		 * TransactionStatus 객체에는 현재 트랜잭션 상태 정보가 담겨져있다.
		 */
		TransactionStatus status = transactionManager.getTransaction(
				new DefaultTransactionDefinition());

		try {
			/**
			 * DB작업이 포함된 비즈니스 로직을 실행하면서 repository의 메소드들을 호출한다.
			 * repository의 메소드들은 DataSourceUtils.getConnection()을 통해 트랜잭션 동기화 매니저에 보관된 커넥션을 꺼내 사용한다.
			 * 이것을 통해 자연스럽게 동일한 커넥션을 사용하게 된다 -> 트랜잭션 유지
			 * 해당 커넥션을 통해 쿼리를 데이터베이스에 전달한다.
			 */
			bizLogic(fromId, toId, money);

			/**
			 * transactionManager.commit()
			 * 1. 트랜잭션 매니저가 트랜잭션 동기화 매니저에서 동기화된 커넥션을 획득
			 * 2. 해당 커넥션으로 con.commit() 호출
			 * 3. 리소스 정리 - con.setAutoCommit(true) -> con.close
			 */
			transactionManager.commit(status);
		} catch (Exception e) {
			/**
			 * transactionManager.rollback()
			 * 1. 트랜잭션 매니저가 트랜잭션 동기화 매니저에서 동기화된 커넥션을 획득
			 * 2. 해당 커넥션으로 con.rollback() 호출
			 * 3. 리소스 정리 - con.setAutoCommit(true) -> con.close
			 */
			transactionManager.rollback(status);
			throw new IllegalStateException(e);
		}
	}

	private void bizLogic(String fromId, String toId, int money) throws SQLException {
		Member fromMember = memberRepository.findById(fromId);
		Member toMember = memberRepository.findById(toId);

		memberRepository.update(fromMember.getMemberId(), fromMember.getMoney() - money);

		validation(toMember);

		memberRepository.update(toMember.getMemberId(), toMember.getMoney() + money);
	}

	private void validation(Member toMember) {
		if (toMember.getMemberId().equals("ex")) {
			throw new IllegalStateException("이체 중 예외 발생");
		}
	}
}
