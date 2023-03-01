package hello.jdbc.exception.translator;

import static hello.jdbc.connection.ConnectionConst.PASSWORD;
import static hello.jdbc.connection.ConnectionConst.URL;
import static hello.jdbc.connection.ConnectionConst.USERNAME;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

@Slf4j
public class SpringExceptionTranslatorTest {
	DataSource dataSource;

	@BeforeEach
	void init() {
		dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
	}

	@Test
	void sqlExceptionErrorCode() {
		String sql = "select bad grammar";

		try {
			Connection con = dataSource.getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.executeQuery();
		} catch (SQLException e) {
			assertThat(e.getErrorCode()).isEqualTo(42122);

			// errorCode를 직접 확인..
			int errorCode = e.getErrorCode();

			log.info("errorCode={}", errorCode);

			//org.h2.jdbc.JdbcSQLSyntaxErrorException(JDBC에 종속된 데이터 접근 예외)
			log.info("error", e);
		}
	}

	@Test
	void exceptionTranslator() {
		String sql = "select bad grammar";

		try {
			Connection con = dataSource.getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.executeQuery();
		} catch (SQLException e) {

			/**
			 * org.springframework.jdbc.support.sql-error-codes.xml -
			 */
			assertThat(e.getErrorCode()).isEqualTo(42122);

			/**
			 * 스프링이 제공하는 예외 변환기
			 */
			SQLErrorCodeSQLExceptionTranslator exTranslator = new SQLErrorCodeSQLExceptionTranslator(
					dataSource);

			/**
			 * exTranslator.translate() - 적절한 스프링 데이터 접근 계층의 예외로 변환하여 반환
			 * 스프링 데이터 접근 계층의 예외는 특정한 구현 기술(JDBC, JPA)에 종속되지 않는다.
			 * DB마다 다른 errorCode를 일일히 확인할 필요도 없다.
			 * org.springframework.jdbc.support.sql-error-codes.xml에 DB, errorCode에 따른 스프링 데이터 접근 예외가 매핑되어 있다!
			 */
			DataAccessException resultEx = exTranslator.translate("select", sql, e);

			log.info("resultEx", resultEx);

			assertThat(resultEx).isInstanceOf(BadSqlGrammarException.class);
		}
	}
}