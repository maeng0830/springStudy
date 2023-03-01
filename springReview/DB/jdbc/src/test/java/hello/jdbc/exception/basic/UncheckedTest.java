package hello.jdbc.exception.basic;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class UncheckedTest {

	@Test
	void checked_catch() {
		Service service = new Service();
		/**
		 * service.callCatch() 내부에서 언체크 예외를 잡음 -> 이곳으로 예외가 올라오지 않음.
		 */
		service.callCatch();
	}

	@Test
	void checked_throw() {
		Service service = new Service();
		/**
		 * service.callCatch()에서 언체크 예외를 호출한 곳으로 던짐 -> 이곳으로 예외가 올라옴.
		 */
		assertThatThrownBy(() -> service.callThrow())
						.isInstanceOf(MyCheckedException.class);
	}

	/**
	 * MyCheckedException은 RuntimeException을 상속 받았다. -> 언체크 예외
	 */
	static class MyCheckedException extends RuntimeException {
		public MyCheckedException(String message) {
			super(message);
		}
	}

	static class Service {
		Repository repository = new Repository();

		/**
		 * 언체크 예외를 잡아서 처리
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
		 * 언체크 예외를 해당 메소드를 호출한 곳으로 던짐.
		 * 언체크 예외는 잡아서 처리 하지 않을 경우, 반드시 throws 키워드를 선언할 필요가 없다. 자동으로 던진다.
		 * throws 미선언 시 컴파일 오류 발생하지도 않는다!
		 */
		public void callThrow() {
			repository.call();
		}
	}

	/**
	 * 언체크 예외는 잡지 않아도, 밖으로 던진다는 throws 선언을 필수로 할 필요가 없다.
	 * 잡지 않을 경우, throws를 생략하더라도 밖으로 던져진다.
	 */
	static class Repository {
		public void call() {
			throw new MyCheckedException("ex");
		}
	}
}
