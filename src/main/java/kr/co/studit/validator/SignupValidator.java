package kr.co.studit.validator;

import kr.co.studit.dto.MemberDto;
import kr.co.studit.repository.data.MemberDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class SignupValidator implements Validator {

    private final MemberDataRepository memberDataRepository;


    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(MemberDto.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        MemberDto memberDto = (MemberDto)target;
        if (memberDataRepository.existsByEmail(memberDto.getEmail())) {
            errors.rejectValue("email", "invalid.email", new Object[]{memberDto.getEmail()}, "이미 사용중인 이메일 입니다.");
        }

    }
}
