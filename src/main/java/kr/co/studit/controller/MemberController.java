package kr.co.studit.controller;

import io.swagger.annotations.ApiOperation;
import kr.co.studit.dto.InvitationDto;
import kr.co.studit.dto.enums.Status;
import kr.co.studit.dto.member.*;
import kr.co.studit.dto.response.ResponseDto;
import kr.co.studit.dto.search.MemberSearchCondition;
import kr.co.studit.entity.member.Member;
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

    @ApiOperation(value = "회원 가입")
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

    @ApiOperation(value = "로그인")
    @PostMapping("/signin")
    public ResponseEntity<?> authenticate(@RequestBody SigninDto signinDto) {
        return memberService.authenticate(signinDto);
    }

    @ApiOperation(value = "닉네임 중복 체크")
    @GetMapping("/checkNickname/{nickname}")
    public ResponseEntity<?> checkNickname(@PathVariable String nickname) {
        Boolean existsByNickname = memberDataRepository.existsByNickname(nickname);
        if (existsByNickname) {
            return ResponseEntity.ok(false);
        } else {
            return ResponseEntity.ok(false);
        }
    }

    @ApiOperation(value = "이메일 중복 체크")
    @GetMapping("/signup/{email}")
    public ResponseEntity<?> checkEmail(@PathVariable String email) {
        Boolean existsByEmail = memberDataRepository.existsByEmail(email.strip());

        if (existsByEmail) {
            return ResponseEntity.ok(false);
        } else {
            return ResponseEntity.ok(true);
        }
    }

    @ApiOperation(value = "이메일 인증")
    @GetMapping("/checkEmailToken/{token}/{email}")
    public ResponseEntity<?> checkEmailToken(@PathVariable String token, @PathVariable String email) {
        Member member = memberDataRepository.findMemberByEmail(email);
        if (member.isEmailVerified()) {
            return ResponseEntity.badRequest().body("이미 인증된 회원입니다");
        }
        if (member == null) {
            return ResponseEntity.badRequest().body("유효하지 않은 이메일입니다.");
        }

        if (!member.isVaildEmailCheckToken(token)) {
            return ResponseEntity.badRequest().body("유효하지 않은 토큰 입니다.");
        }

        return memberService.compleateSignup(member);
    }

    @ApiOperation(value = "프로필 기본 정보 수정")
    @PutMapping("/profile/basic")
    public ResponseEntity<?> editBasicProfile(@AuthenticationPrincipal String email, @RequestBody BasicProfileForm basicProfileForm) {

        Member member = memberService.editBasicProfile(email, basicProfileForm);
        ResponseDto<Object> responseDto = ResponseDto.builder()
                .data(basicProfileForm)
                .status(Status.SUCCESS)
                .build();
        // 프로필 이미지 추후 수정 해야 함. 에러시 결과값등

        return ResponseEntity.ok(responseDto);
    }

    @ApiOperation(value = "비밀번호 변경")
    @PutMapping("/profile/password")
    public ResponseEntity<?> updatePassword(@AuthenticationPrincipal String email, @Valid @RequestBody UpdatePasswordForm updatePasswordForm, Errors errors) {
        if (errors.hasErrors()) {
            ResponseDto<Object> responseDto = ResponseDto.builder()
                    .message("변경하실 비밀번호가 일치 하지 않습니다.")
                    .status(Status.FALSE)
                    .build();
            return ResponseEntity.badRequest().body(responseDto);
        } else {
            return memberService.updatePassword(email, updatePasswordForm);
        }
    }

    @ApiOperation(value = "비밀 번호 재설정", notes = "비밀번호 찾기 후 비밀번호 재설정")
    @PutMapping("/password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        return memberService.resetPassword(resetPasswordDto);
    }

    @ApiOperation(value = "비밀번호 찾기 이메일 전송")
    @PostMapping("/password/send-mail")
    public ResponseEntity<?> findPassword(@RequestBody FindPasswordDto findPasswordDto) {
        String email = findPasswordDto.getEmail();
        return memberService.findPassword(email);
    }

    @ApiOperation(value = "비밀번호 찾기 메일 인증")
    @GetMapping("/checkFindPasswordToken/{token}/{email}")
    public ResponseEntity<?> checkFindPasswordToken(@PathVariable String token, @PathVariable String email) {
        Member member = memberDataRepository.findMemberByEmail(email);
        if (member == null) {
            return ResponseEntity.badRequest().body("유효하지 않은 이메일입니다.");
        }

        if (!member.isValidPasswordToken(token)) {
            return ResponseEntity.badRequest().body("유효하지 않은 토큰 입니다.");
        }

        memberService.compleateSignup(member);


        return ResponseEntity.ok().body("이메일 인증이 완료 되었습니다.");
    }

    @ApiOperation(value = "프로필 수정")
    @PutMapping("/profile")
    public ResponseEntity<?> editProfile(@AuthenticationPrincipal String email, @RequestBody ProfileForm profileForm) {
        memberService.editProfile(profileForm, email);


        return memberService.editProfile(profileForm, email);
    }

    @ApiOperation(value = "내 프로필 조회")
    @GetMapping("/profile/myProfile")
    public ResponseEntity<?> myProfile(@AuthenticationPrincipal String email) {
        // 접근 권한 설정 해야함 시큐리티 설정 할 것
        ProfileForm profileDto = memberService.getProfile(email);
        ResponseDto<Object> responseDto = ResponseDto.builder()
                .data(profileDto)
                .build();
        return ResponseEntity.ok().body(responseDto);
    }

    @ApiOperation(value = "회원 프로필 조회")
    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getProfile(@PathVariable Long id) {
        // 접근 권한 설정 해야함 시큐리티 설정 할 것
        ProfileForm profileDto = memberService.getProfile(id);
        ResponseDto<Object> responseDto = ResponseDto.builder()
                .data(profileDto)
                .build();
        return ResponseEntity.ok().body(responseDto);
    }

    @ApiOperation(value = "회원 리스트 조회")
    @PostMapping("/search")
    public ResponseEntity<?> searchMembers(@AuthenticationPrincipal String email, @RequestBody(required = false) MemberSearchCondition condition, Pageable pageable) {
        //로그인한 회원이 검색히 북마크 정보도 DTO에 담아서 리턴 해줘야한다. 로그인 유무

        Member member = memberDataRepository.findMemberByEmail(email);

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
