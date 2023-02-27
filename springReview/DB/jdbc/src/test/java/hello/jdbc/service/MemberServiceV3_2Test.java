package hello.jdbc.service;

import static hello.jdbc.connection.ConnectionConst.PASSWORD;
import static hello.jdbc.connection.ConnectionConst.URL;
import static hello.jdbc.connection.ConnectionConst.USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

/**
 * 트랜잭션 - 트랜잭션매니저를 통한 트랜잭션 + 트랜잭션 템플릿
 */
class MemberServiceV3_2Test {

	private static final String MEMBER_A = "memberA";
	private static final String MEMBER_B = "memberB";
	private static final String MEMBER_EX = "ex";

	private final MemberServiceV3_2 memberService;
	private final MemberRepositoryV3 memberRepository;

	MemberServiceV3_2Test() {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setJdbcUrl(URL);
		dataSource.setUsername(USERNAME);
		dataSource.setPassword(PASSWORD);

		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(
				dataSource);

		memberRepository = new MemberRepositoryV3(dataSource);
		memberService = new MemberServiceV3_2(transactionManager, memberRepository);
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

		// 커밋
		memberService.accountTransfer(MEMBER_A, MEMBER_B, 2000);

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

		// 예외 발생
		// 롤백
		assertThatThrownBy(() -> memberService.accountTransfer(MEMBER_A, MEMBER_EX, 2000))
				.isInstanceOf(IllegalStateException.class);

		Member fromMember = memberRepository.findById(MEMBER_A);
		Member toMember = memberRepository.findById(MEMBER_EX);

		// 정상 이체 X
		// 롤백을 통해 돈은 트랜잭션 시작 전으로 복구된다.
		assertThat(fromMember.getMoney()).isEqualTo(10000);
		assertThat(toMember.getMoney()).isEqualTo(10000);
	}
}