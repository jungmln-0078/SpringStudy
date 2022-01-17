package example.mybatis.blog.module;

import example.mybatis.blog.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtManager {

    private static final String secretKey = "secretKey";

    @Autowired
    private MemberService memberService;

    public String createToken(long mid) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);

        Claims claims = Jwts.claims()
                .setSubject("access_token")
                .setIssuedAt(new Date())
                .setExpiration(new Date(cal.getTimeInMillis()));

        claims.put("mid", mid);

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }

    public boolean checkClaim(String jwt) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes())
                    .parseClaimsJws(jwt).getBody();

            return memberService.getMemberById((int) claims.get("mid")) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean checkClaim(String jwt, long mid) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes())
                    .parseClaimsJws(jwt).getBody();

            return memberService.getMemberById((int) claims.get("mid")) != null && (long) claims.get("mid") == mid;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims getJwtContent(String jwt) {
        return Jwts.parser()
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(jwt).getBody();
    }
}
