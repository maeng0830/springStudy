package hello.jdbc.repository;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.connection.ConnectionConst;
import hello.jdbc.domain.Member;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

class MemberRepositoryV1Test {

	MemberRepositoryV1 repository;

	@BeforeEach
	void beforeEach() {
		// 매번 새로운 커넥션 획득
//		DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

		// ConnectionPool 사용
		// HikariCP는 가장 대중적으로 사용되는 커넥션 풀이다.
		// HikariDataSource는 HikariCP를 사용하는 DataSource 구현체이다.
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setJdbcUrl(URL);
		dataSource.setUsername(USERNAME);
		dataSource.setPassword(PASSWORD);

		// DriverMangerDataSource, HikariDataSource는 DataSource의 구현체이다.
		repository = new MemberRepositoryV1(dataSource);
	}

	@Test
	void crud() throws SQLException {
		// save
		Member member = new Member("memberV0", 10000);
		repository.save(member);

		// findById
		Member findMember = repository.findById(member.getMemberId());
		assertThat(findMember).isEqualTo(member);

		// update
		repository.update(member.getMemberId(), 20000);
		Member updateMember = repository.findById(member.getMemberId());
		assertThat(updateMember.getMoney()).isEqualTo(20000);

		// delete
		repository.delete(member.getMemberId());
		assertThatThrownBy(() -> repository.findById(member.getMemberId()))
				.isInstanceOf(NoSuchElementException.class);
	}
}