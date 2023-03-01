package hello.jdbc.exception.translator;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.springframework.jdbc.support.JdbcUtils.closeConnection;
import static org.springframework.jdbc.support.JdbcUtils.closeStatement;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.connection.ConnectionConst;
import hello.jdbc.domain.Member;
import hello.jdbc.repository.ex.MyDbException;
import hello.jdbc.repository.ex.MyDuplicateKeyException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

public class ExTranslatorV1Test {

	private final Repository repository;
	private final Service service;

	ExTranslatorV1Test() {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setJdbcUrl(URL);
		dataSource.setUsername(USERNAME);
		dataSource.setPassword(PASSWORD);

		this.repository = new Repository(dataSource);

		this.service = new Service(repository);
	}

	@Test
	void duplicateKeySave() {
		service.create("myId");
		service.create("myId");//같은 ID 저장 시도
	}

	@Slf4j
	@RequiredArgsConstructor
	static class Service {
		private final Repository repository;

		public void create(String memberId) {
			try {
				repository.save(new Member(memberId, 0));
				log.info("saveId={}", memberId);
			} catch (MyDuplicateKeyException e) {
				log.info("키 중복, 복구 시도");
				String retryId = generateNewId(memberId);
				log.info("retryId={}", retryId);
				repository.save(new Member(retryId, 0));
			}
		}

		private String generateNewId(String memberId) {
			return memberId + new Random().nextInt(10000);
		}
	}

	@RequiredArgsConstructor
	static class Repository {
		private final DataSource dataSource;

		public Member save(Member member) {
			String sql = "insert into member(member_id, money) values(?, ?)";
			Connection con = null;
			PreparedStatement pstmt = null;
			try {
				con = dataSource.getConnection();
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, member.getMemberId());
				pstmt.setInt(2, member.getMoney());
				pstmt.executeUpdate();
				return member;
			} catch (SQLException e) {
				/**
				 * h2 db 에러코드를 통해 중복키 예외임을 확인
				 * 맞을 경우 해당하는 언체크 예외를 던진다.
				 */
				if (e.getErrorCode() == 23505) {
					throw new MyDuplicateKeyException(e);
				}

				throw new MyDbException(e);
			} finally {
				closeStatement(pstmt);
				closeConnection(con);
			}
		}
	}
}
