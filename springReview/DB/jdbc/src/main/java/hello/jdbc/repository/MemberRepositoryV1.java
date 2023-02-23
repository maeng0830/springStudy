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
import org.springframework.jdbc.support.JdbcUtils;

/**
 * JDBC - DataSource 사용, JdbcUtils 사용
 */
@Slf4j
public class MemberRepositoryV1 {

	// DataSource 사용
	private final DataSource dataSource;

	public MemberRepositoryV1(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	// 생성
	public Member save(Member member) throws SQLException {
		String sql = "insert into member(member_id, money) values(?, ?)";
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			// DB 연결, 해당 커넥션 반환
			con = getConnection();

			// sql문 작성 완료(파라미터 대입)
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, member.getMemberId());
			pstmt.setInt(2, member.getMoney());

			// 쿼리 실행
			// 생성, 수정, 삭제는 executeUpdate()
			// resultSize는 영향 받은 row의 수를 반환한다.
			int resultSize = pstmt.executeUpdate();
			return member;
		} catch (SQLException e) {
			log.error("db error", e);
			throw e;
		} finally {
			// 리소스 정리
			close(con, pstmt, null);
		}
	}

	// 조회
	public Member findById(String memberId) throws SQLException {
		String sql = "select * from member where member_id = ?";
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			con = getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, memberId);
			// 조회는 executeQuery()를 사용한다.
			// executeQuery()는 조회 결과를 갖고 있는 ResultSet을 반환한다.
			rs = pstmt.executeQuery();

			if (rs.next()) {
				Member member = new Member();
				member.setMemberId(rs.getString("member_id"));
				member.setMoney(rs.getInt("money"));
				return member;
			} else {
				throw new NoSuchElementException();
			}
		} catch (SQLException e) {
			log.error("db error", e);
			throw e;
		} finally {
			close(con, pstmt, rs);
		}
	}

	// 수정
	public void update(String memberId, int money) throws SQLException {
		String sql = "update member set money=? where member_id=?";
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			con = getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, money);
			pstmt.setString(2, memberId);
			int resultSize = pstmt.executeUpdate();
		} catch (SQLException e) {
			log.error("db error", e);
			throw e;
		} finally {
			close(con, pstmt, null);
		}
	}

	// 삭제
	public void delete(String memberId) throws SQLException {
		String sql = "delete from member where member_id=?";
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			con = getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, memberId);
			int resultSize = pstmt.executeUpdate();
		} catch (SQLException e) {
			log.error("db error", e);
			throw e;
		} finally {
			close(con, pstmt, null);
		}
	}

	// 리소스 정리는 획득의 역순으로 한다.
	// 획득: Connection -> PreparedStatement -> ResultSet
	// 정리: ResultSet -> PreparedStatement -> Connection
	// JdbcUtils는 리소스 정리를 할 수 있는 메소드를 제공한다.
	private void close(Connection con, Statement stmt, ResultSet rs) {
		JdbcUtils.closeResultSet(rs);
		JdbcUtils.closeStatement(stmt);
		JdbcUtils.closeConnection(con);
	}

	// DataSource를 통해 DB 연결, 해당 커넥션 반환
	private Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
}
