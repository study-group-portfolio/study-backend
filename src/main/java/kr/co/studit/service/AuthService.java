package kr.co.studit.service;

import io.jsonwebtoken.Claims;
import kr.co.studit.dto.enums.Status;
import kr.co.studit.dto.response.ResponseDto;
import kr.co.studit.provider.TokenProvider;
import kr.co.studit.repository.member.MemberDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static kr.co.studit.error.ErrorResponse.getErrorResponse;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
    private final MemberDataRepository memberDataRepository;
    private final TokenProvider tokenProvider;

    public ResponseEntity<?> checkRefreshToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && !authorizationHeader.equalsIgnoreCase("null")) {
            try {
                String token = authorizationHeader.substring(7);
                Claims claims = tokenProvider.getClaims(token);
                String email = claims.getSubject();

                Map<String, String> tokens = new HashMap<>();
                String accessToken = tokenProvider.createAccessToken(memberDataRepository.findMemberByEmail(email));
                tokens.put("accessToken",accessToken);

                ResponseDto<Object> responseDto = ResponseDto.builder()
                        .status(Status.SUCCESS)
                        .data(tokens)
                        .message("access_token이 재발급 되었습니다.").build();

                return ResponseEntity.ok(responseDto);
            } catch (Exception ex) {
                log.error("is not valid refrshToken", ex);
                return getErrorResponse(ex, HttpStatus.FORBIDDEN, "다시 로그인 해주세요.");
            }
        } else {
            return getErrorResponse(new RuntimeException("유효하지 않은 토큰 입니다."), HttpStatus.FORBIDDEN, "다시 로그인 해주세요.");
        }
    }
}
