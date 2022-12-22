package com.maeng0830.core.web;

import com.maeng0830.core.common.MyLogger;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class LogDemoController {

    private final LogDemoService logDemoService;
    private final MyLogger myLogger; // proxyMode를 적용하여, 가짜 myLogger가 일단 주입된다.

    @RequestMapping("log-demo")
    @ResponseBody
    public String logDemo(HttpServletRequest request) throws InterruptedException {
        String requestURL = request.getRequestURI().toString();

        System.out.println("myLogger = " + myLogger.getClass());
        myLogger.setRequestURL(requestURL);

        myLogger.log("controller test"); // 가짜 myLogger의 logic()을 호출하면, 가짜 myLogger는 진짜 myLogger의 logic()을 호출한다.
        Thread.sleep(1000);
        logDemoService.logic("testId");
        return "OK";
    }
}
