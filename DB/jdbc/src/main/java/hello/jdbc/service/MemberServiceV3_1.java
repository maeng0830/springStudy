package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import java.sql.Connection;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * 트랜잭션 - 트랜잭션 매니저
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_1 {

	private final PlatformTransactionManager transactionManager;
	private final MemberRepositoryV3 memberRepository;

	public void accountTransfer(String fromId, String toId, int money) throws SQLException {
		// 트랜잭션 시작
		TransactionStatus status = transactionManager.getTransaction(
				new DefaultTransactionDefinition());

		try {
			// 비즈니스 로직 -> DB 작업
			bizLogic(fromId, toId, money);

			// commit - 트랜잭션 종료
			transactionManager.commit(status);
		} catch (Exception e) {
			// rollback - 트랜잭션 종료
			transactionManager.rollback(status);
			throw new IllegalStateException(e);
		}
		// 트랜잭션 매니저 내부에서, 트랜잭션이 종료되면 connection 관리를 자동으로 해준다. -> release 직접 X
	}

	private void bizLogic(String fromId, String toId, int money)
			throws SQLException {
		Member fromMember = memberRepository.findById(fromId);
		Member toMember = memberRepository.findById(toId);

		memberRepository.update(fromId, fromMember.getMoney() - money);
		validation(toMember);
		memberRepository.update(toId, toMember.getMoney() + money);
	}

	private void validation(Member toMember) {
		if (toMember.getMemberId().equals("ex")) {
			throw new IllegalStateException("이체 중 예외 발생");
		}
	}

	private void release(Connection con) {
		if (con != null) {
			try {
				con.setAutoCommit(true); // 커넥션이 반환될 때 기본 값으로 반환
				con.close(); // 커넥션 반환
			} catch (Exception e) {
				log.info("error", e);
			}
		}
	}
}
