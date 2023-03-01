package hello.jdbc.exception.basic;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.registerCustomDateFormat;

import java.net.ConnectException;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class UncheckedAppTest {

	@Test
	void unchecked() {
		Controller controller = new Controller();

		/**
		 * repository, networkClient에서 발생한 언체크 예외를 잡아서 처리하지 못한 결과,
		 * service -> Controller를 거쳐 이곳 까지 올라오게 된다.
		 */
		assertThatThrownBy(() -> controller.request())
				.isInstanceOf(RuntimeException.class);
	}

	static class Controller {
		Service service = new Service();

		/**
		 * repository, networkClient -> 서비스를 통해 올라온 것은 언체크 예외이다.
		 * 컨트롤러 계층 또한 더 이상 throws 선언을 통해 해당 예외를 던질 필요가 없다.
		 * 예외에 대한 의존을 벗어난 것이다.
		 */
		public void request() {
			service.logic();
		}
	}

	static class Service {
		Repository repository = new Repository();
		NetworkClient networkClient = new NetworkClient();

		/**
		 * repository, networkClient에서 체크 예외를 언체크 예외로 변경해서 던진다.
		 * 서비스 계층에 올라온 것이 언체크 예외이므로, 서비스 계층은 throws 선언을 통해 던질 필요가 없다.
		 * 예외에 대한 의존을 벗어난 것이다.
		 */
		public void logic() {
			networkClient.call();
			repository.call();
		}
	}

	static class NetworkClient {
		public void call() {
			try {
				run();
			} catch (ConnectException e) {
				// 체크 예외 -> 언체크 예외
				throw new RuntimeConnectException(e);
			}
		}

		public void run() throws ConnectException {
			throw new ConnectException();
		}
	}

	static class Repository {
		public void call() {
			try {
				run();
			} catch (SQLException e) {
				// 처크 예외 -> 언체크 예외
				throw new RuntimeSQLException(e);
			}
		}

		public void run() throws SQLException {
			throw new SQLException("DB 에러");
		}
	}

	// 언체크 예외
	static class RuntimeConnectException extends RuntimeException {
		public RuntimeConnectException(Throwable cause) {
			super(cause);
		}
	}

	// 언체크 예외
	static class RuntimeSQLException extends RuntimeException {
		public RuntimeSQLException(Throwable cause) {
			super(cause);
		}
	}
}
