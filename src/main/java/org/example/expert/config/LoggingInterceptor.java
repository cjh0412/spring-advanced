package org.example.expert.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.common.exception.ServerException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LocalDateTime requestTime = LocalDateTime.now();
        String userRole = (String) request.getAttribute("userRole");

        if (!"ADMIN".equals(userRole)) {
            log.warn("요청시간 : [{}], URL : [{}], userRole : [{}]", requestTime, request.getRequestURL(), userRole);
            throw new ServerException("관리자로 로그인 후 사용가능합니다.");
        }

        log.info("요청시간 : [{}], URL : [{}], userRole : [{}]", requestTime, request.getRequestURL(), userRole);
        return true;
    }
}
