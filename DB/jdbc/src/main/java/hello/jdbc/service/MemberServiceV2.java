package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 트랜잭션 - 파라미터 연동, 풀을 고려한 종료
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

	private final DataSource dataSource;
	private final MemberRepositoryV2 memberRepository;

	public void accountTransfer(String fromId, String toId, int money) throws SQLException {
		// 한 트랜잭션 동안 동일하게 사용될 connection 불러오기
		Connection con = dataSource.getConnection();

		try {
			// 트랜잭션 시작
			con.setAutoCommit(false);

			// 비즈니스 로직 -> DB 작업
			bizLogic(con, fromId, toId, money);

			// commit - 트랜잭션 종료
			con.commit();
		} catch (Exception e) {
			// rollback - 트랜잭션 종료
			con.rollback();
			throw new IllegalStateException(e);
		} finally {
			release(con);
		}
	}

	private void bizLogic(Connection con, String fromId, String toId, int money)
			throws SQLException {
		Member fromMember = memberRepository.findById(con, fromId);
		Member toMember = memberRepository.findById(con, toId);

		memberRepository.update(con, fromId, fromMember.getMoney() - money);
		validation(toMember);
		memberRepository.update(con, toId, toMember.getMoney() + money);
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
