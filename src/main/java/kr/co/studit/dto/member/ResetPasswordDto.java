package kr.co.studit.dto.member;

import lombok.Data;

@Data
public class ResetPasswordDto {
    private String email;
    private String password;
    private String confirmPassword;
}
