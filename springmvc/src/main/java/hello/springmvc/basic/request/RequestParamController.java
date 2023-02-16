package hello.springmvc.basic.request;

import hello.springmvc.basic.HelloData;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class RequestParamController {

	@RequestMapping("/request-param-v1")
	public void requestParamV1(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String username = request.getParameter("username");
		int age = Integer.parseInt(request.getParameter("age"));

		log.info("username={}, age={}", username, age);
		response.getWriter().write("ok");
	}

	@ResponseBody // 반환 스트링을 그냥 응답 값으로 사용한다.
	@RequestMapping("/request-param-v2")
	public String requestParamV2(
			@RequestParam("username") String memberName,
			@RequestParam("age") int memberAge) {

		log.info("username={}, age={}", memberName, memberAge);
		return "ok";
	}

	@ResponseBody
	@RequestMapping("/request-param-v3")
	public String requestParamV3(
			@RequestParam String username,
			@RequestParam int age) {

		log.info("username={}, age={}", username, age);
		return "ok";
	}

	@ResponseBody
	@RequestMapping("/request-param-v4")
	public String requestParamV4(String username, int age) {

		log.info("username={}, age={}", username, age);
		return "ok";
	}

	@ResponseBody
	@RequestMapping("/request-param-required")
	public String requestParamRequired(
			@RequestParam(required = true) String username,
			// 값은 지정하지 않더라도, 파라미터 자체는 반드시 보내져야 한다(빈문자 라는 값이 들어오는 것).
			@RequestParam(required = false) Integer age) {

		log.info("username={}, age={}", username, age);
		return "ok";
	}

	@ResponseBody
	@RequestMapping("/request-param-default")
	public String requestParamDefault(
			@RequestParam(required = true, defaultValue = "guest") String username,
			// 파라미터를 보내지 않더라도, defaultvalue를 값으로한 파라미터를 받을 수 있다.
			@RequestParam(required = false, defaultValue = "-1") int age) {

		log.info("username={}, age={}", username, age);
		return "ok";
	}

	@ResponseBody
	@RequestMapping("/request-param-map")
	public String requestParamMap(@RequestParam Map<String, Object> paramMap) {

		log.info("username={}, age={}", paramMap.get("username"), paramMap.get("age"));
		return "ok";
	}

	@ResponseBody
	@RequestMapping("/model-attribute-v1")
	public String modelAttributeV1(@ModelAttribute HelloData helloData) {

		log.info("username={}, age={}", helloData.getUsername(), helloData.getAge());
		log.info("helloData={}", helloData);

		return "ok";
	}

	@ResponseBody
	@RequestMapping("/model-attribute-v2")
	public String modelAttributeV2(HelloData helloData) {

		log.info("username={}, age={}", helloData.getUsername(), helloData.getAge());

		return "ok";
	}
}
