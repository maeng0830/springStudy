package hello.springtx.propagation;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

@Slf4j
@SpringBootTest
class MemberServiceTest {

	@Autowired
	MemberService memberService;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	LogRepository logRepository;

	/**
	 * memberService    @Transactional: OFF
	 * memberRepository @Transactional: ON
	 * logRepository    @Transactional: ON
	 */
	@Test
	void outerTxOff_success() {
		//given
		String username = "outerTxOff_success";

		//when
		memberService.joinV1(username);

		//then: 모든 데이터가 정상 저장된다.
		assertThat(memberRepository.find(username).isPresent()).isTrue();
		assertThat(logRepository.find(username).isPresent()).isTrue();
	}

	/**
	 * memberService    @Transactional: OFF
	 * memberRepository @Transactional: ON
	 * logRepository    @Transactional: ON Exception
	 */
	@Test
	void outerTxOff_fail() {
		//given
		String username = "로그예외_outerTxOff_fail";

		//when
		assertThatThrownBy(() -> memberService.joinV1(username))
				.isInstanceOf(RuntimeException.class);

		//then: logRepository.save()는 언체크예외 발생으로 롤백된다.
		assertThat(memberRepository.find(username).isPresent()).isTrue();
		assertThat(logRepository.find(username).isEmpty()).isTrue();
	}

	/**
	 * memberService    @Transactional: ON
	 * memberRepository @Transactional: OFF
	 * logRepository    @Transactional: OFF
	 */
	@Test
	void singleTx() {
		//given
		String username = "singleTx";

		//when
		memberService.joinV1(username);

		//then: 모든 데이터가 정상 저장된다.
		assertThat(memberRepository.find(username).isPresent()).isTrue();
		assertThat(logRepository.find(username).isPresent()).isTrue();
	}

	/**
	 * memberService    @Transactional: ON
	 * memberRepository @Transactional: ON
	 * logRepository    @Transactional: ON
	 */
	@Test
	void outerTxOn_success() {
		//given
		String username = "outerTxOn_success";

		//when
		memberService.joinV1(username);

		//then: 모든 데이터가 정상 저장된다.
		assertThat(memberRepository.find(username).isPresent()).isTrue();
		assertThat(logRepository.find(username).isPresent()).isTrue();
	}

	/**
	 * memberService    @Transactional: ON
	 * memberRepository @Transactional: ON
	 * logRepository    @Transactional: ON Exception
	 */
	@Test
	void outerTxOn_fail() {
		//given
		String username = "로그예외_outerTxOn_fail";

		//when
		assertThatThrownBy(() -> memberService.joinV1(username))
				.isInstanceOf(RuntimeException.class);

		//then: 모든 데이터가 롤백된다.
		// 사실 이 경우, 외부 트랜잭션(MemberService.joinV1())은 내부 트랜잭션(LogRepository.save())이 표시한 rollBackOnly를 참고하지 않는다.
		// 내부 트랜잭션에서 발생한 언체크 예외가 외부 트랜잭션으로 올라왔기 때문에, 외부 트랜잭션도 바로 롤백 호출을 한다.
		// 그리고 물리 트랜잭션도 롤백된다.
		assertThat(memberRepository.find(username).isEmpty()).isTrue();
		assertThat(logRepository.find(username).isEmpty()).isTrue();
	}

	/**
	 * memberService    @Transactional: ON try~catch Exception
	 * memberRepository @Transactional: ON
	 * logRepository    @Transactional: ON Exception
	 */
	@Test
	void recoverException_fail() {
		//given
		String username = "로그예외_recoverException_fail";

		//when
		assertThatThrownBy(() -> memberService.joinV2(username))
				.isInstanceOf(UnexpectedRollbackException.class);

		//then: 모든 데이터가 롤백된다.
		// 모든 논리 트랜잭션 중 하나라도 롤백되면, 물리 트랜잭션은 롤백되기 때문이다.
		// 내부 트랜잭션(LogRepository.save())에서 언체크 예외가 발생하고, 롤백되면서 rollbackOnly를 남긴다.
		// 외부 트랜잭션에서 try~catch로 내부 트랜잭션으로부터 올라온 언체크 예외를 잡아서 복구하고, 커밋을 시도한다.
		// 커밋 전에 rollbackOnly가 남겨져있는 것을 확인하고, 롤백 처리한다. => 물리 트랜잭션은 롤백된다.
		assertThat(memberRepository.find(username).isEmpty()).isTrue();
		assertThat(logRepository.find(username).isEmpty()).isTrue();
	}

	/**
	 * memberService    @Transactional: ON try~catch Exception
	 * memberRepository @Transactional: ON
	 * logRepository    @Transactional(REQUIRES_NEW): ON Exception
	 */
	@Test
	void recoverException_success() {
		//given
		String username = "로그예외_recoverException_success";

		//when
		memberService.joinV2(username);

		//then: member 저장, log 롤백
		// logRepository.save()는 새로운 외부 트랜잭션으로 시작한다. 즉 새로운 물리 트랜잭션으로 동작하는 것이다.
		// 이제 logRepository.save()는 memberService.joinV2()의 트랜잭션과는 전혀 별개의 트랜잭션으로 동작하는 것이다.
		assertThat(memberRepository.find(username).isPresent()).isTrue();
		assertThat(logRepository.find(username).isEmpty()).isTrue();
	}
}