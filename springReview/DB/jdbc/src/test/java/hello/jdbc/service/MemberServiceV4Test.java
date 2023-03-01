package hello.jdbc.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepository;
import hello.jdbc.repository.MemberRepositoryV4_1;
import hello.jdbc.repository.MemberRepositoryV4_2;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * JDBC - 트랜잭션매니저를 통한 트랜잭션 + 트랜잭션 AOP(@Transactional)
 * DataSource, transactionManager 자동 등록
 * 체크 예외 -> 언체크 예외
 * MemberRepository 인터페이스 사용
 */
@SpringBootTest
class MemberServiceV4Test {

	private static final String MEMBER_A = "memberA";
	private static final String MEMBER_B = "memberB";
	private static final String MEMBER_EX = "ex";

	private final MemberServiceV4 memberService;

	private final MemberRepository memberRepository;

	@Autowired
	public MemberServiceV4Test(MemberServiceV4 memberService,
			MemberRepository memberRepository) {
		this.memberService = memberService;
		this.memberRepository = memberRepository;
	}

	@TestConfiguration
	static class TestConfig {
		private final DataSource dataSource;

		public TestConfig(DataSource dataSource) {
			this.dataSource = dataSource;
		}

		@Bean
		MemberRepository memberRepository() {
//			return new MemberRepositoryV4_1(dataSource); // 단순 예외 변환
			return new MemberRepositoryV4_2(dataSource); // 스프링 예외 변환
		}

		@Bean
		MemberServiceV4 memberServiceV4() {
			return new MemberServiceV4(memberRepository());
		}
	}

	@AfterEach
	void after() {
		memberRepository.delete(MEMBER_A);
		memberRepository.delete(MEMBER_B);
		memberRepository.delete(MEMBER_EX);
	}

	@Test
	@DisplayName("정상 이체")
	void accountTransfer() {
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
	void accountTransferEx() {
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