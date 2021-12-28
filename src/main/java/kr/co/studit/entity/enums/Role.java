package kr.co.studit.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    GUEST("ROLE_GUEST", "일반 회원"),
    USER("ROLE_USER", "인증된 회원");

    private final String key;
    private final String title;

}
