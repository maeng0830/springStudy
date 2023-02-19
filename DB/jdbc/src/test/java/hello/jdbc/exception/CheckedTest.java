package hello.jdbc.exception;

import static org.assertj.core.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
class CheckedTest {

	/**
	 * Exception을 상속받은 예외는 체크 예외가 된다.
	 */
	static class MyCheckedException extends Exception {
		public MyCheckedException(String message) {
			super(message);
		}
	}

	@Test
	void checked_catch() {
		Service service = new Service();
		service.callCatch();
	}

	@Test
	void checked_throw() {
		Service service = new Service();
		assertThatThrownBy(() -> service.callThrow())
				.isInstanceOf(MyCheckedException.class);
	}

	/**
	 * Checked 예외는
	 * 예외를 던지거나, 잡아서 처리해야한다.
	 */
	static class Service {
		Repository repository = new Repository();

		/**
		 * 예외를 잡아서 처리하는 코드
		 */
		public void callCatch() {
			try {
				repository.call();
			} catch (MyCheckedException e) {
				log.info("예외 처리, message={}", e.getMessage(), e);
			}
		}

		/**
		 * 체크 예외를 밖으로 던지는 코드
		 * 체크 예외는 예외를 잡지 않고 밖으로 던지려면 throws를 메서드에 필수로 선언해야한다.
		 * 그렇지 않을 경우 컴파일 오류가 발생한다.
		 */
		public void callThrow() throws MyCheckedException {
			repository.call();
		}
	}

	static class Repository {
		// 체크 예외를 던질 때는, 던지는 메소드 선언부에 throws를 반드시 선언해줘야한다.
		public void call() throws MyCheckedException {
			throw new MyCheckedException("ex"); // 예외 발생
		}
	}
}
