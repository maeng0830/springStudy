package hello.itemservice.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

@SpringBootTest
public class MessageSourceTest {

	@Autowired
	MessageSource ms;

	@Test
	void helloMessage() {
	    // given
		String result = ms.getMessage("hello", null, null);
		// when

	    // then
		assertThat(result).isEqualTo("안녕");
	}

	@Test
	void notFoundMessageCode() {
	    // given

	    // when

	    // then
		assertThatThrownBy(() -> ms.getMessage("no_code", null, null))
				.isInstanceOf(NoSuchMessageException.class);
	}

	@Test
	void notFoundMessageCodeDefaultMessage() {
	    // given
		String result = ms.getMessage("no_code", null, "기본 메세지", null);
		// when

	    // then
		assertThat(result).isEqualTo("기본 메세지");
	}

	@Test
	void argumentMessage() {
	    // given
		String result = ms.getMessage("hello.name", new Object[]{"Spring"}, null);
		// when

	    // then
		assertThat(result).isEqualTo("안녕 Spring");
	}

	@Test
	void defaultLang() {
	    // given

	    // when

	    // then
		assertThat(ms.getMessage("hello", null, null)).isEqualTo("안녕");
		assertThat(ms.getMessage("hello", null, Locale.KOREA)).isEqualTo("안녕");
	}

	@Test
	void enLang() {
	    // given

	    // when

	    // then
		assertThat(ms.getMessage("hello", null, Locale.ENGLISH)).isEqualTo("hello");
	}
}
