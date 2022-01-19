package kr.co.studit.controller;

import kr.co.studit.dto.enums.Status;
import kr.co.studit.dto.member.*;
import kr.co.studit.dto.response.ResponseDto;
import kr.co.studit.dto.search.MemberSearchCondition;
import kr.co.studit.entity.member.Member;
import kr.co.studit.provider.TokenProvider;
import kr.co.studit.repository.member.MemberDataRepository;
import kr.co.studit.service.MemberService;
import kr.co.studit.validator.SignupValidator;
import kr.co.studit.validator.UpdatePasswordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {


    private final SignupValidator signupValidator;
    private final UpdatePasswordValidator updatePasswordValidator;
    private final MemberService memberService;
    private final MemberDataRepository memberDataRepository;
    private final TokenProvider tokenProvider;


    @InitBinder({"signupDto"})
    public void initSignupBinder(WebDataBinder webDataBinder) {

        webDataBinder.addValidators(signupValidator);
    }

    @InitBinder({"updatePasswordForm"})
    public void initUpdatePasswordBinder(WebDataBinder webDataBinder) {

        webDataBinder.addValidators(updatePasswordValidator);
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

    @GetMapping("/checkNickname/{nickname}")
    public ResponseEntity<?> checkNickname(@PathVariable String nickname) {
        Boolean existsByNickname = memberDataRepository.existsByNickname(nickname);
        if (existsByNickname) {
            return ResponseEntity.ok(false);
        } else {
            return ResponseEntity.ok(false);
        }
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

    @PutMapping("/profile/basic")
    public ResponseEntity<?> editBasicProfile(@AuthenticationPrincipal String email, @RequestBody BasicProfileForm basicProfileForm) {

        Member member = memberService.editBasicProfile(email, basicProfileForm);
        String token = tokenProvider.create(member);
        ResponseDto<Object> responseDto = ResponseDto.builder()
                .data(basicProfileForm)
                .status(Status.SUCCESS)
                .build();
        // 프로필 이미지 추후 수정 해야 함. 에러시 결과값등

        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/profile/password")
    public ResponseEntity<?> updatePassword(@AuthenticationPrincipal String email, @Valid @RequestBody UpdatePasswordForm updatePasswordForm, Errors errors) {

        return null;
    }

    @PutMapping("/profile")
    public ResponseEntity<?> editProfile(@AuthenticationPrincipal String email, @RequestBody ProfileForm profileForm) {
        memberService.editProfile(profileForm, email);


        return ResponseEntity.ok().body("업데이트");
    }

    @GetMapping("/profile/myProfile")
    public ResponseEntity<?> myProfile(@AuthenticationPrincipal String email) {
        // 접근 권한 설정 해야함 시큐리티 설정 할 것
        ProfileForm profileDto = memberService.getProfile(email);
        ResponseDto<Object> responseDto = ResponseDto.builder()
                .data(profileDto)
                .build();
        return ResponseEntity.ok().body(responseDto);
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getProfile(@PathVariable Long id) {
        // 접근 권한 설정 해야함 시큐리티 설정 할 것
        ProfileForm profileDto = memberService.getProfile(id);
        ResponseDto<Object> responseDto = ResponseDto.builder()
                .data(profileDto)
                .build();
        return ResponseEntity.ok().body(responseDto);
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchMembers(@AuthenticationPrincipal String email, @RequestBody(required = false) MemberSearchCondition condition, Pageable pageable) {
        //로그인한 회원이 검색히 북마크 정보도 DTO에 담아서 리턴 해줘야한다. 로그인 유무

        Member member = memberDataRepository.findMemberByNickname(email);

        Page<SearchMemberDto> searchMemberDtos = null;
        if (condition != null) {
            searchMemberDtos = memberService.searchMemberDto(member, condition, pageable);
        } else if (condition == null) {
            searchMemberDtos = memberService.searchMemberDto(member, pageable);
        }
        ResponseDto<Object> responseListDto = ResponseDto.builder().data(searchMemberDtos)
                .build();
        return ResponseEntity.ok().body(responseListDto);
    }

}
