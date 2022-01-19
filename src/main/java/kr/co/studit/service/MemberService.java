package kr.co.studit.service;

import kr.co.studit.dto.*;
import kr.co.studit.dto.enums.InviteType;
import kr.co.studit.dto.enums.Status;
import kr.co.studit.dto.search.MemberSearchCondition;
import kr.co.studit.entity.Position;
import kr.co.studit.entity.Region;
import kr.co.studit.entity.Skill;
import kr.co.studit.entity.member.*;
import kr.co.studit.entity.enums.Role;
import kr.co.studit.entity.study.Study;
import kr.co.studit.error.ErrorResponse;
import kr.co.studit.provider.TokenProvider;
import kr.co.studit.repository.RegionDataRepository;
import kr.co.studit.repository.StudyRepository;
import kr.co.studit.repository.data.MemberRegionDataRepository;
import kr.co.studit.repository.data.StudyDataRepository;
import kr.co.studit.repository.member.MemberDataRepository;
import kr.co.studit.util.mail.EmailMessage;
import kr.co.studit.util.mail.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberDataRepository memberDataRepository;
    private final RegionDataRepository regionDataRepository;
    private final StudyRepository studyRepository;
    private final StudyDataRepository studyDataRepository;

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


    public ResponseEntity<?> authenticate(SigninDto signinDto) {

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
                    .status(Status.FALSE)
                    .build();
            return ResponseEntity
                    .badRequest()
                    .body(responseDto);
        }
    }

    public void sendSignupConfirmEmail(Member newMember) {
        Context context = new Context();
        context.setVariable("link", "http://localhost:8080/api/member/checkEmailToken/" + newMember.getEmaiCheckToken() + "/" + newMember.getEmail());
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
        member.createAt();
        member.compleateSignup();
    }

    public void editProfile(ProfileForm profileForm, String nickname) {
        Member member = memberDataRepository.findMemberByNickname(nickname);
        member.updateMember(profileForm);
        updateMemberRegion(profileForm.getRegions(), member);
        updateMemberPosition(profileForm.getPositions(), member);
        updateMemberSkill(profileForm.getSkills(), member);
        member.updateAt();
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

    public ProfileForm getProfile(String nickname) {
        Member member = memberDataRepository.findMemberByNickname(nickname);
        ProfileForm profileForm = new ProfileForm();
        profileForm.setBio(member.getBio());
        profileForm.setNickname(member.getNickname());
        profileForm.setOnOffStatus(member.getOnOffStatus());
        profileForm.setStudyType(member.getStudyType());
        profileForm.setPositions(getStream(member.getPositions()).map(memberPosition -> memberPosition.getPosition().getPositionName()).collect(toList()));
        profileForm.setRegions(getStream(member.getRegions()).map(memberRegion -> memberRegion.getRegion().getArea()).collect(toList()));
        profileForm.setSkills(getStream(member.getSkills()).map(memberSkill -> memberSkill.getSkill().getSkillName()).collect(toList()));
        return profileForm;
    }

    private <T> Stream<T> getStream(List<T> list) {
        return Optional.ofNullable(list).map(List::stream).orElseGet(Stream::empty);
    }

    public Page<SearchMemberDto> searchMemberDto(Pageable pageable) {
        Page<Member> members = memberDataRepository.searchPageMember(pageable);
        Page<SearchMemberDto> page = createSearchMemberDto(members);
        return page;
    }

    public Page<SearchMemberDto> searchMemberDto(MemberSearchCondition condition, Pageable pageable) {
        Page<Member> members = memberDataRepository.searchPageMember(condition, pageable);
        return createSearchMemberDto(members);
    }

    private Page<SearchMemberDto> createSearchMemberDto(Page<Member> members) {
        List<SearchMemberDto> searchMemberDtos = new ArrayList<>();
        for (Member member: members) {
            SearchMemberDto searchMemberDto = new SearchMemberDto();
            searchMemberDto.setMemberId(member.getId());
            searchMemberDto.setNickname(member.getNickname());
            searchMemberDto.setPositionName(getStream(member.getPositions()).map(memberPosition -> memberPosition.getPosition().getPositionName()).collect(toList()));
            searchMemberDto.setSkillName(getStream(member.getSkills()).map(memberSkill -> memberSkill.getSkill().getSkillName()).collect(toList()));
            searchMemberDto.setArea(getStream(member.getRegions()).map(memberRegion -> memberRegion.getRegion().getArea()).collect(toList()));
            searchMemberDtos.add(searchMemberDto);
        }
        Page<SearchMemberDto> page = new PageImpl<>(searchMemberDtos, members.getPageable(), members.getTotalElements());
        return page;
    }


    public ResponseEntity<?> inviteMember(InvitationDto invitationDto) {
        try {
            boolean checkInviteMember = memberDataRepository.checkInviteMember(invitationDto.getInviteMember());
            boolean checkParticipateStudy = studyRepository.checkParticipateStudy(invitationDto.getInviteMember());
            if (checkInviteMember || checkParticipateStudy) {
                return ErrorResponse.getErrorResponse(new Exception("스터디 참여 또는 스터디 초대를 하였습니다"));
            }

            ResponseDto<String> response = new ResponseDto<>();



            Member findMember = memberDataRepository.findMemberByEmail(invitationDto.getInviteMember());
            Study study = studyDataRepository.findById(invitationDto.getStudyId()).get();
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
}
