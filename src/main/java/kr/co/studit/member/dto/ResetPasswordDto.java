package kr.co.studit.member.dto;

import lombok.Data;

@Data
public class ResetPasswordDto {
    private String email;
    private String password;
    private String confirmPassword;
    private String resetToken;
}
