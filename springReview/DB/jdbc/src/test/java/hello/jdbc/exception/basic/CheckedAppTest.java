package hello.jdbc.exception.basic;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.ConnectException;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class CheckedAppTest {

	@Test
	void checked() {
		Controller controller = new Controller();

		/**
		 * repository, networkClient에서 발생한 체크 예외를 잡아서 처리하지 못한 결과,
		 * service -> Controller를 거쳐 이곳 까지 올라오게 된다.
		 */
		assertThatThrownBy(() -> controller.request())
				.isInstanceOf(Exception.class);
	}

	static class Controller {
		Service service = new Service();

		/**
		 * 컨트롤러 계층은 두 개의 예외를 잡아서 처리할 수 없다.
		 * 두 예외는 체크 예외이기 때문에 throws 키워드를 통해 던진다는 것을 선언해야한다.
		 * 결국 컨트롤러 계층은 두 체크 예외에 의존하게 된다..
		 */
		public void request() throws SQLException, ConnectException {
			service.logic();
		}
	}

	static class Service {
		Repository repository = new Repository();
		NetworkClient networkClient = new NetworkClient();

		/**
		 * 서비스 계층은 두 개의 예외를 잡아서 처리할 수 없다.
		 * 두 예외는 체크 예외이기 때문에 throws 키워드를 통해 던진다는 것을 선언해야한다.
		 * 결국 서비스 계층은 두 체크 예외에 의존하게 된다..
		 */
		public void logic() throws ConnectException, SQLException {
			networkClient.call();
			repository.call();
		}
	}

	static class NetworkClient {
		public void call() throws ConnectException {
			// 체크 예외
			throw new ConnectException("Network 에러");
		}
	}

	static class Repository {
		public void call() throws SQLException {
			// 체크 예외
			throw new SQLException("DB 에러");
		}
	}
}
