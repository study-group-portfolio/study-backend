package kr.co.studit.validator;

import kr.co.studit.dto.member.UpdatePasswordForm;
import kr.co.studit.repository.member.MemberDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class UpdatePasswordValidator implements Validator {
    private final MemberDataRepository memberDataRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(UpdatePasswordForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UpdatePasswordForm updatePasswordForm = (UpdatePasswordForm) target;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String nickname = (String) principal;


    }
}
