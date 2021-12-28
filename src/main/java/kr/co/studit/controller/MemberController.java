package kr.co.studit.controller;

import kr.co.studit.dto.ResponseDto;
import kr.co.studit.dto.SigninDto;
import kr.co.studit.dto.MemberDto;
import kr.co.studit.dto.SignupDto;
import kr.co.studit.entity.Member;
import kr.co.studit.provider.TokenProvider;
import kr.co.studit.repository.data.MemberDataRepository;
import kr.co.studit.service.MemberService;
import kr.co.studit.validator.SignupValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {


    private final SignupValidator signupValidator;
    private final MemberService memberService;
    private final MemberDataRepository memberDataRepository;
    private final TokenProvider tokenProvider;


    @InitBinder("signupDto")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signupValidator);
    }

    @GetMapping("/")
    public String index() {
        return "member";
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupDto signupDto, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body("error");
        }
        Member newMember = memberService.processCreateMember(signupDto);
        MemberDto responseMemberDto = MemberDto.builder()
                .email(newMember.getEmail())
                .nickname(newMember.getNickname())
                .build();

        return ResponseEntity.ok(responseMemberDto);

    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticate(@RequestBody SigninDto signinDto) {
        return memberService.authenticate(signinDto);
    }

    @GetMapping("/signup/{email}")
    public ResponseEntity<?> checkEmail(@PathVariable String email) {
        Boolean existsByEmail = memberDataRepository.existsByEmail(email.strip());

        if (existsByEmail) {
            return ResponseEntity.ok(false);
        } else {
            return ResponseEntity.ok(true);
        }
    }

    @GetMapping("/checkEmailToken/{token}/{email}")
    public ResponseEntity<?> checkEmailToken(@PathVariable String token, @PathVariable String email) {
        Member member = memberDataRepository.findMemberByEmail(email);
        if (member == null) {
            return ResponseEntity.badRequest().body("유효하지 않은 이메일입니다.");
        }

        if (!member.isVaildToken(token)) {
            return ResponseEntity.badRequest().body("유효하지 않은 토큰 입니다.");
        }

        memberService.compleateSignup(member);


        return ResponseEntity.ok().body("이메일 인증이 완료 되었습니다.");
    }

    @GetMapping("/sendEmailVerifyToken/{email}")
    public ResponseEntity<?> sendEmailVerifyToken(@PathVariable String email) {

        return ResponseEntity.ok("인증 코드가 발송 되었습니다.");
    }
}
