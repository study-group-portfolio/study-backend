package kr.co.studit.member.validator;

import kr.co.studit.member.dto.UpdatePasswordForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class UpdatePasswordValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(UpdatePasswordForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UpdatePasswordForm updatePasswordForm = (UpdatePasswordForm) target;
        if (!updatePasswordForm.getNewPassword().equals(updatePasswordForm.getNewConfirmPassword())) {
            errors.rejectValue("newPassword", "wrong.value", "입력한 새 패스워드가 일치하지 않습니다.");
        }


    }
}
