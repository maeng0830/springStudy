package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * JDBC - 트랜잭션매니저를 통한 트랜잭션 + 트랜잭션 템플릿
 */
@Slf4j
public class MemberServiceV3_2 {

	private final TransactionTemplate txTemplate;
	private final MemberRepositoryV3 memberRepository;

	public MemberServiceV3_2(PlatformTransactionManager transactionManager, MemberRepositoryV3 memberRepository) {
		// 트랜잭션 템플릿을 사용하기 위해서는 트랜잭션 매니저가 필요하다.
		this.txTemplate = new TransactionTemplate(transactionManager);
		this.memberRepository = memberRepository;
	}


	public void accountTransfer(String fromId, String toId, int money) throws SQLException {
		/**
		 * 트랜잭션 템플릿 -> 트랜잭션 시작, 커밋 or 롤백 코드를 제거
		 * 1-1. 비즈니스 로직 정상 수행 -> 커밋
		 * 1-2. 언체크 예외 발생 -> 롤백
		 */
		txTemplate.executeWithoutResult((status) -> {
			try {
				// 비즈니스 로직
				bizLogic(fromId, toId, money);
			} catch (SQLException e) {
				// 해당 람다에서 체크 예외를 밖으로 던질 수 없다.
				// 체크 예외(SQLException) -> 언체크 예외(IllegalStateException)
				throw new IllegalStateException(e);
			}
		});

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
