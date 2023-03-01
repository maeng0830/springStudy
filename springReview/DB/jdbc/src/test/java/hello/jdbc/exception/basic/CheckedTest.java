package hello.jdbc.exception.basic;

import static org.assertj.core.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class CheckedTest {

	@Test
	void checked_catch() {
		Service service = new Service();
		/**
		 * service.callCatch() 내부에서 체크 예외를 잡음 -> 이곳으로 예외가 올라오지 않음.
		 */
		service.callCatch();
	}

	@Test
	void checked_throw() {
		Service service = new Service();
		/**
		 * service.callCatch()에서 체크 예외를 호출한 곳으로 던짐 -> 이곳으로 예외가 올라옴.
		 */
		assertThatThrownBy(() -> service.callThrow())
						.isInstanceOf(MyCheckedException.class);
	}

	/**
	 * MyCheckedException은 Exception을 상속 받았다. -> 체크 예외
	 */
	static class MyCheckedException extends Exception {
		public MyCheckedException(String message) {
			super(message);
		}
	}

	static class Service {
		Repository repository = new Repository();

		/**
		 * 체크 예외를 잡아서 처리
		 */
		public void callCatch() {
			try {
				repository.call();
			} catch (MyCheckedException e) {
				// 예외 처리 로직
				log.info("예외 처리, message={}", e.getMessage(), e);
			}
		}

		/**
		 * 체크 예외를 해당 메소드를 호출한 곳으로 던짐.
		 * 체크 예외는 잡아서 처리 하지 않을 경우, 반드시 throws 키워드를 통해 던진다는 것을 선언 해줘야함.
		 * throws 미선언 시 컴파일 오류 발생!
		 */
		public void callThrow() throws MyCheckedException {
			repository.call();
		}
	}

	static class Repository {
		public void call() throws MyCheckedException {
			throw new MyCheckedException("ex");
		}
	}
}
