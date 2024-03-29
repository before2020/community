package com.wyli.community.controller.advice;

import com.wyli.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private static Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler(Exception.class)
    public void handleExceptions(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("服务器异常：" + e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());
        }

        String xRequestedWith = request.getHeader("x-requested-with");
        // 异步请求，返回JSON对象
        if (xRequestedWith.equalsIgnoreCase("XMLHttpRequest")) {
            response.setContentType("application/plain;charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1, "服务器异常"));
        }
        // 普通请求，重定向到错误页面
        else {
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}
