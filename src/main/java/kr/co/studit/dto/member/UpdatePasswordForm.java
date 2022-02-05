package kr.co.studit.dto.member;

import lombok.Data;

@Data
public class UpdatePasswordForm {
    private String currentPassword;
    private String newPassword;
    private String newConfirmPassword;
}
