package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import hello.jdbc.repository.MemberRepositoryV2;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JDBC - ConnectionParam을 통한 트랜잭션
 */
@RequiredArgsConstructor
@Slf4j
public class MemberServiceV2 {

	private final MemberRepositoryV2 memberRepository;
	// service 계층에서 커넥션을 획득하기 위한 의존관계 주입
	private final DataSource dataSource;

	public void accountTransfer(String fromId, String toId, int money) throws SQLException {
		// 트랜잭션 동안 공유하게될 커넥션 획득
		Connection con = dataSource.getConnection();

		try {
			// 트랜잭션 시작
			con.setAutoCommit(false);

			// 비즈니스 로직
			// 하나의 트랜잭션에서 진행되어야할 로직이므로, 커넥션을 공유한다.
			bizLogic(con, fromId, toId, money);

			// 트랜잭션 종료 - 커밋
			con.commit();
		} catch (Exception e) {
			// 트랜잭션 종료 - 롤백
			con.rollback();
			throw new IllegalStateException(e);
		} finally {
			// 커넥션 풀에 커넥션 반환
			release(con);
		}
	}

	private void bizLogic(Connection con, String fromId, String toId, int money) throws SQLException {
		Member fromMember = memberRepository.findById(con, fromId);
		Member toMember = memberRepository.findById(con, toId);

		memberRepository.update(con, fromMember.getMemberId(), fromMember.getMoney() - money);

		validation(toMember);

		memberRepository.update(con, toMember.getMemberId(), toMember.getMoney() + money);
	}

	private void release(Connection con) {
		if (con != null) {
			try {
				// 커넥션 풀에 반환할 때는 기본 값으로 반환해주는 것이 좋다.
				con.setAutoCommit(true);
				// 커넥션 풀을 사용하므로, 커넥션 종료가 아닌 반환
				con.close();
			} catch (Exception e) {
				log.info("error", e);
			}
		}
	}

	private void validation(Member toMember) {
		if (toMember.getMemberId().equals("ex")) {
			throw new IllegalStateException("이체 중 예외 발생");
		}
	}
}
