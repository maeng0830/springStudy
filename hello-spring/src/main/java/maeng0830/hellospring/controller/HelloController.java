package maeng0830.hellospring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {

    @GetMapping("hello")
    public String hello(Model model) {
        model.addAttribute("data", "hello!!"); // 타임리프의 매개변수와 값 지정
        // hello.html를 리턴
        // viewResolver가 화면을 찾아서 처리한다.
        // resources:templates/ 'ViewName'.html
        return "hello";
    }

    // mvc => html 형태로 응답
    @GetMapping("hello-mvc")
    public String helloMvc(@RequestParam("name") String name, Model model) {
        model.addAttribute("name", name);

        return "hello-template";
    }

    // api => 데이터를 그대로 응답
    @GetMapping("hello-string")
    @ResponseBody // http body에 응답 데이터를 직접 넣어주겠다는 의미
    public String helloString(@RequestParam("name") String name) {
        return "hello " + name;
    }

    // api, 객체가 반환될 경우 json 형태로 응답
    @GetMapping("hello-api")
    @ResponseBody
    public Hello helloApi(@RequestParam("name") String name) {
        Hello hello = new Hello();
        hello.setName(name);
        return hello;
    }

    static class Hello {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
