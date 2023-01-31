package hello.springmvc.basic.response;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ResponseViewController {

	@RequestMapping("/response-view-v1")
	public ModelAndView responseViewV1() {
		ModelAndView mav = new ModelAndView("response/hello")
				.addObject("data", "hello!");
		return mav;
	}

	// 권장 방법
	@RequestMapping("/response-view-v2")
	public String responseViewV2(Model model) {
		model.addAttribute("data", "hello!");

		/**
		 * @Controller이면서,
		 * String으로 반환할 경우,
		 * 해당 String이 view의 논리이름이 된다.
		 * @ResponseBody를 사용하거나,
		 * @RestController를 사용할 경우,
		 * 해당 String은 그냥 응답 body의 값이 된다.
		 */
		return "response/hello";
	}

	@RequestMapping("/response/hello")
	public void responseViewV3(Model model) {
		model.addAttribute("data", "hello!");
	}

}
