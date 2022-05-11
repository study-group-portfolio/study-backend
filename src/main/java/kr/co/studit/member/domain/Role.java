package kr.co.studit.member.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    GUEST("ROLE_GUEST", "일반 회원"),
    USER("ROLE_USER", "인증된 회원"),
    ADMIN("ROLE_ADMIN", "관리자");

    private final String key;
    private final String title;

}
