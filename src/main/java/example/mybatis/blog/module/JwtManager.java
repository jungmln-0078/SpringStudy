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

    public String createToken(String email, String nickname) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);

        Claims claims = Jwts.claims()
                .setSubject("access_token")
                .setIssuedAt(new Date())
                .setExpiration(new Date(cal.getTimeInMillis()));

        claims.put("email", email);
        claims.put("nickname", nickname);

        String jwt = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();

        return jwt;
    }

    public long checkClaim(String jwt) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes())
                    .parseClaimsJws(jwt).getBody();

            return memberService.getMemberByPk((String) claims.get("email"), (String) claims.get("nickname")).getMid();
        } catch (Exception e) {
            return 0L;
        }
    }

    public Claims getJwtContent(String jwt) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(jwt).getBody();
        return claims;
    }
}
