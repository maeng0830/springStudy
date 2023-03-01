package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

/**
 * JdbcTemplate 사용 -> 아래의 모든 기능을 JdbcTemplate로 사용할 수 있다...
 * 커넥션 조회, 동기화
 * 리소스 종료
 * 쿼리 생성 및 실행
 * 결과 바인딩
 * 스프링 예외 변환기
 */
@Slf4j
public class MemberRepositoryV5 implements MemberRepository {

	private final JdbcTemplate template;


	public MemberRepositoryV5(DataSource dataSource) {
		this.template = new JdbcTemplate(dataSource);
	}

	// 생성
	public Member save(Member member) {
		String sql = "insert into member(member_id, money) values(?, ?)";

		/**
		 * JdbcTemplate 사용
		 */
		template.update(sql, member.getMemberId(), member.getMoney());

		return member;
	}

	// 조회
	public Member findById(String memberId) {
		String sql = "select * from member where member_id = ?";

		/**
		 * JdbcTemplate 사용
		 * db 조회 데이터 -> 객체화
		 */
		return template.queryForObject(sql, memberRowMapper(), memberId);
	}

	// 수정
	public void update(String memberId, int money) {
		String sql = "update member set money=? where member_id=?";
		template.update(sql, money, memberId);
	}

	// 삭제
	public void delete(String memberId) {
		String sql = "delete from member where member_id=?";

		template.update(sql, memberId);
	}

	// db 조회 데이터 -> 객체화
	private RowMapper<Member> memberRowMapper() {
		return (rs, rowNum) -> {
			Member member = new Member();
			member.setMemberId(rs.getString("member_id"));
			member.setMoney(rs.getInt("money"));
			return member;
		};
	}
}
