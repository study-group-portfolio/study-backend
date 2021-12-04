package kr.co.studit.controller;

import kr.co.studit.dto.MemberDto;
import kr.co.studit.dto.ResponseDto;
import kr.co.studit.entity.Member;
import kr.co.studit.repository.MemberRepository;
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

    @InitBinder("memberDto")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signupValidator);
    }

    @GetMapping("/")
    public String index(){
        return "member";
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody MemberDto memberDto, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getFieldError().getDefaultMessage());
        }
        Member newMember = memberService.createMember(memberDto);
        //TODO 회원 가입 완료시 응답 처리 , 벨리데이션 체크(세부사항)
        MemberDto responseMemberDto = MemberDto.builder()
                .email(newMember.getEmail()).build();

        return ResponseEntity.ok(responseMemberDto);

    }


}
