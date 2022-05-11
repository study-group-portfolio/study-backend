package kr.co.studit.member.validator;

import kr.co.studit.member.dto.SignupDto;
import kr.co.studit.member.domain.MemberDataRepository;
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
        return clazz.isAssignableFrom(SignupDto.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SignupDto signupDto = (SignupDto) target;
        if (memberDataRepository.existsByEmail(signupDto.getEmail())) {
            errors.rejectValue("email", "invalid.email", new Object[]{signupDto.getEmail()}, "이미 사용중인 이메일 입니다.");
        }
        if (memberDataRepository.existsByNickname(signupDto.getNickname())) {
            errors.rejectValue("nickname", "ivalid.nickname", new Object[]{signupDto.getNickname()}, "이미 사용중인 닉네입 입니다.");
        }
        if ( !(signupDto.getPassword().equals(signupDto.getConfirmPassword()))) {
            errors.rejectValue("password", "invalid.password" ,"비밀번호가 일치 하지 않습니다.");
        }

    }
}
