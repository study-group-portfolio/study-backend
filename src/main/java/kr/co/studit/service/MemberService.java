package kr.co.studit.service;

import javassist.NotFoundException;
import kr.co.studit.config.AppProperties;
import kr.co.studit.dto.*;
import kr.co.studit.dto.enums.InviteType;
import kr.co.studit.dto.enums.Status;
import kr.co.studit.dto.member.*;
import kr.co.studit.dto.response.ResponseDto;
import kr.co.studit.dto.search.MemberSearchCondition;
import kr.co.studit.entity.Portfolio;
import kr.co.studit.entity.Position;
import kr.co.studit.entity.Region;
import kr.co.studit.entity.Skill;
import kr.co.studit.entity.member.*;
import kr.co.studit.entity.enums.Role;
import kr.co.studit.entity.study.Study;
import kr.co.studit.error.ErrorResponse;
import kr.co.studit.provider.TokenProvider;
import kr.co.studit.repository.RegionDataRepository;
import kr.co.studit.repository.study.StudyRepository;
import kr.co.studit.repository.study.StudyDataRepository;
import kr.co.studit.repository.member.MemberDataRepository;
import kr.co.studit.util.mail.EmailMessage;
import kr.co.studit.util.mail.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static kr.co.studit.error.ErrorResponse.getErrorResponse;
import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberDataRepository memberDataRepository;
    private final RegionDataRepository regionDataRepository;
    private final StudyRepository studyRepository;
    private final StudyDataRepository studyDataRepository;
    private final AppProperties appProperties;

    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;

    public ResponseEntity<?> processCreateMember(SignupDto signupDto) {
        Member newMember = createMember(signupDto);
        sendSignupConfirmEmail(newMember);

        return signin(newMember);
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

    public Member signin(SigninDto signinDto) throws NotFoundException {

        Member findMember = memberDataRepository.findMemberByEmail(signinDto.getEmail().strip());
        if (findMember != null && passwordEncoder.matches(signinDto.getPassword(), findMember.getPassword())) {
            return findMember;
        }
        throw new NotFoundException("아이디 및 비밀번호가 일치하지 않습니다.");
    }

    //이메일 인증 후 로그인 과정
    private ResponseEntity<?> signin(Member member) {
        String accessToken = tokenProvider.createAccessToken(member);
        String refreshToken = tokenProvider.createRefreshToken(member);
        SigninRes signinRes = SigninRes.builder()
                .accesToken(accessToken)
                .refreshToken(refreshToken)
                .email(member.getEmail())
                .nickname(member.getNickname())
                .build();
        ResponseDto<?> responseDto = ResponseDto.builder()
                .status(Status.SUCCESS)
                .data(signinRes).build();
        return ResponseEntity.ok(responseDto);
    }


    public ResponseEntity<?> authenticate(SigninDto signinDto) {
        try {
            Member member = signin(signinDto);
            String accessToken = tokenProvider.createAccessToken(member);
            String refreshToken = tokenProvider.createRefreshToken(member);
            SigninRes signinRes = SigninRes.builder()
                    .accesToken(accessToken)
                    .refreshToken(refreshToken)
                    .email(member.getEmail())
                    .nickname(member.getNickname())
                    .build();
            ResponseDto<Object> responseDto = ResponseDto.builder()
                    .status(Status.SUCCESS)
                    .data(signinRes).build();

            return ResponseEntity.ok().body(responseDto);
        } catch (Exception e) {
            log.info("error : {}", e.getMessage());
            return getErrorResponse(e,HttpStatus.BAD_REQUEST, e.getMessage());
        }

    }

    private void sendSignupConfirmEmail(Member newMember) {
        Context context = new Context();
        context.setVariable("host", appProperties.getBackhost() );
        context.setVariable("link", "/api/member/checkEmailToken/" + newMember.getEmaiCheckToken() + "/" + newMember.getEmail());
        context.setVariable("nickname", newMember.getNickname());
        context.setVariable("logo", "logo");
        String message = templateEngine.process("mail/signup-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(newMember.getEmail())
                .subject("STUDIT, 회원 가입 인증")
                .message(message)
                .build();

        emailService.sendMail(emailMessage);

    }

    public ResponseEntity compleateSignup(Member member) {
        member.setRole(Role.USER);
        member.compleateSignup();
        return signin(member);
    }

    public ResponseEntity<?> editProfile(ProfileForm profileForm, String email) {
        Member member = memberDataRepository.findMemberByEmail(email);
        member.updateMember(profileForm);
        updateMemberRegion(profileForm.getRegions(), member);
        updateMemberPosition(profileForm.getPositions(), member);
        updateMemberSkill(profileForm.getSkills(), member);
        updateMemberPortpolio(profileForm.getPortpolios(), member);
        ProfileForm profileFormRes = toProfleForm(member);
        ResponseDto responseDto = ResponseDto.builder()
                .data(profileFormRes)
                .status(Status.SUCCESS)
                .message("업데이트 성공")
                .build();
        return ResponseEntity.ok(responseDto);
    }

    private void updateMemberPortpolio(List<String> portpolios, Member member) {
        deletePortpolio(member);
        createPortpolios(portpolios, member);
    }

    private void createPortpolios(List<String> portpolios,Member member) {
        for (String url: portpolios) {
            Portfolio portfolio = Portfolio.builder()
                    .member(member)
                    .url(url)
                    .build();
            portfolio.addPortPolio(portfolio, member);
        }
    }

    private void deletePortpolio(Member member) {
        memberDataRepository.deletePortpolio(member);
        member.getPortfolios().clear();

    }

    public ResponseEntity<?> updatePassword(String email, UpdatePasswordForm updatePasswordForm) {
        try {


            Member member = memberDataRepository.findMemberByEmail(email);
            if (passwordEncoder.matches(updatePasswordForm.getCurrentPassword(), member.getPassword())) {
                member.updatePassword(passwordEncoder.encode(updatePasswordForm.getNewPassword()));

                ResponseDto<Object> responseDto = ResponseDto.builder()
                        .message("비밀번호 수정이 완료되었습니다.")
                        .status(Status.SUCCESS)
                        .build();
                return ResponseEntity.ok(responseDto);
            } else {
                throw new RuntimeException("입력하신 현재 비밀번호를 재확인 해주세요.");
            }
        } catch (Exception e) {
            log.error("error : {}" , e.getMessage());
            return ErrorResponse.getErrorResponse(e, HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public void updateMemberSkill(List<String> skills, Member member) {
        if (isEmpty(skills) && isEmpty(member.getSkills())) {
            return;
        }

        if (isEmpty(member.getSkills()) && !isEmpty(skills)) {
            createMemberSkill(skills, member);
        } else if (!isEmpty(member.getSkills()) && isEmpty(skills)) {
            deleteSkill(member);
        } else {
            deleteSkill(member);
            createMemberSkill(skills, member);
        }
    }

    private void deleteSkill(Member member) {
        memberDataRepository.deleteSkill(member);
        member.getSkills().clear();
    }

    private void createMemberSkill(List<String> skills, Member member) {
        List<Skill> findSkills = memberDataRepository.findSkillBySkillName(skills);
        for (Skill skill : findSkills
        ) {
            MemberSkill memberSkill = MemberSkill.createMemberSkill(skill, member);
            memberDataRepository.saveMemberSkill(memberSkill);
        }

    }

    public void updateMemberPosition(List<String> positions, Member member) {

        if (isEmpty(positions) && isEmpty(member.getPositions())) {
            return;
        }

        if (isEmpty(member.getPositions()) && !isEmpty(positions)) {
            createMemberPositions(positions, member);
        } else if (!isEmpty(member.getPositions()) && isEmpty(positions)) {
            deletePosition(member);
        } else {
            deletePosition(member);
            createMemberPositions(positions, member);
        }

    }

    private void deletePosition(Member member) {
        memberDataRepository.deletePosition(member);
        member.getPositions().clear();
    }

    private void createMemberPositions(List<String> potions, Member member) {
        for (String positionName : potions) {
            Position findPositon = memberDataRepository.findPositionByPositionName(positionName);
            MemberPosition memberPosition = MemberPosition.createMemberPosition(findPositon, member);
            memberDataRepository.saveMemberPosition(memberPosition);

        }
    }

    public void updateMemberRegion(List<String> regions, Member member) {

        if (isEmpty(regions) && isEmpty(member.getRegions())) {
            return;
        }

        if (isEmpty(member.getRegions()) && !isEmpty(regions)) {
            createMemberRegion(regions, member);
        } else if (!isEmpty(member.getRegions()) && isEmpty(regions)) {
            deleteRegion(member);
        } else {
            deleteRegion(member);
            createMemberRegion(regions, member);
        }

    }

    private void deleteRegion(Member member) {
        memberDataRepository.delateRegions(member);
        member.getRegions().clear();
    }


    private void createMemberRegion(List<String> regions, Member member) {
        for (String area : regions) {
            Region region = regionDataRepository.findRegionByArea(area);
            MemberRegion newMemberRegion = MemberRegion.createMemberRegion(member, region);
            memberDataRepository.saveMemberRegion(newMemberRegion);
        }
    }

    public ProfileForm getProfile(String email) {
        Member member = memberDataRepository.findMemberByEmail(email);
        return toProfleForm(member);
    }

    public ProfileForm getProfile(Long id) {
        Member member = memberDataRepository.findMemberById(id);
        return toProfleForm(member);
    }

    public ProfileForm toProfleForm(Member member) {
        ProfileForm profileForm = new ProfileForm();
        profileForm.setBio(member.getBio());
        profileForm.setNickname(member.getNickname());
        profileForm.setEmail(member.getEmail());
        profileForm.setOnOffStatus(member.getOnOffStatus());
        profileForm.setStudyType(member.getStudyType());
        profileForm.setPositions(getStream(member.getPositions()).map(memberPosition -> memberPosition.getPosition().getPositionName()).collect(toList()));
        profileForm.setRegions(getStream(member.getRegions()).map(memberRegion -> memberRegion.getRegion().getArea()).collect(toList()));
        profileForm.setSkills(getStream(member.getSkills()).map(memberSkill -> memberSkill.getSkill().getSkillName()).collect(toList()));
        profileForm.setPortpolios(getStream(member.getPortfolios()).map(portfolio -> portfolio.getUrl()).collect(toList()));
        return profileForm;
    }

    private <T> Stream<T> getStream(List<T> list) {
        return Optional.ofNullable(list).map(List::stream).orElseGet(Stream::empty);
    }

    public Page<SearchMemberDto> searchMemberDto(Member loginMember, Pageable pageable) {
        Page<Member> members = memberDataRepository.searchPageMember(pageable);
        Page<SearchMemberDto> page = createSearchMemberDto(loginMember, members);
        return page;
    }

    public Page<SearchMemberDto> searchMemberDto(Member loginMember, MemberSearchCondition condition, Pageable pageable) {
        Page<Member> members = memberDataRepository.searchPageMember(condition, pageable);
        return createSearchMemberDto(loginMember, members);
    }

    private Page<SearchMemberDto> createSearchMemberDto(Member loginMember, Page<Member> members) {
        List<SearchMemberDto> searchMemberDtos = new ArrayList<>();

        for (Member member : members) {
            SearchMemberDto searchMemberDto = new SearchMemberDto();
            searchMemberDto.setMemberId(member.getId());
            searchMemberDto.setNickname(member.getNickname());
            searchMemberDto.setPositionName(getStream(member.getPositions()).map(memberPosition -> memberPosition.getPosition().getPositionName()).collect(toList()));
            searchMemberDto.setSkillName(getStream(member.getSkills()).map(memberSkill -> memberSkill.getSkill().getSkillName()).collect(toList()));
            searchMemberDto.setArea(getStream(member.getRegions()).map(memberRegion -> memberRegion.getRegion().getArea()).collect(toList()));
            if (loginMember != null) {
                getStream(loginMember.getBookmarks()).filter(bookmark -> bookmark.getMarkedMember().getId() == member.getId()).forEach(bookmark -> {
                    searchMemberDto.setBookmarkId(bookmark.getId());
                    searchMemberDto.setBookmarkState(true);
                });
            }
            searchMemberDtos.add(searchMemberDto);

        }
        Page<SearchMemberDto> page = new PageImpl<>(searchMemberDtos, members.getPageable(), members.getTotalElements());
        return page;
    }


    public ResponseEntity<?> inviteMember(InvitationDto invitationDto ,String email) {
        try {
            Optional<Study> findOpStudy = studyRepository.findStudyByEmailAndId(invitationDto.getStudyId(), email);
            Study study = findOpStudy.orElseThrow(() -> new Exception("사용자가 스터디를 생성하지 않았습니다"));

            boolean checkInviteMember = memberDataRepository.checkInviteMember(invitationDto.getInviteMember());
            boolean checkParticipateStudy = studyRepository.checkParticipateStudy(invitationDto.getInviteMember());
            if (checkInviteMember || checkParticipateStudy) {
                return ErrorResponse.getErrorResponse(new Exception("스터디 참여 또는 스터디 초대를 이미 하였습니다"));
            }

            ResponseDto<String> response = new ResponseDto<>();


            Member findMember = memberDataRepository.findMemberByEmail(invitationDto.getInviteMember());
            Position position = studyRepository.findPositionByPositionName(invitationDto.getPosition());
            MemberInvitation.createStudyInvitation(study, findMember, position, invitationDto.getMessage());
            response.setStatus(Status.SUCCESS);
            response.setData("초대 성공");
            return new ResponseEntity<ResponseDto>(response, HttpStatus.OK);
        } catch (Exception e) {
            return ErrorResponse.getErrorResponse(e);
        }

    }

    public List<AlarmDto> findMemberAlarm(String email) {
        List<MemberInvitation> memberInvitationList = memberDataRepository.findMemberInvitationByEmail(email);
        List<AlarmDto> memberAlarmDtoList = alarmSetData(memberInvitationList);
        return memberAlarmDtoList;
    }

    private List<AlarmDto> alarmSetData(List<MemberInvitation> memberInvitationList) {
        List<AlarmDto> result = memberInvitationList.stream().map(mi -> {
            AlarmDto alarmDto = new AlarmDto();
            alarmDto.setType(InviteType.MEMBER);
            alarmDto.setId(mi.getId());
            alarmDto.setStudyId(mi.getStudy().getId());
            alarmDto.setEmail(mi.getStudy().getMember().getEmail());
            alarmDto.setTitle(mi.getStudy().getTitle());
            alarmDto.setPosition(mi.getPosition().getPositionName());
            alarmDto.setMessage(mi.getMessage());
            alarmDto.setCreateDate(mi.getCreatedDate());
            alarmDto.setModifiedDate(mi.getModifiedDate());
            return alarmDto;
        }).collect(toList());
        return result;
    }

    public Member editBasicProfile(String email, BasicProfileForm basicProfileForm) {
        Member member = memberDataRepository.findMemberByEmail(email);
        member.setNickname(basicProfileForm.getNickname());
        return member;
    }


    public ResponseEntity<?> findPassword(String email) {
        try {
            Member member = memberDataRepository.findMemberByEmail(email);
            if (member == null) {
                throw new NotFoundException("해당 하는 이메일을 찾지 못했습니다.");
            } else { // 메일을 계속 보내면 서버에 부담이 가니 추후에 몇분에 한번씩 보낼지 결정 하고 로직 변경 할 것.
                    member.generatePasswordFindToken();
                    sendFindPassword(member);
                ResponseDto<Object> responseDto = ResponseDto.builder()
                        .status(Status.SUCCESS)
                        .message("이메일을 확인 해주세요.")
                        .build();
                return ResponseEntity.ok(responseDto);
            }
        } catch (Exception e) {
            log.error("error : {}", e.getMessage());
            return getErrorResponse(e, HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private void sendFindPassword(Member member) {
        Context context = new Context();
        context.setVariable("host", appProperties.getBackhost() );
        context.setVariable("link", "/api/member/checkFindPasswordToken/" + member.getPasswordFindToken() + "/" + member.getEmail());
        context.setVariable("nickname", member.getNickname());
        context.setVariable("linkName", "비밀번호 재설정");
        context.setVariable("message", "새로운 비밀호를 설정하시려면 링크를 클릭하세요");
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(member.getEmail())
                .subject("STUDIT, 비밀번호 재설정")
                .message(message)
                .build();

        emailService.sendMail(emailMessage);

    }

    public ResponseEntity<?> resetPassword(ResetPasswordDto resetPasswordDto) {
        String message = null;
        if (!resetPasswordDto.getPassword().equals(resetPasswordDto.getConfirmPassword())) {
            message = "비밀번호를 재확인 해주세요.";
            return ResponseEntity.badRequest().body(message);
        } else {
            try {
                Member member = memberDataRepository.findMemberByEmail(resetPasswordDto.getEmail());
                if (member == null) {
                    throw new NotFoundException("이메일을 찾지 못했습니다");
                }
                member.updatePassword(passwordEncoder.encode(resetPasswordDto.getPassword()));
                message = "비밀번호가 재설정 되었습니다.";
                return ResponseEntity.ok().body(message);
            } catch (Exception e) {
                log.error("error : {} ", e.getMessage());
               return getErrorResponse(e, HttpStatus.BAD_REQUEST, e.getMessage());
            }

        }
    }
}
