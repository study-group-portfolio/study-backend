package kr.co.studit.filter;

import io.jsonwebtoken.*;
import kr.co.studit.provider.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.security.config.Elements.JWT;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals("/api/member/signin") || request.getServletPath().equals("/api/auth/refresh-token")) {
            filterChain.doFilter(request, response);
        } else {
            try {
                //요청에서 토큰 가져오기
                String token = parseBearerToken(request);

                log.info("Filter is running...");
                //토큰 검사하기 JWT이므로 인가 서버에 요청하지 않고도 검증 가능
                if (token != null && !token.equalsIgnoreCase("null")) {
                    Claims claims = tokenProvider.getClaims(token);
                    Set<GrantedAuthority> authorities = new HashSet<>();
                    authorities.add(new SimpleGrantedAuthority((String) claims.get("role")));
                    String email = claims.getSubject();
                    log.info("Authenticated member email : " + email);
                    // 인증 완료. SecurityContextHolder에 등록해야 인증된 사용자라고 생각한다.
                    AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            email, null, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                    securityContext.setAuthentication(authentication);
                    SecurityContextHolder.setContext(securityContext);
                }
            } catch (IllegalArgumentException ex) {
                logger.error("Could not set user authentication in security context", ex);
                throw new JwtException("token is not valid");
            } catch (ExpiredJwtException ex) {
                logger.error("the token is expired and not valid anymore", ex);
                throw new JwtException("access token is expired", ex);
            } catch (SignatureException ex) {
                logger.error("signature is not valid");
                throw new JwtException("signature is not valid", ex);
            }

            filterChain.doFilter(request, response);
        }
    }

    private String parseBearerToken(HttpServletRequest request) {
        // Http 요청의 헤더를 파싱해 Bearer 토큰을 리턴한다
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
