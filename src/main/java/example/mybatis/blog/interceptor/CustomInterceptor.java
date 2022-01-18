package example.mybatis.blog.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(request.getRequestURI().contains("v3") || request.getRequestURI().contains("swagger"))) {
            LOGGER.info("===================    START    ===================");
            LOGGER.info(request.getMethod() + " " + request.getRequestURI());
            LOGGER.info(handler.toString());
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (!(request.getRequestURI().contains("v3") || request.getRequestURI().contains("swagger"))) {
            LOGGER.info("====================    END    ====================");
        }
    }
}
