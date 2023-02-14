package hello.jdbc.connection;

import static org.assertj.core.api.Assertions.*;

import java.sql.Connection;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class DBConnectionUtilTest {

	@Test
	void connection() {
		Connection connection = DBConnectionUtil.getConnection();
		assertThat(connection).isNotNull();
	}
}
