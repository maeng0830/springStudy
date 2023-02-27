package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * JDBC - 트랜잭션매니저를 통한 트랜잭션 + 트랜잭션 AOP(@Transactional)
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_3 {

	private final MemberRepositoryV3 memberRepository;

	/**
	 * 트랜잭션 AOP 적용
	 *
	 */
	@Transactional
	public void accountTransfer(String fromId, String toId, int money) throws SQLException {
		bizLogic(fromId, toId, money);
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
