package hello.springmvc.basic.response;

import hello.springmvc.basic.HelloData;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@Controller
// @RestController는 @Controller + @ResponseBody의 역할을 한다!
public class ResponseBodyController {

	// HttpServletResponse 객체를 통해 HTTP 메세지 바디에 데이터를 작성할 수 있다.
	@GetMapping("/response-body-string-v1")
	public void responseBodyV1(HttpServletResponse response) throws IOException {
		response.getWriter().write("ok");
	}

	// HttpEntity 또는 ResponseEntity 객체를 통해 HTTP 메세지 바디에 데이터를 작성할 수 있다.
	// HttpEntity는 HTTP 메세지의 헤더, 바디 정보를 갖고 있다.
	// ResponseEntity는 추가적으로 HTTP 응답 코드를 설정할 수 있다.
	@GetMapping("/response-body-string-v2")
	public ResponseEntity<String> responseBodyV2() {
		return new ResponseEntity<>("ok", HttpStatus.OK);
	}

	// @ResponseBody를 적용하면, HttpMessageConverter를 통해 HTTP 메세지 바디에 데이터를 작성할 수 있다.
	@ResponseBody
	@GetMapping("/response-body-string-v3")
	public String responseBodyV3() {
		return "ok";
	}

	// 프로그램 조건에 따라서 동적으로 HttpStatus를 변경할 때는 ResponseEntity를 사용하자.
	@GetMapping("/response-body-json-v1")
	public ResponseEntity<HelloData> responseBodyJsonV1() {
		HelloData helloData = new HelloData();
		helloData.setUsername("userA");
		helloData.setAge(20);

		return new ResponseEntity<>(helloData, HttpStatus.OK);
	}

	// @ResponseStatus는 HttpStatus를 동적으로 변경할 수 없다.
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@GetMapping("/response-body-json-v2")
	public HelloData responseBodyJsonV2() {
		HelloData helloData = new HelloData();
		helloData.setUsername("userA");
		helloData.setAge(20);

		return helloData;
	}
}
