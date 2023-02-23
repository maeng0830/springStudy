package hello.jdbc.service;

import static hello.jdbc.connection.ConnectionConst.PASSWORD;
import static hello.jdbc.connection.ConnectionConst.URL;
import static hello.jdbc.connection.ConnectionConst.USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 기본 동작 - 트랜잭션 적용 X
 */
class MemberServiceV1Test {

	private static final String MEMBER_A = "memberA";
	private static final String MEMBER_B = "memberB";
	private static final String MEMBER_EX = "ex";

	private final MemberServiceV1 memberServiceV1;
	private final MemberRepositoryV1 memberRepository;

	MemberServiceV1Test() {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setJdbcUrl(URL);
		dataSource.setUsername(USERNAME);
		dataSource.setPassword(PASSWORD);

		memberRepository = new MemberRepositoryV1(dataSource);
		memberServiceV1 = new MemberServiceV1(memberRepository);
	}

//	@BeforeEach
//	void before() {
//		HikariDataSource dataSource = new HikariDataSource();
//		dataSource.setJdbcUrl(URL);
//		dataSource.setUsername(USERNAME);
//		dataSource.setPassword(PASSWORD);
//
//		memberRepository = new MemberRepositoryV1(dataSource);
//		memberServiceV1 = new MemberServiceV1(memberRepository);
//	}

	@AfterEach
	void after() throws SQLException {
		memberRepository.delete(MEMBER_A);
		memberRepository.delete(MEMBER_B);
		memberRepository.delete(MEMBER_EX);
	}

	@Test
	@DisplayName("정상 이체")
	void accountTransfer() throws SQLException {
		memberRepository.save(new Member(MEMBER_A, 10000));
		memberRepository.save(new Member(MEMBER_B, 10000));

		memberServiceV1.accountTransfer(MEMBER_A, MEMBER_B, 2000);

		Member fromMember = memberRepository.findById(MEMBER_A);
		Member toMember = memberRepository.findById(MEMBER_B);
		assertThat(fromMember.getMoney()).isEqualTo(8000);
		assertThat(toMember.getMoney()).isEqualTo(12000);
	}

	@Test
	@DisplayName("이제 중 예외 발생")
	void accountTransferEx() throws SQLException {
		memberRepository.save(new Member(MEMBER_A, 10000));
		memberRepository.save(new Member(MEMBER_EX, 10000));

		// MemberA의 money - 2000는 성공
		// 예외 발생
		// MemberEX의 money + 2000는 실패
		assertThatThrownBy(() -> memberServiceV1.accountTransfer(MEMBER_A, MEMBER_EX, 2000))
				.isInstanceOf(IllegalStateException.class);

		Member fromMember = memberRepository.findById(MEMBER_A);
		Member toMember = memberRepository.findById(MEMBER_EX);

		// 정상 이체 X
		// MemberA의 돈은 줄었으나, MemberEX의 돈은 그대로...
		assertThat(fromMember.getMoney()).isEqualTo(8000);
		assertThat(toMember.getMoney()).isEqualTo(10000);
	}
}