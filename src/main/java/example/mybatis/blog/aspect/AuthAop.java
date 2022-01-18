package example.mybatis.blog.aspect;

import example.mybatis.blog.module.JwtManager;
import example.mybatis.blog.module.UnAuthorizedException;
import example.mybatis.blog.service.ArticleService;
import example.mybatis.blog.service.ReplyService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Aspect
@Component
public class AuthAop {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthAop.class);

    @Autowired
    private JwtManager jwtManager;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ReplyService replyService;

    @Around("@annotation(example.mybatis.blog.aspect.Authentication)") // @Authentication 어노테이션이 붙은 메소드가 실행될 때마다 호출
    public Object auth(ProceedingJoinPoint joinPoint) throws Throwable {
        Long id = -1L;
        Long aid = -1L;
        Long rid = -1L;
        String jwt = "";
        Object dto = null;
        Object[] parameterValues = joinPoint.getArgs(); // 파라미터로 보내진 값
        String parameterName; // 파라미터 변수 이름
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod(); // 해당 메소드

        for (int i = 0; i < method.getParameterCount(); i++) {
            parameterName = method.getParameters()[i].getName(); // 파라미터 변수 이름 가져오기
            if (parameterName.equals("mid") || parameterName.equals("author")) { // 파라미터 변수 이름이 mid 또는 author 면
                id = (Long) parameterValues[i]; // 보내진 값 저장
            } else if (parameterName.equals("request")) { // request 면
                jwt = ((HttpServletRequest) parameterValues[i]).getHeader("jwt"); // request 객체의 헤더에서 jwt 가져오기
            } else if (parameterName.equals("aid")) {
                aid = (Long) parameterValues[i];
            } else if (parameterName.equals("rid")) {
                rid = (Long) parameterValues[i];
            }
        }

        if (method.getName().contains("eArticle")) {
            long author = articleService.getArticleById(aid).getAuthor();
            if (jwtManager.checkClaim(jwt, author)) {
                throw new UnAuthorizedException();
            }
        } else if (method.getName().contains("eReply")) {
            long author = replyService.getReplyById(rid).getAuthor();
            if (jwtManager.checkClaim(jwt, author)) {
                throw new UnAuthorizedException();
            }
        } else if (method.getName().contains("Article") || method.getName().contains("Reply")) {
            if (jwtManager.checkClaim(jwt)) {
                throw new UnAuthorizedException();
            }
        } else {
            if (jwtManager.checkClaim(jwt, id)) {
                throw new UnAuthorizedException();
            }
        }
        return joinPoint.proceed();
    }
}
