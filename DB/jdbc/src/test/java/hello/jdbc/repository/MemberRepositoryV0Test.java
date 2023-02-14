package hello.jdbc.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import hello.jdbc.domain.Member;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
class MemberRepositoryV0Test {

	MemberRepositoryV0 memberRepositoryV0 = new MemberRepositoryV0();

	@Test
	void crud() throws SQLException {
		// save
		Member member = new Member("memberV4", 10000);
		memberRepositoryV0.save(member);

		// findById
		Member findMember = memberRepositoryV0.findById(member.getMemberId());
		log.info("findMember={}", findMember);
		assertThat(findMember).isEqualTo(member);

		// update
		memberRepositoryV0.update(member.getMemberId(), 20000);
		Member updateMember = memberRepositoryV0.findById(member.getMemberId());
		assertThat(updateMember.getMemberId()).isEqualTo(member.getMemberId());
		assertThat(updateMember.getMoney()).isEqualTo(20000);

		// delete
		memberRepositoryV0.delete(member.getMemberId());
		assertThatThrownBy(() -> memberRepositoryV0.findById(member.getMemberId()))
				.isInstanceOf(NoSuchElementException.class);
	}
}