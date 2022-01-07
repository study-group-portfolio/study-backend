package kr.co.studit.service;

import kr.co.studit.dto.*;
import kr.co.studit.entity.Position;
import kr.co.studit.entity.Region;
import kr.co.studit.entity.Skill;
import kr.co.studit.entity.member.Member;
import kr.co.studit.entity.enums.Role;
import kr.co.studit.entity.member.MemberPosition;
import kr.co.studit.entity.member.MemberRegion;
import kr.co.studit.entity.member.MemberSkill;
import kr.co.studit.provider.TokenProvider;
import kr.co.studit.repository.RegionDataRepository;
import kr.co.studit.repository.data.MemberRegionDataRepository;
import kr.co.studit.repository.member.MemberDataRepository;
import kr.co.studit.util.mail.EmailMessage;
import kr.co.studit.util.mail.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberDataRepository memberDataRepository;
    private final MemberRegionDataRepository memberRegionDataRepository;
    private final RegionDataRepository regionDataRepository;

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
        member.createAt();
        member.compleateSignup();
    }

    public void editProfile(ProfileForm profileForm, String nickname) {
        Member member = memberDataRepository.findMemberByNickname(nickname);
        member.updateMember(profileForm);
        if (profileForm.isUpdateRegion() == true) {
            updateMemberRegion(profileForm.getRegions(), member);
        }
        if (profileForm.isUpdatePosition() == true ) {
            updateMemberPosition(profileForm.getPositions(), member);
        }
        if (profileForm.isUpdateSkill() == true) {
        updateMemberSkill(profileForm.getSkills(), member);
        }
        member.updateAt();
    }

    private void updateMemberSkill(List<String> skills, Member member) {
        if (member.getSkills() == null || member.getSkills().isEmpty()) {
            createMemberSkill(skills, member);
        } else if (skills == null || skills.isEmpty()) {
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

    private void updateMemberPosition(List<String> positions, Member member) {

        if ( member.getPositions() == null || member.getPositions().isEmpty()) {
            createMemberPositions(positions, member);
        } else if (positions == null || positions.isEmpty()) {
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
        for (String positionName: potions) {
            Position findPositon = memberDataRepository.findPositionByPositionName(positionName);
            MemberPosition memberPosition = MemberPosition.createMemberPosition(findPositon, member);
            memberDataRepository.saveMemberPosition(memberPosition);

        }
    }

    public void updateMemberRegion(List<String> regions, Member member) {

        if (member.getRegions() == null || member.getRegions().isEmpty()) {
            createMemberRegion(regions, member);
        } else if (regions == null || regions.isEmpty()) {
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
        for (String area: regions) {
            Region region = regionDataRepository.findRegionByArea(area);
            MemberRegion newMemberRegion = MemberRegion.createMemberRegion(member, region);
            memberDataRepository.saveMemberRegion(newMemberRegion);
        }
    }

    public ProfileForm getProfile(String nickname) {
        Member member = memberDataRepository.findMemberByNickname(nickname);
        ProfileForm profileForm = memberDataRepository.findProfileFormByMemberId(member.getId());
//        ProfileForm profileForm = new ProfileForm();
//        profileForm.setBio(member.getBio());
//        profileForm.setNickname(member.getNickname());
//        profileForm.setOnOffStatus(member.getOnOffStatus());
////        List<String> positions = new ArrayList<>();
//        while (member.getPositions().listIterator().hasNext()) {
//            profileForm.getPositions().add(member.getPositions().listIterator().next().getPosition().getPositionName());
//        }
//        for (MemberPosition position: member.getPositions()) {
//            positions.add(position.getPosition().getPositionName());
//        }
//        profileForm.setPositions(positions);
//        List<String> regions = member.getRegions().listIterator().next().getRegion().getArea()
//        profileForm.setRegions();
        return profileForm;
    }
}
