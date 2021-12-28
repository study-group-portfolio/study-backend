package kr.co.studit.service;

import kr.co.studit.dto.MemberDto;
import kr.co.studit.dto.ResponseDto;
import kr.co.studit.dto.SigninDto;
import kr.co.studit.dto.SignupDto;
import kr.co.studit.entity.Member;
import kr.co.studit.entity.enums.Role;
import kr.co.studit.provider.TokenProvider;
import kr.co.studit.repository.data.MemberDataRepository;
import kr.co.studit.util.mail.EmailMessage;
import kr.co.studit.util.mail.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberDataRepository memberDataRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;

    public Member processCreateMember(SignupDto signupDto) {
        Member newMember = createMember(signupDto);
        sendSignupConfirmEmail(newMember);
        return newMember;
    }

    public Member createMember(SignupDto signupDto) {
        Member newMember = Member.builder()
                .nickname(signupDto.getNickname())
                .email(signupDto.getEmail())
                .password(passwordEncoder.encode(signupDto.getPassword()))
                .role(Role.GUEST)
                .build();
        newMember.generateEmailCheckToken();
        return memberDataRepository.save(newMember);
    }

    public Member signin(SigninDto signinDto) {

        Member findMember = memberDataRepository.findMemberByEmail(signinDto.getEmail().strip());
        if (findMember != null && passwordEncoder.matches(signinDto.getPassword(), findMember.getPassword())) {
            return findMember;
        }
        return null;
    }


    public ResponseEntity<?>
    authenticate(SigninDto signinDto) {

        Member member = signin(signinDto);

        if (member != null) {
            String token = tokenProvider.create(member);
            MemberDto responseMemberDto = MemberDto.builder()
                    .email(member.getEmail())
                    .token(token)
                    .build();
            return ResponseEntity.ok().body(responseMemberDto);
        } else {
            ResponseDto responseDto = ResponseDto.builder()
                    .status("Login failed.")
                    .build();
            return ResponseEntity
                    .badRequest()
                    .body(responseDto);
        }
    }

    public void sendSignupConfirmEmail(Member newMember) {
        Context context = new Context();
        context.setVariable("link", "http://localhost:8080/api/member/checkEmailToken/" + newMember.getEmaiCheckToken() +"/" + newMember.getEmail());
        context.setVariable("nickname", newMember.getNickname());
        context.setVariable("linkName", "이메일 인증하기");
        context.setVariable("message", "스터딧 서비스를 이용하시려면 링크를 클릭하세요");
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(newMember.getEmail())
                .subject("STUDIT, 회원 가입 인증")
                .message(message)
                .build();

        emailService.sendMail(emailMessage);

    }

    public void compleateSignup(Member member) {
        member.setRole(Role.USER);
        member.compleateSignup();
    }
}
