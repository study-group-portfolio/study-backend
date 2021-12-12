package kr.co.studit.controller;

import kr.co.studit.dto.SigninDto;
import kr.co.studit.dto.MemberDto;
import kr.co.studit.dto.SignupDto;
import kr.co.studit.entity.Member;
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
        Member newMember = memberService.createMember(signupDto);
        //TODO 회원 가입 완료시 응답 처리 , jwt 테스트,  프로필 등록
        MemberDto responseMemberDto = MemberDto.builder()
                .email(newMember.getEmail()).build();

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
}
