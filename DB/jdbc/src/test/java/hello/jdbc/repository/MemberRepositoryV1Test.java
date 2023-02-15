package hello.jdbc.repository;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.connection.ConnectionConst;
import hello.jdbc.domain.Member;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Slf4j
class MemberRepositoryV1Test {

	MemberRepositoryV1 repository;

	@BeforeEach
	void beforeEach() {
		// 기본 DriverManager - 항상 새로운 커넥션을 획득
//		DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

		// ConnectionPool - 미리 커넥션을 pool에 생성해 둔다.
		// connectionPool의 경우, close메소드 실행시 connection을 다시 pool로 반환한다.
		// 동시 요청, 즉 멀티 쓰레드로 처리할 경우, 동시 처리되는 쓰레드만큼 pool에서 connection을 가져다 사용한다.
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setPoolName("myPool");
		dataSource.setJdbcUrl(URL);
		dataSource.setUsername(USERNAME);
		dataSource.setPassword(PASSWORD);
		dataSource.setMaximumPoolSize(10);

		repository = new MemberRepositoryV1(dataSource);
	}

	@Test
	void crud() throws SQLException, InterruptedException {
		// save
		Member member = new Member("memberV4", 10000);
		repository.save(member);

		// findById
		Member findMember = repository.findById(member.getMemberId());
		log.info("findMember={}", findMember);
		assertThat(findMember).isEqualTo(member);

		// update
		repository.update(member.getMemberId(), 20000);
		Member updateMember = repository.findById(member.getMemberId());
		assertThat(updateMember.getMemberId()).isEqualTo(member.getMemberId());
		assertThat(updateMember.getMoney()).isEqualTo(20000);

		// delete
		repository.delete(member.getMemberId());
		assertThatThrownBy(() -> repository.findById(member.getMemberId()))
				.isInstanceOf(NoSuchElementException.class);

		Thread.sleep(1000);
	}
}