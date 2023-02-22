package hello.jdbc.connection;

import static hello.jdbc.connection.ConnectionConst.*;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Slf4j
public class ConnectionTest {

	@Test
	void driverManger() throws SQLException {
		// DiverManager - 항상 새로운 커넥션 획득
		Connection con1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
		Connection con2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
		log.info("connection={}, class={}", con1, con1.getClass());
		log.info("connection={}, class={}", con2, con2.getClass());
	}

	@Test
	void driverManagerDataSource() throws SQLException {
		// DriverManagerDataSource - 항상 새로운 커넥션 획득
		// DataSource 인터페이스의 구현체
		DataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
		useDataSource(dataSource);

	}

	@Test
	void dataSourceConnectionPool() throws SQLException, InterruptedException {
		// HikariDataSource - 커넥션 풀 사용
		// DataSource 인터페이스의 구현체
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setJdbcUrl(URL);
		dataSource.setUsername(USERNAME);
		dataSource.setPassword(PASSWORD);

		useDataSource(dataSource);
		Thread.sleep(1000); // 커넥션 생성 시간 대기
	}

	// DataSource를 통해 커넥션 획득
	private void useDataSource(DataSource dataSource) throws SQLException {
		Connection con1 = dataSource.getConnection();
		Connection con2 = dataSource.getConnection();
		log.info("connection={}, class={}", con1, con1.getClass());
		log.info("connection={}, class={}", con2, con2.getClass());
	}
}
