package example.mybatis.blog.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.lang.reflect.Method;

@Aspect
@Component
public class LogAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthAspect.class);

    @Around("within(example.mybatis.blog.controller.*)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch(joinPoint.getSignature().toShortString());

        stopWatch.start();

        Object proceed = joinPoint.proceed();

        stopWatch.stop();

        LOGGER.info("running time = {} s", stopWatch.getTotalTimeSeconds());

        return proceed;
    }
}
