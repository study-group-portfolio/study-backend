package kr.co.studit.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import kr.co.studit.entity.member.Member;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.springframework.security.config.Elements.JWT;

@Service
public class TokenProvider {
    private static final String SECRET_KEY = "NMA8JPct587c";

    public String createAccessToken(Member member) {

        //기한은 지금으로 부터 30분
        Date expiryDate = Date.from(
                Instant.now()
                        .plus(30, ChronoUnit.MINUTES)
//                        .plus(2, ChronoUnit.MINUTES) //TEST

        );


        //JWT 토큰 생성
        return Jwts.builder()
                //header에 들어갈 내용 및 서명을 하기 위한 SECRET_KEY
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                //payload에 들어갈 내용
                .claim("nickname", member.getNickname())
                .claim("id", member.getId())
                .claim("role", "ROLE_"+member.getRole())
                .setSubject(member.getEmail()) // sub
                .setIssuer("studit app") // iss
                .setIssuedAt(new Date()) // iat
                .setExpiration(expiryDate) // exp
                .compact();
    }

    public String createRefreshToken(Member member) {
        //기한은 지금으로 부터 7일
        Date expiryDate = Date.from(
                Instant.now()
                        .plus(7, ChronoUnit.DAYS)
//                        .plus(5, ChronoUnit.MINUTES)//TEST
        );


        //JWT 토큰 생성
        return Jwts.builder()
                //header에 들어갈 내용 및 서명을 하기 위한 SECRET_KEY
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                //payload에 들어갈 내용
                .claim("role", "ROLE_"+member.getRole())
                .setSubject(member.getEmail()) // sub
                .setIssuer("studit app") // iss
                .setIssuedAt(new Date()) // iat
                .setExpiration(expiryDate) // exp
                .compact();
    }

    public Claims getClaims(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return claims;
    }


}
