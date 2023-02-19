package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class UncheckedTest {


	@Test
	void unchecked_catch() {
		Service service = new Service();
		service.callCatch();
	}

	@Test
	void unchecked_throw() {
		Service service = new Service();
		Assertions.assertThatThrownBy(() -> service.callThrow())
				.isInstanceOf(MyUnCheckedException.class);
	}

	/**
	 * RuntimeException을 상속받은 예외는 언체크 예외가 된다.
	 */
	static class MyUnCheckedException extends RuntimeException {
		public MyUnCheckedException(String message) {
			super(message);
		}
	}

	/**
	 * Unchecked 예외는
	 * 예외를 별도로 예외를 잡거나, 던지는 코드를 작성하지 않아도 된다.
	 * 예외를 잡지 않으면 자동으로 밖으로 던져진다.
	 */
	static class Service {
		Repository repository = new Repository();

		/**
		 * 필요한 경우 예외를 잡아서 처리하면 된다.
		 */
		public void callCatch() {
			try {
				repository.call();
			} catch (MyUnCheckedException e) {
				// 예외 처리 로직
				log.info("예외 처리, message={}", e.getMessage(), e);
			}
		}

		/**
		 * 예외를 잡지 않아도 된다. 자연스럽게 상위로 던저진다.
		 * 체크 예외와 다르게 throws를 선언하지 않아도 컴파일 오류가 발생하지 않는다.
		 */
		public void callThrow() {
			repository.call();
		}
	}

	static class Repository {
		// 언체크 예외는 메소드 선언부에 throws를 선언하지 않아도, 컴파일 오류가 발생하지 않는다.
		public void call() {
			throw new MyUnCheckedException("ex");
		}
	}
}
