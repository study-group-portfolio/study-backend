package kr.co.studit.dto.member;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SigninRes {
    private String accesToken;
    private String refreshToken;
    private String email;
    private String nickname;
}
